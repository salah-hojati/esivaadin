package com.example.application.views;

import com.example.application.entity.ProjectFile;
import com.example.application.entity.Template;
import com.example.application.repository.ProjectFileRepository;
import com.example.application.repository.TemplateRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Route("admin/upload-file")
@PageTitle("Upload File")
@PermitAll
public class ProjectFileUploadView extends AbstractAdminView {

    private final ProjectFileRepository projectFileRepository;

    private final Upload upload = new Upload();
    private final MultiSelectComboBox<Template> templateSelector = new MultiSelectComboBox<>("Associate with Templates");
    private final Button saveButton = new Button("Save File");

    private final MemoryBuffer buffer = new MemoryBuffer();
    private String originalFileName;
    private String mimeType;
    private byte[] fileContent;

    public ProjectFileUploadView(ProjectFileRepository projectFileRepository, TemplateRepository templateRepository) {
        this.projectFileRepository = projectFileRepository;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        add(new H1("Upload a Reusable Project File"));

        upload.setReceiver(buffer);
        upload.setMaxFiles(1);
        upload.setMaxFileSize(15 * 1024 * 1024); // 15MB limit

        upload.addSucceededListener(event -> {
            originalFileName = event.getFileName();
            mimeType = event.getMIMEType();
            try {
                fileContent = buffer.getInputStream().readAllBytes();
                Notification.show("File '" + originalFileName + "' uploaded. Select associated templates and save.", 3000, Notification.Position.MIDDLE);
                saveButton.setEnabled(true);
                templateSelector.setEnabled(true);
            } catch (IOException e) {
                Notification.show("Error reading file content: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
                e.printStackTrace();
                clearForm();
            }
        });

        upload.addFileRejectedListener(event -> Notification.show(event.getErrorMessage(), 5000, Notification.Position.MIDDLE));
        upload.addFailedListener(event -> {
            Notification.show("File upload failed: " + event.getReason().getMessage(), 5000, Notification.Position.MIDDLE);
            clearForm();
        });

        List<Template> templates = templateRepository.findAll();
        templateSelector.setItems(templates);
        templateSelector.setItemLabelGenerator(Template::getTemplateName);
        templateSelector.setEnabled(false);

        VerticalLayout formLayout = new VerticalLayout(upload, templateSelector, saveButton);
        formLayout.setAlignItems(Alignment.STRETCH);
        formLayout.setWidth("500px");
        formLayout.add(new Span("Upload a file (e.g., checkstyle.xml, .gitignore, a static logo) that can be reused across multiple generated projects."));

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);
        saveButton.addClickListener(e -> saveFile());

        add(formLayout);
    }

    private void saveFile() {
        if (fileContent == null) {
            Notification.show("No file has been uploaded.", 3000, Notification.Position.MIDDLE);
            return;
        }

        ProjectFile newFile = new ProjectFile();
        newFile.setFileName(originalFileName);
        newFile.setContentType(mimeType);
        newFile.setContent(fileContent);
        newFile.setTemplates(new HashSet<>(templateSelector.getValue()));

        projectFileRepository.save(newFile);
        Notification.show("File '" + originalFileName + "' saved successfully!", 3000, Notification.Position.BOTTOM_START);
        clearForm();
    }

    private void clearForm() {
        upload.clearFileList();
        templateSelector.clear();
        templateSelector.setEnabled(false);
        saveButton.setEnabled(false);
        fileContent = null;
        originalFileName = null;
        mimeType = null;
    }
}