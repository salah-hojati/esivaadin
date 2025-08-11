package com.example.application.repository;

import com.example.application.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    // This method is now simpler as templateName is unique
    Optional<Template> findByTemplateName(String templateName);
}