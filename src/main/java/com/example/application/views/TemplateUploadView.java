package com.example.application.views;

import com.example.application.entity.Template;
import com.example.application.repository.TemplateRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Route("admin/upload-template")
@PageTitle("Upload Template")
@PermitAll
public class TemplateUploadView extends AbstractAdminView {

    private final TemplateRepository templateRepository;

    private final TextField templateName = new TextField("Template Name");
    private final TextField path = new TextField("Path");
    private final Upload upload = new Upload();
    private final Button saveButton = new Button("Save Template");

    private String fileContent;
    private String originalFileName;

    public TemplateUploadView(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        add(new H1("Upload New Template"));

        MemoryBuffer buffer = new MemoryBuffer();
        upload.setReceiver(buffer);
        upload.setAcceptedFileTypes(".ftl", ".java", ".xml", ".txt", ".md"); // A few common types
        upload.setMaxFiles(1);
        upload.setMaxFileSize(5 * 1024 * 1024); // 5MB limit

        upload.addSucceededListener(event -> {
            originalFileName = event.getFileName();
            try (InputStream inputStream = buffer.getInputStream()) {
                fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                // Pre-fill template name without extension
                templateName.setValue(removeFileExtension(originalFileName));
                Notification.show("File '" + originalFileName + "' uploaded successfully. Please review and save.", 3000, Notification.Position.MIDDLE);
                saveButton.setEnabled(true);
            } catch (IOException e) {
                Notification.show("Error reading file content: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                e.printStackTrace();
                clearForm();
            }
        });

        upload.addFileRejectedListener(event -> {
            Notification.show(event.getErrorMessage(), 5000, Notification.Position.MIDDLE);
        });

        upload.addFailedListener(event -> {
            Notification.show("File upload failed: " + event.getReason().getMessage(), 5000, Notification.Position.MIDDLE);
            clearForm();
        });

        templateName.setRequiredIndicatorVisible(true);
        templateName.setPlaceholder("e.g., pom, entity-class");
        path.setPlaceholder("e.g., pom.xml or src/main/java/com/example/domain/${entity.name}.java");

        VerticalLayout formLayout = new VerticalLayout(templateName, path, upload, saveButton);
        formLayout.setAlignItems(Alignment.STRETCH);
        formLayout.setWidth("500px");

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false); // Disabled until a file is uploaded
        saveButton.addClickListener(e -> saveTemplate());

        add(formLayout);
    }

    private void saveTemplate() {
        if (templateName.getValue().isBlank()) {
            Notification.show("Template Name is required.", 3000, Notification.Position.MIDDLE);
            templateName.focus();
            return;
        }
        if (fileContent == null || fileContent.isEmpty()) {
            Notification.show("No file content. Please upload a file.", 3000, Notification.Position.MIDDLE);
            return;
        }

        Template newTemplate = new Template();
        newTemplate.setTemplateName(templateName.getValue());
        newTemplate.setPath(path.getValue());
        newTemplate.setContent(fileContent);

        try {
            templateRepository.save(newTemplate);
            Notification.show("Template '" + newTemplate.getTemplateName() + "' saved successfully!", 3000, Notification.Position.BOTTOM_START);
            clearForm();
        } catch (Exception e) {
            // Catch potential database errors, like unique constraint violation on templateName
            Notification.show("Error saving template. A template with this name may already exist.", 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private void clearForm() {
        templateName.clear();
        path.clear();
        upload.clearFileList();
        fileContent = null;
        originalFileName = null;
        saveButton.setEnabled(false);
    }

    private String removeFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(0, lastDot);
        }
        return filename;
    }
}