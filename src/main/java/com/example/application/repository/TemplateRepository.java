package com.example.application.repository;

import com.example.application.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    /**
     * Finds a template by its specific characteristics.
     */
    Optional<Template> findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(
        String buildTool, String projectType, String framework, String templateName
    );
}