package com.example.application.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@jakarta.persistence.Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String projectType;

    // The 'framework' field is no longer needed and has been removed.

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Entity> entities = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ConfigurationProperty> configurationProperties = new ArrayList<>();

    public Project() {
    }

    // Constructor updated to remove the framework parameter.
    public Project(String name, String projectType) {
        this.name = name;
        this.projectType = projectType;
    }

    //<editor-fold desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<ConfigurationProperty> getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(List<ConfigurationProperty> configurationProperties) {
        this.configurationProperties = configurationProperties;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}