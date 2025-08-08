package com.example.application.service.generator;

import com.example.application.entity.Project;
import com.example.application.repository.TemplateRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("Maven")
public class PomXmlGenerator implements BuildFileGenerator {

    private final Configuration freemarkerConfig;
    private final TemplateRepository templateRepository;

    // Inject the new repository along with the FreeMarker configuration
    public PomXmlGenerator(Configuration freemarkerConfig, TemplateRepository templateRepository) {
        this.freemarkerConfig = freemarkerConfig;
        this.templateRepository = templateRepository;
    }

    @Override
    public String getBuildToolName() {
        return "Maven";
    }

    @Override
    public String getFileName() {
        return "pom.xml";
    }

    @Override
    public String generate(Project project) {
        try {
            // 1. Fetch the template string from the database.
            String templateContent = findTemplateForProject(project);

            // 2. Create a new FreeMarker template object directly from the string.
            // The first argument is a name for logging/error-reporting purposes.
            Template template = new Template("pom.xml-from-db", templateContent, freemarkerConfig);

            // 3. Prepare the data model, same as before.
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("project", project);
            dataModel.put("springBootVersion", "3.3.0");
            dataModel.put("vaadinVersion", "24.3.12");

            // 4. Process the template.
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "Error generating pom.xml: " + e.getMessage();
        }
    }

    private String findTemplateForProject(Project project) {
        String buildTool = project.getBuildTool();
        String projectType = project.getProjectType();
        String framework = project.getFramework();
        String templateName = getFileName();

        // Strategy: Look for the most specific template first, then fall back to more generic ones.
        // 1. Look for a template matching Project Type and Framework.
        Optional<com.example.application.entity.Template> template = templateRepository
                .findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(buildTool, projectType, framework, templateName);
        if (template.isPresent()) {
            return template.get().getContent();
        }

        // 2. Fallback: Look for a template matching only Project Type (wildcard framework).
        template = templateRepository
                .findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(buildTool, projectType, "*", templateName);
        if (template.isPresent()) {
            return template.get().getContent();
        }

        // 3. Fallback: Look for the default template for the build tool (wildcard type and framework).
        template = templateRepository
                .findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(buildTool, "*", "*", templateName);
        if (template.isPresent()) {
            return template.get().getContent();
        }

        // If no template is found at all, throw an exception.
        throw new IllegalStateException("No suitable 'pom.xml' template found in the database for the project configuration.");
    }
}