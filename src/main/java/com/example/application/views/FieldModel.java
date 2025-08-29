//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.application.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class FieldModel {
    private Long id;
    private String name;
    private String type;
    private boolean required;
    private String relationshipType;
    private String regularExpression;
    private String label;

    public FieldModel(Long id, String name, String type, boolean required, String relationshipType,String regularExpression,String label) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.required = required;
        this.relationshipType = relationshipType;
        this.regularExpression=regularExpression;
        this.label=label;
    }
}
