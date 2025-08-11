package com.example.application.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ProjectType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // --- MODIFIED: Removed cascade options ---
    // We are only managing the relationship to existing Frameworks,
    // not creating new ones from this entity.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "project_type_frameworks",
            joinColumns = @JoinColumn(name = "project_type_id"),
            inverseJoinColumns = @JoinColumn(name = "framework_id")
    )
    private Set<Framework> frameworks = new HashSet<>();

    //<editor-fold desc="Getters, Setters, and Helper methods">
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

    public Set<Framework> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(Set<Framework> frameworks) {
        this.frameworks = frameworks;
    }

    public void addFramework(Framework framework) {
        this.frameworks.add(framework);
        framework.getProjectTypes().add(this);
    }

    public void removeFramework(Framework framework) {
        this.frameworks.remove(framework);
        framework.getProjectTypes().remove(this);
    }

    public ProjectType(String name) {
        this.name = name;
    }

    public ProjectType() {
    }

    //</editor-fold>
}