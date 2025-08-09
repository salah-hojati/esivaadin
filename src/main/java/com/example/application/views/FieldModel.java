//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.application.views;

public class FieldModel {
    private Long id;
    private String name;
    private String type;
    private boolean required;
    private String relationshipType;

    public FieldModel(Long id, String name, String type, boolean required, String relationshipType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.required = required;
        this.relationshipType = relationshipType;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRelationshipType() {
        return this.relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}
