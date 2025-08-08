package com.example.application.config;

import com.example.application.entity.Framework;
import com.example.application.entity.ProjectType;
import com.example.application.entity.Template;
import com.example.application.repository.FrameworkRepository;
import com.example.application.repository.ProjectTypeRepository;
import com.example.application.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
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
        // Seed project-level templates
        seedTemplate("Maven", "*", "*", "pom", "pom.xml", "classpath:templates/generator/pom.xml.ftl");
        seedTemplate("Gradle", "*", "*", "build-gradle", "build.gradle", "classpath:templates/generator/build.gradle.ftl");

        // Seed the entity-level template. This makes EntityClassGenerator obsolete.
        seedTemplate("*", "*", "*", "entity-class", "src/main/java/com/example/domain/${entity.name}.java", "classpath:templates/generator/entity.java.ftl");

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
                logger.info("Seeded ProjectType: {}", name); // Add logging
            }
        });
        logger.info("All ProjectTypes seeded.");
    }

    private void seedFrameworks() {
        List.of("Vaadin", "Spring Rest", "Spring MVC", "JSF", "React", "Spring Batch", "JAX-RS", "PrimeFaces", "Struts").forEach(name -> {
            if (frameworkRepository.findByName(name).isEmpty()) {
                Framework f = new Framework();
                f.setName(name);
                frameworkRepository.save(f);
                logger.info("Seeded Framework: {}", name); // Add logging
            }
        });
        logger.info("All Frameworks seeded.");
    }

    private void seedTemplate(String buildTool, String projectType, String framework, String templateName, String path, String resourcePath) {
        Optional<Template> existingTemplate = templateRepository.findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(
                buildTool, projectType, framework, templateName
        );

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
                newTemplate.setPath(path); // Set the new path
                newTemplate.setContent(content);

                templateRepository.save(newTemplate);
                logger.info("Seeded Template: {}", templateName);
            } catch (Exception e) {
                logger.error("Failed to seed template from {}", resourcePath, e);
            }
        }
    }
}