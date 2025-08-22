package com.example.application.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    private String contentType;

    @Lob
    @Column(length = 16777215) // MEDIUMBLOB for MySQL, adjusts for other DBs
    private byte[] content;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "project_file_template",
            joinColumns = @JoinColumn(name = "project_file_id"),
            inverseJoinColumns = @JoinColumn(name = "template_id"  )
    )
    private Set<Template> templates = new HashSet<>();

    public ProjectFile(String fileName, byte[] content) {
        this.fileName=fileName;
        this.content=content;
    }

    public ProjectFile() {
    }

    //<editor-fold desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Set<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(Set<Template> templates) {
        this.templates = templates;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectFile that = (ProjectFile) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : getClass().hashCode();
    }
}