package com.example.application.views;

public class FieldModel {
    private Long id; // ID from the database Field entity
    private String name;
    private String type;
    private boolean required;
    private String relationshipType; // New field

    public FieldModel(Long id, String name, String type, boolean required, String relationshipType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.required = required;
        this.relationshipType = relationshipType;
    }

    //<editor-fold desc="Getters and Setters">
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