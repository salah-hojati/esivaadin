package com.example.application.controller;

import com.example.application.entity.Entity;
import com.example.application.entity.Project;
import com.example.application.repository.EntityRepository;
import com.example.application.repository.ProjectRepository;
import com.example.application.service.CodeGeneratorService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/download")
// For simplicity, this allows anyone to access the endpoint.
// In a real application, you would replace this with Spring Security annotations.
@AnonymousAllowed
public class ProjectDownloadController {

    private final ProjectRepository projectRepository;
    private final EntityRepository entityRepository;
    private final CodeGeneratorService codeGeneratorService;

    public ProjectDownloadController(ProjectRepository projectRepository,
                                     EntityRepository entityRepository,
                                     CodeGeneratorService codeGeneratorService) {
        this.projectRepository = projectRepository;
        this.entityRepository = entityRepository;
        this.codeGeneratorService = codeGeneratorService;
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<byte[]> downloadProject(@PathVariable Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Project project = projectOpt.get();

        List<Entity> entities = entityRepository.findByProjectIdWithFields(project.getId());
        if (entities.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        try {
            byte[] zipData = codeGeneratorService.generateAndZipProject(project, entities);
            String fileName = project.getName() + ".zip";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // This header tells the browser to treat the response as a file download
            headers.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(zipData, headers, HttpStatus.OK);

        } catch (IOException e) {
            // It's good practice to log the exception
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}