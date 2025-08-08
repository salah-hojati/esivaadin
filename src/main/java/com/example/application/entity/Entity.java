package com.example.application.entity;

import jakarta.persistence.*;
import java.util.List;

@jakarta.persistence.Entity
public class Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entity_id")
    private List<Field> fields;

    // Add a relationship to the Project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false) // An entity MUST belong to a project
    private Project project;

    public Entity() {
    }

    public Entity(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    // Getters and Setters for all fields, including the new 'project' field
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}