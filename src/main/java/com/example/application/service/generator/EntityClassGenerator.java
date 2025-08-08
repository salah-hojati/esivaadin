package com.example.application.service.generator;

import com.example.application.entity.Entity;
import com.example.application.entity.Field;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EntityClassGenerator {

    private final Configuration freemarkerConfig;

    public EntityClassGenerator(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public String generate(Entity entity, List<Entity> allEntities) {
        try {
            Template template = freemarkerConfig.getTemplate("generator/entity.java.ftl");

            // Prepare the data model for the template
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("entity", entity);
            dataModel.put("entityCamelCaseName", toCamelCase(entity.getName()));
            dataModel.put("hasOneToMany", entity.getFields().stream().anyMatch(f -> "OneToMany".equals(f.getRelationshipType())));

            // Create a list of field models with pre-calculated names for the template
            List<FieldTemplateModel> fieldModels = entity.getFields().stream()
                    .map(field -> new FieldTemplateModel(field, allEntities))
                    .collect(Collectors.toList());
            dataModel.put("fields", fieldModels);

            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "Error generating entity " + entity.getName() + ": " + e.getMessage();
        }
    }

    /**
     * A wrapper class to provide pre-formatted names and types to the template.
     * This keeps the template logic simple.
     */
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

        // Getters are used by FreeMarker to access properties, e.g., ${field.type}
        public String getType() { return type; }
        public String getCamelCaseName() { return camelCaseName; }
        public String getCapitalizedName() { return capitalizedName; }
        public String getSnakeCaseName() { return snakeCaseName; }
        public String getRelationship() { return relationship; }
    }

    //<editor-fold desc="Static Helper Methods">
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