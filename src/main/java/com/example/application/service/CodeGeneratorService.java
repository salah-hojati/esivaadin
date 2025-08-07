package com.example.application.service;

import com.example.application.entity.Entity;
import com.example.application.entity.Field;
import com.example.application.entity.Project;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CodeGeneratorService {

    /**
     * Generates Java entity classes for a project and packages them into a zip file.
     * @param project The project metadata.
     * @param entities The list of entities to generate.
     * @return A byte array representing the zipped project.
     * @throws IOException If an I/O error occurs during zipping.
     */
    public byte[] generateAndZipProject(Project project, List<Entity> entities) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            String basePath = project.getName() + "/src/main/java/com/example/domain/";

            for (Entity entity : entities) {
                String classContent = generateEntityClass(entity, entities);
                ZipEntry entry = new ZipEntry(basePath + entity.getName() + ".java");
                zos.putNextEntry(entry);
                zos.write(classContent.getBytes());
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    private String generateEntityClass(Entity entity, List<Entity> allEntities) {
        StringBuilder sb = new StringBuilder();
        String entityName = entity.getName();

        // Package and imports
        sb.append("package com.example.domain;\n\n");
        sb.append("import jakarta.persistence.*;\n");
        if (entity.getFields().stream().anyMatch(f -> "OneToMany".equals(f.getRelationshipType()))) {
            sb.append("import java.util.List;\n");
        }
        sb.append("\n");

        // Class definition
        sb.append("@Entity\n");
        sb.append("public class ").append(entityName).append(" {\n\n");

        // ID field
        sb.append("    @Id\n");
        sb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
        sb.append("    private Long id;\n\n");

        // Generate fields based on the model
        for (Field field : entity.getFields()) {
            generateField(sb, field, entityName, allEntities);
        }

        // Generate getters and setters
        sb.append("    //<editor-fold desc=\"Getters and Setters\">\n");
        sb.append("    public Long getId() { return id; }\n");
        sb.append("    public void setId(Long id) { this.id = id; }\n\n");
        for (Field field : entity.getFields()) {
            generateGetterSetter(sb, field);
        }
        sb.append("    //</editor-fold>\n");

        sb.append("}\n");
        return sb.toString();
    }

    private void generateField(StringBuilder sb, Field field, String currentEntityName, List<Entity> allEntities) {
        String fieldType = field.getType();
        String fieldName = toCamelCase(field.getName());
        String relationship = field.getRelationshipType();

        boolean isRelationship = allEntities.stream().anyMatch(e -> e.getName().equals(fieldType));

        if (isRelationship && relationship != null) {
            switch (relationship) {
                case "OneToOne":
                    sb.append("    @OneToOne(cascade = CascadeType.ALL)\n");
                    sb.append("    @JoinColumn(name = \"").append(toSnakeCase(fieldName)).append("_id\", referencedColumnName = \"id\")\n");
                    sb.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
                    break;
                case "ManyToOne":
                    sb.append("    @ManyToOne(fetch = FetchType.LAZY)\n");
                    sb.append("    @JoinColumn(name = \"").append(toSnakeCase(fieldName)).append("_id\")\n");
                    sb.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
                    break;
                case "OneToMany":
                    // NOTE: This uses a convention for `mappedBy`. The other side of the relationship
                    // must have a field named after the current entity in camelCase.
                    // e.g., if User has a OneToMany to Post, Post must have a field `private User user;`
                    String mappedBy = toCamelCase(currentEntityName);
                    sb.append("    @OneToMany(mappedBy = \"").append(mappedBy).append("\", cascade = CascadeType.ALL, orphanRemoval = true)\n");
                    sb.append("    private List<").append(fieldType).append("> ").append(fieldName).append(";\n\n");
                    break;
            }
        } else {
            // It's a primitive type
            sb.append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }
    }

    private void generateGetterSetter(StringBuilder sb, Field field) {
        String fieldType = field.getType();
        String fieldName = toCamelCase(field.getName());
        String capitalizedFieldName = capitalize(fieldName);

        if ("OneToMany".equals(field.getRelationshipType())) {
            fieldType = "List<" + fieldType + ">";
        }

        sb.append("    public ").append(fieldType).append(" get").append(capitalizedFieldName).append("() {\n");
        sb.append("        return ").append(fieldName).append(";\n");
        sb.append("    }\n\n");

        sb.append("    public void set").append(capitalizedFieldName).append("(").append(fieldType).append(" ").append(fieldName).append(") {\n");
        sb.append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n");
        sb.append("    }\n\n");
    }

    // Helper methods for consistent naming
    private String toCamelCase(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] parts = s.trim().split("[_\\s]+");
        StringBuilder camelCaseString = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(capitalize(parts[i]));
        }
        return camelCaseString.toString();
    }

    private String toSnakeCase(String s) {
        if (s == null) return null;
        return s.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}