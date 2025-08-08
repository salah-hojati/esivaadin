package com.example.application.service.generator;

import com.example.application.entity.Project;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component("Maven")
public class PomXmlGenerator implements BuildFileGenerator {

    private final Configuration freemarkerConfig;

    public PomXmlGenerator(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
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
            Template template = freemarkerConfig.getTemplate("generator/pom.xml.ftl");
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("project", project);
            dataModel.put("springBootVersion", "3.3.0");
            dataModel.put("vaadinVersion", "24.3.12");

            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            // In a real application, you'd have more robust error handling
            e.printStackTrace();
            return "Error generating pom.xml: " + e.getMessage();
        }
    }
}