package com.example.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private boolean required;
    private String relationshipType; // New field to store relationship

    public Field() {
    }

    // Existing constructor
    public Field(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    // New constructor with relationship
    public Field(String name, String type, boolean required, String relationshipType) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.relationshipType = relationshipType;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
    //</editor-fold>
}