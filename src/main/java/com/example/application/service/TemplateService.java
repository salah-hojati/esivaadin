package com.example.application.service;

import com.example.application.entity.*;
import com.example.application.repository.ProjectTypeRepository;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final ProjectTypeRepository projectTypeRepository;
    private final Configuration freemarkerConfig;

    // Constructor simplified: FrameworkRepository is no longer needed here.
    public TemplateService(ProjectTypeRepository projectTypeRepository,
                           Configuration freemarkerConfig) {
        this.projectTypeRepository = projectTypeRepository;
        this.freemarkerConfig = freemarkerConfig;
    }

    public List<String> getApplicableFilePaths(Project project, List<Entity> entities) {
        List<Template> applicableTemplates = findApplicableTemplates(project);
        List<String> filePaths = new ArrayList<>();

        for (Template template : applicableTemplates) {
            try {
                if (template.getPath().contains("${entity.name}")) {
                    for (Entity entity : entities) {
                        Map<String, Object> dataModel = createDataModel(project, entities, entity);
                        filePaths.add(processString(template.getPath(), dataModel));
                    }
                } else {
                    Map<String, Object> dataModel = createDataModel(project, entities, null);
                    filePaths.add(processString(template.getPath(), dataModel));
                }
            } catch (IOException | TemplateException e) {
                System.err.println("Error processing path for template " + template.getTemplateName() + ": " + e.getMessage());
            }
        }
        return filePaths;
    }

    public Map<String, String> generateFilesFromDbTemplates(Project project, List<Entity> entities, Set<String> selectedPaths) {
        List<Template> applicableTemplates = findApplicableTemplates(project);
        Map<String, String> generatedFiles = new HashMap<>();

        for (Template template : applicableTemplates) {
            if (template.getPath().contains("${entity.name}")) {
                for (Entity entity : entities) {
                    processAndAddFile(generatedFiles, template, project, entities, entity, selectedPaths);
                }
            } else {
                processAndAddFile(generatedFiles, template, project, entities, null, selectedPaths);
            }
        }
        return generatedFiles;
    }

    /**
     * --- REWRITTEN: Implements the new logic for finding templates. ---
     * This method is transactional to ensure lazy-loaded collections can be accessed.
     */
    @Transactional(readOnly = true)
    private List<Template> findApplicableTemplates(Project project) {
        String projectTypeName = project.getProjectType();
        if (projectTypeName == null || projectTypeName.isBlank()) {
            return Collections.emptyList();
        }

        // 1. Find the ProjectType entity from the repository.
        Optional<ProjectType> projectTypeOpt = projectTypeRepository.findByName(projectTypeName);

        if (projectTypeOpt.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Get the set of associated frameworks from the ProjectType.
        Set<Framework> frameworks = projectTypeOpt.get().getFrameworks();

        // 3. For each framework, get its templates. Collect them into a single Set
        //    to automatically handle duplicates (in case a template is linked to multiple frameworks).
        Set<Template> allTemplates = frameworks.stream()
                .flatMap(framework -> framework.getTemplates().stream())
                .collect(Collectors.toSet());

        // 4. Return the unique templates as a list.
        return new ArrayList<>(allTemplates);
    }

    //<editor-fold desc="Unchanged Helper Methods">
    private void processAndAddFile(Map<String, String> generatedFiles, Template template, Project project, List<Entity> allEntities, Entity currentEntity, Set<String> selectedPaths) {
        try {
            Map<String, Object> dataModel = createDataModel(project, allEntities, currentEntity);
            String finalPath = processString(template.getPath(), dataModel);

            if (selectedPaths != null && !selectedPaths.contains(finalPath)) {
                return; // Skip this file
            }

            String finalContent = processString(template.getContent(), dataModel);
            generatedFiles.put(project.getName() + "/" + finalPath, finalContent);
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
        dataModel.put("springBootVersion", "3.3.0");
        dataModel.put("vaadinVersion", "24.3.12");

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
        public String getType() { return type; }
        public String getCamelCaseName() { return camelCaseName; }
        public String getCapitalizedName() { return capitalizedName; }
        public String getSnakeCaseName() { return snakeCaseName; }
        public String getRelationship() { return relationship; }
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

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    //</editor-fold>
}