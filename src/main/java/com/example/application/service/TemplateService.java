package com.example.application.service;

import com.example.application.entity.Entity;
import com.example.application.entity.Field;
import com.example.application.entity.Project;
import com.example.application.entity.Template;
import com.example.application.repository.TemplateRepository;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final Configuration freemarkerConfig;

    public TemplateService(TemplateRepository templateRepository, Configuration freemarkerConfig) {
        this.templateRepository = templateRepository;
        this.freemarkerConfig = freemarkerConfig;
    }

    public Map<String, String> generateFilesFromDbTemplates(Project project, List<Entity> entities) {
        List<Template> applicableTemplates = templateRepository.findApplicableTemplates(
                project.getBuildTool(),
                project.getProjectType(),
                project.getFramework()
        );

        Map<String, Template> finalTemplates = resolveOverrides(applicableTemplates);
        Map<String, String> generatedFiles = new HashMap<>();

        for (Template template : finalTemplates.values()) {
            // Check if this is a "per-entity" template by looking for a placeholder in its path.
            if (template.getPath().contains("${entity.name}")) {
                // Process this template once for each entity.
                for (Entity entity : entities) {
                    processAndAddFile(generatedFiles, template, project, entities, entity);
                }
            } else {
                // This is a "singleton" project-level template. Process it once.
                processAndAddFile(generatedFiles, template, project, entities, null);
            }
        }
        return generatedFiles;
    }

    private void processAndAddFile(Map<String, String> generatedFiles, Template template, Project project, List<Entity> allEntities, Entity currentEntity) {
        try {
            Map<String, Object> dataModel = createDataModel(project, allEntities, currentEntity);
            String finalPath = processString(template.getPath(), dataModel);
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

    private Map<String, Template> resolveOverrides(List<Template> templates) {
        return templates.stream()
                .collect(Collectors.toMap(
                        Template::getTemplateName,
                        t -> t,
                        (t1, t2) -> getMoreSpecific(t1, t2)
                ));
    }

    private Template getMoreSpecific(Template t1, Template t2) {
        int score1 = (!"*".equals(t1.getProjectType()) ? 2 : 0) + (!"*".equals(t1.getFramework()) ? 1 : 0);
        int score2 = (!"*".equals(t2.getProjectType()) ? 2 : 0) + (!"*".equals(t2.getFramework()) ? 1 : 0);
        return score1 >= score2 ? t1 : t2;
    }

    //<editor-fold desc="Inner classes and helpers">
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