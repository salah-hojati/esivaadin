package com.example.application.config;

import com.example.application.entity.Framework;
import com.example.application.entity.ProjectType;
import com.example.application.entity.Template;
import com.example.application.repository.FrameworkRepository;
import com.example.application.repository.ProjectTypeRepository;
import com.example.application.repository.TemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TemplateRepository templateRepository;
    private final ResourceLoader resourceLoader;
    private final ProjectTypeRepository projectTypeRepository;
    private final FrameworkRepository frameworkRepository;

    public DataSeeder(TemplateRepository templateRepository, ResourceLoader resourceLoader, ProjectTypeRepository projectTypeRepository, FrameworkRepository frameworkRepository) {
        this.templateRepository = templateRepository;
        this.resourceLoader = resourceLoader;
        this.projectTypeRepository = projectTypeRepository;
        this.frameworkRepository = frameworkRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed a default "pom.xml" template for Maven.
        seedTemplate("Maven", "*", "*", "pom.xml", "classpath:templates/generator/pom.xml.ftl");

        // Seed the new lookup tables
        seedProjectTypes();
        seedFrameworks();
    }

    private void seedProjectTypes() {
        List.of("Web", "API", "Job", "Tools").forEach(name -> {
            if (projectTypeRepository.findByName(name).isEmpty()) {
                ProjectType pt = new ProjectType();
                pt.setName(name);
                projectTypeRepository.save(pt);
            }
        });
    }

    private void seedFrameworks() {
        List.of("Vaadin", "Spring Rest", "Spring MVC", "JSF", "React", "Spring Batch", "JAX-RS", "PrimeFaces", "Struts").forEach(name -> {
            if (frameworkRepository.findByName(name).isEmpty()) {
                Framework f = new Framework();
                f.setName(name);
                frameworkRepository.save(f);
            }
        });
    }

    private void seedTemplate(String buildTool, String projectType, String framework, String templateName, String resourcePath) {
        // Check if a template with these exact characteristics already exists.
        Optional<Template> existingTemplate = templateRepository.findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(
            buildTool, projectType, framework, templateName
        );

        // If it doesn't exist, create it.
        if (existingTemplate.isEmpty()) {
            try {
                Resource resource = resourceLoader.getResource(resourcePath);
                Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                String content = FileCopyUtils.copyToString(reader);

                Template newTemplate = new Template();
                newTemplate.setBuildTool(buildTool);
                newTemplate.setProjectType(projectType);
                newTemplate.setFramework(framework);
                newTemplate.setTemplateName(templateName);
                newTemplate.setContent(content);

                templateRepository.save(newTemplate);
                System.out.println("Seeded template: " + templateName + " for " + buildTool);

            } catch (Exception e) {
                System.err.println("Failed to seed template from " + resourcePath);
                e.printStackTrace();
            }
        }
    }
}