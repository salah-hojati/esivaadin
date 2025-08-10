package com.example.application.entity;

import jakarta.persistence.*; // Make sure you're using the correct import
import jakarta.persistence.Entity; // Make sure you're using the correct import
import java.util.Set;

@Entity
public class Framework {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable( // Example of JoinTable, adjust if needed
            name = "framework_project_type",
            joinColumns = @JoinColumn(name = "framework_id"),
            inverseJoinColumns = @JoinColumn(name = "project_type_id"))
    private Set<ProjectType> projectTypes;

    // --- Add this field ---
    private boolean appliesToAll = false;

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    public void setProjectTypes(Set<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
    }

    // --- And the getter/setter for the new field ---
    public boolean isAppliesToAll() {
        return appliesToAll;
    }

    public void setAppliesToAll(boolean appliesToAll) {
        this.appliesToAll = appliesToAll;
    }
}