package com.example.application.service;

import com.example.application.entity.*;
import com.example.application.repository.ProjectTypeRepository;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final ProjectTypeRepository projectTypeRepository;
    private final Configuration freemarkerConfig;

    public TemplateService(ProjectTypeRepository projectTypeRepository,
                           Configuration freemarkerConfig) {
        this.projectTypeRepository = projectTypeRepository;
        this.freemarkerConfig = freemarkerConfig;
    }

    public List<String> getApplicableFilePaths(Project project, List<Entity> entities) {
        List<Template> applicableTemplates = findApplicableTemplates(project);
        List<String> filePaths = new ArrayList<>();

        // Add paths from dynamic templates
        for (Template template : applicableTemplates) {
            try {
                if (template.getPath() != null && !template.getPath().isBlank() && template.getContent() != null) {
                    if (template.getPath().contains("${entity.name}")) {
                        for (Entity entity : entities) {
                            Map<String, Object> dataModel = createDataModel(project, entities, entity);
                            filePaths.add(processString(template.getPath(), dataModel));
                        }
                    } else {
                        Map<String, Object> dataModel = createDataModel(project, entities, null);
                        filePaths.add(processString(template.getPath(), dataModel));
                    }
                }
            } catch (IOException | TemplateException e) {
                System.err.println("Error processing path for template " + template.getTemplateName() + ": " + e.getMessage());
            }
        }

        // Add paths from static project files
        Map<String, ProjectFile> projectFileMap = findApplicableProjectFiles(applicableTemplates);
        filePaths.addAll(projectFileMap.keySet());

        return filePaths;
    }

    @Transactional(readOnly = true)
    public Map<String, String> generateFilesFromDbTemplates(Project project, List<Entity> entities, Set<String> selectedPaths) {
        List<Template> applicableTemplates = findApplicableTemplates(project);
        Map<String, String> generatedFiles = new HashMap<>();

        // Process dynamic templates
        for (Template template : applicableTemplates) {
            if (template.getPath() != null && !template.getPath().isBlank() && template.getContent() != null) {
                if (template.getPath().contains("${entity.name}")) {
                    for (Entity entity : entities) {
                        processAndAddFile(generatedFiles, template, project, entities, entity, selectedPaths);
                    }
                } else {
                    processAndAddFile(generatedFiles, template, project, entities, null, selectedPaths);
                }
            }
        }

        Map<String, ProjectFile> projectFileMap = findApplicableProjectFiles(applicableTemplates);
        for (Map.Entry<String, ProjectFile> entry : projectFileMap.entrySet()) {
            String pathInsideProject = entry.getKey();
            ProjectFile file = entry.getValue();
            String finalPathForZip = project.getName() + "/" + pathInsideProject;

            if (selectedPaths == null || selectedPaths.contains(pathInsideProject)) {
                generatedFiles.put(finalPathForZip, new String(file.getContent(), StandardCharsets.UTF_8));
            }
        }

        return generatedFiles;
    }

    @Transactional(readOnly = true)
    public Path generateAndSaveFilesLocally(Project project, List<Entity> entities) throws IOException {
        List<Template> applicableTemplates = findApplicableTemplates(project);
        Path projectRootPath = Paths.get(System.getProperty("user.dir"), project.getName());

        // Process and write dynamic templates
        for (Template template : applicableTemplates) {
            // A dynamic template must have both a path and content.
            if (template.getPath() != null && !template.getPath().isBlank() && template.getContent() != null) {
                if (template.getPath().contains("${entity.name}")) {
                    for (Entity entity : entities) {
                        processAndWriteSingleFile(projectRootPath, template, project, entities, entity);
                    }
                } else {
                    processAndWriteSingleFile(projectRootPath, template, project, entities, null);
                }
            }
        }

        // Process and write static project files
        Map<String, ProjectFile> projectFileMap = findApplicableProjectFiles(applicableTemplates);
        for (Map.Entry<String, ProjectFile> entry : projectFileMap.entrySet()) {
            String pathInsideProject = entry.getKey();
            ProjectFile fileContent = entry.getValue();
            Path destinationFile = projectRootPath.resolve(pathInsideProject);

            if (destinationFile.getParent() != null) {
                Files.createDirectories(destinationFile.getParent());
            }
            Files.write(destinationFile, fileContent.getContent(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }

        return projectRootPath;
    }

    /**
     * --- CORRECTED ---
     * This helper method now correctly processes and writes a single dynamic template file
     * without the flawed logic that checked for associated project files.
     */
    private void processAndWriteSingleFile(Path projectRootPath, Template template, Project project, List<Entity> allEntities, Entity currentEntity) throws IOException {
        try {
            Map<String, Object> dataModel = createDataModel(project, allEntities, currentEntity);
            String finalPath = processString(template.getPath(), dataModel);
            String finalContent = processString(template.getContent(), dataModel);
            Path destinationFile = projectRootPath.resolve(finalPath);

            if (destinationFile.getParent() != null) {
                Files.createDirectories(destinationFile.getParent());
            }
            Files.writeString(destinationFile, finalContent, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (TemplateException e) {
            throw new IOException("Failed to process template: " + template.getTemplateName(), e);
        }
    }

    @Transactional(readOnly = true)
    protected List<Template> findApplicableTemplates(Project project) {
        String projectTypeName = project.getProjectType();
        if (projectTypeName == null || projectTypeName.isBlank()) {
            return Collections.emptyList();
        }
        Optional<ProjectType> projectTypeOpt = projectTypeRepository.findByName(projectTypeName);
        if (projectTypeOpt.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Framework> frameworks = projectTypeOpt.get().getFrameworks();
        return frameworks.stream()
                .flatMap(framework -> framework.getTemplates().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private Map<String, ProjectFile> findApplicableProjectFiles(List<Template> templates) {
        Map<String, ProjectFile> fileMap = new HashMap<>();
        for (Template template : templates) {
            if (template.getProjectFiles() != null && !template.getProjectFiles().isEmpty()) {
                String basePath = template.getPath() != null ? template.getPath() : "";
                for (ProjectFile projectFile : template.getProjectFiles()) {
                    String fullPath = Paths.get(basePath, projectFile.getFileName()).toString().replace('\\', '/');
                    fileMap.putIfAbsent(fullPath, projectFile);
                }
            }
        }
        return fileMap;
    }

    private void processAndAddFile(Map<String, String> generatedFiles, Template template, Project project, List<Entity> allEntities, Entity currentEntity, Set<String> selectedPaths) {
        try {
            Map<String, Object> dataModel = createDataModel(project, allEntities, currentEntity);
            String finalPath = processString(template.getPath(), dataModel);
            String finalPathForZip = project.getName() + "/" + finalPath;

            if (selectedPaths != null && !selectedPaths.contains(finalPath)) {
                return;
            }

            String finalContent = processString(template.getContent(), dataModel);
            generatedFiles.put(finalPathForZip, finalContent);
        } catch (IOException | TemplateException e) {
            System.err.println("Error processing template " + template.getTemplateName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String processString(String stringToProcess, Map<String, Object> dataModel) throws IOException, TemplateException {
        freemarker.template.Template fmTemplate = new freemarker.template.Template("string", stringToProcess, freemarkerConfig);
        StringWriter writer = new StringWriter();
        fmTemplate.process(dataModel, writer);
        return writer.toString();
    }

    private Map<String, Object> createDataModel(Project project, List<Entity> allEntities, Entity currentEntity) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("project", project);
        dataModel.put("entities", allEntities);

        // Load configuration properties from the project entity
        project.getConfigurationProperties().forEach(prop ->
                dataModel.put(toCamelCase(prop.getPropertyKey()), prop.getPropertyValue())
        );
        projectTypeRepository.findByName(project.getProjectType()).ifPresent(pt -> {
            Set<String> frameworkNames = pt.getFrameworks().stream()
                    .map(Framework::getName)
                    .collect(Collectors.toSet());
            dataModel.put("frameworks", frameworkNames);
        });

        if (currentEntity != null) {
            dataModel.put("entity", currentEntity);
            dataModel.put("entityCamelCaseName", toCamelCase(currentEntity.getName()));
            dataModel.put("hasOneToMany", currentEntity.getFields().stream().anyMatch(f -> "OneToMany".equals(f.getRelationshipType())));
            List<FieldTemplateModel> fieldModels = currentEntity.getFields().stream()
                    .map(field -> new FieldTemplateModel(field, allEntities))
                    .collect(Collectors.toList());
            dataModel.put("fields", fieldModels);
        }
        return dataModel;
    }

    //<editor-fold desc="Unchanged Helper Methods">
    public static class FieldTemplateModel {
        private final String type;
        private final String camelCaseName;
        private final String capitalizedName;
        private final String snakeCaseName;
        private final String relationship;

        public FieldTemplateModel(Field field, List<Entity> allEntities) {
            this.camelCaseName = toCamelCase(field.getName());
            this.capitalizedName = capitalize(this.camelCaseName);
            this.snakeCaseName = toSnakeCase(this.camelCaseName);
            this.relationship = allEntities.stream().anyMatch(e -> e.getName().equals(field.getType())) ? field.getRelationshipType() : null;

            if ("OneToMany".equals(this.relationship)) {
                this.type = "List<" + field.getType() + ">";
            } else {
                this.type = field.getType();
            }
        }

        public String getType() {
            return type;
        }

        public String getCamelCaseName() {
            return camelCaseName;
        }

        public String getCapitalizedName() {
            return capitalizedName;
        }

        public String getSnakeCaseName() {
            return snakeCaseName;
        }

        public String getRelationship() {
            return relationship;
        }
    }

    private static String toCamelCase(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] parts = s.trim().split("[_\\s]+");
        StringBuilder camelCaseString = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(capitalize(parts[i]));
        }
        return camelCaseString.toString();
    }

    private static String toSnakeCase(String s) {
        if (s == null) return null;
        return s.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    //</editor-fold>
}