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
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public void run(String... args) throws Exception {
        seedProjectTypes();
        seedFrameworks();
        seedRelationships();
        seedTemplates(); // New method to handle template seeding
    }

    private void seedProjectTypes() {
        if (projectTypeRepository.count() == 0) {
            List.of("Web", "API", "Job", "Tools").forEach(name -> {
                projectTypeRepository.save(new ProjectType(name));
            });
            logger.info("Seeded initial ProjectTypes.");
        }
    }

    private void seedFrameworks() {
        if (frameworkRepository.count() == 0) {
            List.of("Vaadin", "Spring Rest", "Spring MVC", "JSF", "React", "Spring Batch", "JAX-RS", "PrimeFaces", "Struts")
                    .forEach(name -> {
                        frameworkRepository.save(new Framework(name));
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

    private void seedTemplates() {
        // A list of all frameworks that use Java-based templates
        List<String> allJavaFrameworks = List.of("Vaadin", "Spring Rest", "Spring MVC", "JSF", "Spring Batch", "JAX-RS", "PrimeFaces", "Struts");

        seedTemplate("pom", "pom.xml", "classpath:templates/generator/pom.xml.ftl", allJavaFrameworks);
        seedTemplate("build-gradle", "build.gradle", "classpath:templates/generator/build.gradle.ftl", allJavaFrameworks);
        seedTemplate("entity-class", "src/main/java/com/example/domain/${entity.name}.java", "classpath:templates/generator/entity.java.ftl", allJavaFrameworks);
        // Add more template seeds here as needed
    }

    private void seedTemplate(String templateName, String path, String resourcePath, List<String> frameworkNames) {
        if (templateRepository.findByTemplateName(templateName).isEmpty()) {
            try {
                Resource resource = resourceLoader.getResource(resourcePath);
                Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                String content = FileCopyUtils.copyToString(reader);

                Template newTemplate = new Template();
                newTemplate.setTemplateName(templateName);
                newTemplate.setPath(path);
                newTemplate.setContent(content);

                // Find and associate frameworks
                Set<Framework> frameworks = frameworkNames.stream()
                        .map(frameworkRepository::findByName)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());

                if (!frameworks.isEmpty()) {
                    newTemplate.setFrameworks(frameworks);
                    templateRepository.save(newTemplate);
                    logger.info("Seeded template '{}' and associated with {} frameworks.", templateName, frameworks.size());
                }

            } catch (Exception e) {
                logger.error("Failed to seed template from {}", resourcePath, e);
            }
        }
    }
}