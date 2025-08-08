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

@Component("Gradle")
public class BuildGradleGenerator implements BuildFileGenerator {

    private final Configuration freemarkerConfig;

    public BuildGradleGenerator(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public String getBuildToolName() {
        return "Gradle";
    }

    @Override
    public String getFileName() {
        return "build.gradle";
    }

    @Override
    public String generate(Project project) {
        try {
            Template template = freemarkerConfig.getTemplate("generator/build.gradle.ftl");
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("project", project);
            dataModel.put("springBootVersion", "3.3.0");
            dataModel.put("vaadinVersion", "24.3.12");

            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "Error generating build.gradle: " + e.getMessage();
        }
    }
}