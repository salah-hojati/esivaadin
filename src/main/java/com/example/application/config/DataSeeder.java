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
import org.springframework.transaction.annotation.Transactional; // 1. Add this import
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional // 2. Add this annotation
    public void run(String... args) throws Exception {
        seedProjectTypes();
        seedFrameworks();
        seedRelationships(); // This will now run inside a transaction

        seedTemplate("Maven", "*", "*", "pom", "pom.xml", "classpath:templates/generator/pom.xml.ftl");
        seedTemplate("Gradle", "*", "*", "build-gradle", "build.gradle", "classpath:templates/generator/build.gradle.ftl");
        seedTemplate("*", "*", "*", "entity-class", "src/main/java/com/example/domain/${entity.name}.java", "classpath:templates/generator/entity.java.ftl");
    }

    private void seedProjectTypes() {
        if (projectTypeRepository.count() == 0) {
            List.of("Web", "API", "Job", "Tools").forEach(name -> {
                ProjectType pt = new ProjectType();
                pt.setName(name);
                projectTypeRepository.save(pt);
            });
            logger.info("Seeded initial ProjectTypes.");
        }
    }

    private void seedFrameworks() {
        // This condition correctly ensures this code only runs ONCE on a fresh database.
        if (frameworkRepository.count() == 0) {

            // We can directly iterate over our default list of names.
            // This is the sole purpose of the seeder: to provide initial data.
            List.of("Vaadin", "Spring Rest", "Spring MVC", "JSF", "React", "Spring Batch", "JAX-RS", "PrimeFaces", "Struts")
                    .forEach(name -> {
                        Framework f = new Framework();
                        f.setName(name);
                        frameworkRepository.save(f);
                    });

            logger.info("Seeded initial Frameworks.");
        }
    }

    private void seedRelationships() {
        Optional<ProjectType> webOpt = projectTypeRepository.findByName("Web");
        if (webOpt.isPresent() && webOpt.get().getFrameworks().isEmpty()) {
            logger.info("Seeding ProjectType-Framework relationships...");
            associate("Web", List.of("Vaadin", "Spring MVC", "JSF", "React", "PrimeFaces", "Struts"));
            associate("API", List.of("Spring Rest", "JAX-RS"));
            associate("Job", List.of("Spring Batch"));
            logger.info("Relationships seeded.");
        }
    }

    private void associate(String projectTypeName, List<String> frameworkNames) {
        projectTypeRepository.findByName(projectTypeName).ifPresent(projectType -> {
            frameworkNames.forEach(frameworkName ->
                    frameworkRepository.findByName(frameworkName).ifPresent(projectType::addFramework)
            );
            projectTypeRepository.save(projectType);
        });
    }

    private void seedTemplate(String buildTool, String projectType, String framework, String templateName, String path, String resourcePath) {
        if (templateRepository.findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(buildTool, projectType, framework, templateName).isEmpty()) {
            try {
                Resource resource = resourceLoader.getResource(resourcePath);
                Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                String content = FileCopyUtils.copyToString(reader);
                Template newTemplate = new Template();
                newTemplate.setBuildTool(buildTool);
                newTemplate.setProjectType(projectType);
                newTemplate.setFramework(framework);
                newTemplate.setTemplateName(templateName);
                newTemplate.setPath(path);
                newTemplate.setContent(content);
                templateRepository.save(newTemplate);
            } catch (Exception e) {
                logger.error("Failed to seed template from {}", resourcePath, e);
            }
        }
    }
}