package com.example.application.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

/**
 * Represents a code generation template stored in the database.
 */
@Entity
// Add indexes for faster lookups on the fields we will be querying.
@Table(name = "project_templates", indexes = {
    @Index(name = "idx_template_lookup", columnList = "buildTool, projectType, framework, templateName")
})
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g., "Maven", "Gradle"
    @Column(nullable = false)
    private String buildTool;

    // e.g., "Web", "API", or "*" for a wildcard/default
    @Column(nullable = false)
    private String projectType;

    // e.g., "Vaadin", "Spring Rest", or "*" for a wildcard/default
    @Column(nullable = false)
    private String framework;

    // The name of the file to be generated, e.g., "pom.xml"
    @Column(nullable = false)
    private String templateName;

    // Use @Lob to allow for very large template strings.
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //<editor-fold desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    //</editor-fold>
}