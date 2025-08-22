package com.example.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String templateName;

    private String path;

    @Column(length = 10000)
    private String content;

    // --- NEW: Inverse side of the relationship to Framework ---
    @ManyToMany(mappedBy = "templates")
    @JsonIgnore
    private Set<Framework> frameworks = new HashSet<>();

    // --- NEW: Relationship to ProjectFile ---

    // --- CORRECTED: Template is now the owner of the relationship ---
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "template_project_file",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "project_file_id")
    )
    private Set<ProjectFile> projectFiles = new HashSet<>();
    //<editor-fold desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Framework> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(Set<Framework> frameworks) {
        this.frameworks = frameworks;
    }

    public Set<ProjectFile> getProjectFiles() {
        return projectFiles;
    }

    public void setProjectFiles(Set<ProjectFile> projectFiles) {
        this.projectFiles = projectFiles;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Template template = (Template) o;
        return id != null && id.equals(template.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : getClass().hashCode();
    }
}