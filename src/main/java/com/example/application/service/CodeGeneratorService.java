package com.example.application.service;

import com.example.application.entity.Entity;
import com.example.application.entity.Project;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CodeGeneratorService {

    private final TemplateService templateService;

    public CodeGeneratorService(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Delegates the call to the template service to get a preview of file paths.
     * This improves encapsulation, as the UI doesn't need to know about TemplateService.
     */
    public List<String> getApplicableFilePaths(Project project, List<Entity> entities) {
        return templateService.getApplicableFilePaths(project, entities);
    }

    public Map<String, String> generateProjectFiles(Project project, List<Entity> entities, Set<String> selectedPaths) {
        return templateService.generateFilesFromDbTemplates(project, entities, selectedPaths);
    }

    public Map<String, String> generateProjectFiles(Project project, List<Entity> entities) {
        return templateService.generateFilesFromDbTemplates(project, entities, null);
    }

    public byte[] generateAndZipProject(Project project, List<Entity> entities, Set<String> selectedPaths) throws IOException {
        Map<String, String> projectFiles = generateProjectFiles(project, entities, selectedPaths);

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

    public byte[] generateAndZipProject(Project project, List<Entity> entities) throws IOException {
        return generateAndZipProject(project, entities, null);
    }
}