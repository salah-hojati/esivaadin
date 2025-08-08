package com.example.domain;

import jakarta.persistence.*;
<#if hasOneToMany>
    import java.util.List;
</#if>

@Entity
public class ${entity.name} {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

<#-- Loop through each field provided in the data model -->
<#list fields as field>
    <#if field.relationship??>
        <#if field.relationship == "OneToOne">
            @OneToOne(cascade = CascadeType.ALL)
            @JoinColumn(name = "${field.snakeCaseName}_id", referencedColumnName = "id")
        <#elseif field.relationship == "ManyToOne">
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "${field.snakeCaseName}_id")
        <#elseif field.relationship == "OneToMany">
            @OneToMany(mappedBy = "${entityCamelCaseName}", cascade = CascadeType.ALL, orphanRemoval = true)
        </#if>
        private ${field.type} ${field.camelCaseName};

    <#else>
        private ${field.type} ${field.camelCaseName};

    </#if>
</#list>
//<editor-fold desc="Getters and Setters">
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    <#list fields as field>
        public ${field.type} get${field.capitalizedName}() {
        return ${field.camelCaseName};
        }

        public void set${field.capitalizedName}(${field.type} ${field.camelCaseName}) {
        this.${field.camelCaseName} = ${field.camelCaseName};
        }

    </#list>
    //</editor-fold>
}