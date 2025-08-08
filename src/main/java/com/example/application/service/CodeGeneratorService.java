package com.example.application.service;

import com.example.application.entity.Entity;
import com.example.application.entity.Project;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CodeGeneratorService {

    private final TemplateService templateService;

    public CodeGeneratorService(TemplateService templateService) {
        this.templateService = templateService;
    }

    public Map<String, String> generateProjectFiles(Project project, List<Entity> entities) {
        // All generation logic is now delegated to the TemplateService.
        return templateService.generateFilesFromDbTemplates(project, entities);
    }

    public byte[] generateAndZipProject(Project project, List<Entity> entities) throws IOException {
        Map<String, String> projectFiles = generateProjectFiles(project, entities);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Map.Entry<String, String> fileEntry : projectFiles.entrySet()) {
                ZipEntry zipEntry = new ZipEntry(fileEntry.getKey());
                zos.putNextEntry(zipEntry);
                zos.write(fileEntry.getValue().getBytes());
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }
}