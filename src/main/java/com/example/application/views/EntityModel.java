package com.example.application.views;

import java.util.List;

public class EntityModel {
    private Long id; // <-- ADD THIS
    private String name;
    private List<FieldModel> fields;

    public EntityModel(String name, List<FieldModel> fields) {
        this.name = name;
        this.fields = fields;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { // <-- ADD THIS
        return id;
    }

    public void setId(Long id) { // <-- ADD THIS
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldModel> getFields() {
        return fields;
    }

    public void setFields(List<FieldModel> fields) {
        this.fields = fields;
    }
}