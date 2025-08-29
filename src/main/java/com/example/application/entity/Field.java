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
    private String relationshipType;
    private String regularExpression;
    private String label;

    public Field() {
    }

/*    public Field(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }*/

    /*public Field(String name, String type, boolean required, String relationshipType) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.relationshipType = relationshipType;
    }*/

    // New constructor including regularExpression and label
    public Field(String name, String type, boolean required, String relationshipType, String regularExpression, String label) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.relationshipType = relationshipType;
        this.regularExpression = regularExpression;
        this.label = label;
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

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setId(Long id) {
        this.id = id;
    }

    //</editor-fold>
}