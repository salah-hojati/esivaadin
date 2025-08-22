package com.example.application.views;

import com.example.application.entity.ProjectFile;
import com.example.application.entity.Template;
import com.example.application.repository.ProjectFileRepository;
import com.example.application.repository.TemplateRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Route("admin/templates")
@PermitAll
public class TemplateAdminView extends VerticalLayout {

    private final TemplateRepository templateRepository;
    private final ProjectFileRepository projectFileRepository; // Inject repository for checking existing files
    private final Grid<Template> grid = new Grid<>(Template.class, false);
    private final Binder<Template> binder = new Binder<>(Template.class);

    public TemplateAdminView(TemplateRepository templateRepository, ProjectFileRepository projectFileRepository) {
        this.templateRepository = templateRepository;
        this.projectFileRepository = projectFileRepository;

        setSizeFull();
        add(new H1("Template Management"));

        Button addTemplateButton = new Button("Add New Template", VaadinIcon.PLUS.create());
        addTemplateButton.addClickListener(e -> openTemplateDialog(new Template()));

        // The "Upload Template" button is now removed from the main view.
        add(addTemplateButton);
        configureGrid();
        add(grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Template::getTemplateName).setHeader("Template Name").setSortable(true).setFlexGrow(1);
        grid.addColumn(Template::getPath).setHeader("Path").setSortable(true).setFlexGrow(1);
        // Add a column to show the type of template (Text or File)
        grid.addColumn(template -> (template.getContent() != null && !template.getContent().isBlank()) ? "Text" : "File(s)")
                .setHeader("Type").setFlexGrow(0);

        grid.addComponentColumn(template -> {
            Button editButton = new Button("Edit", VaadinIcon.EDIT.create());
            editButton.addClickListener(e -> openTemplateDialog(template));
            return editButton;
        }).setHeader("Edit").setFlexGrow(0);

        grid.addComponentColumn(template -> {
            Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClickListener(e -> confirmDeleteDialog(template));
            return deleteButton;
        }).setHeader("Delete").setFlexGrow(0);
    }
    private String getTemplateSourceInfo(Template template) {
        if (template.getContent() != null && !template.getContent().isBlank()) {
            return "Text Content";
        }
        if (template.getProjectFiles() != null && !template.getProjectFiles().isEmpty()) {
            return "Files: " + template.getProjectFiles().stream()
                    .map(ProjectFile::getFileName)
                    .sorted()
                    .collect(Collectors.joining(", "));
        }
        return "Not configured";
    }
    private void refreshGrid() {
        grid.setItems(templateRepository.findAll());
    }

    private void openTemplateDialog(Template template) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(template.getId() == null ? "Add New Template" : "Edit Template");
        dialog.setWidth("70vw");
        dialog.setHeight("90vh");

        // --- UI COMPONENTS FOR THE DIALOG ---
        TextField templateName = new TextField("Template Name");
        TextField path = new TextField("Path (base directory for files)");
        path.setWidthFull();

        RadioButtonGroup<String> sourceType = new RadioButtonGroup<>();
        sourceType.setLabel("Template Source");
        sourceType.setItems("Text Content", "File Upload");

        TextArea contentArea = new TextArea("Template Content");
        contentArea.setSizeFull();

        MemoryBuffer buffer = new MemoryBuffer();
        Upload uploader = new Upload(buffer);
        uploader.setAcceptedFileTypes(".*"); // Allow any file type
        Span uploadStatus = new Span("Upload a new file to associate it with this template.");

        VerticalLayout fileLayout = new VerticalLayout(uploader, uploadStatus);
        fileLayout.setSpacing(false);
        fileLayout.setPadding(false);

        // --- LAYOUT AND VISIBILITY LOGIC ---
        sourceType.addValueChangeListener(e -> {
            boolean isText = "Text Content".equals(e.getValue());
            contentArea.setVisible(isText);
            fileLayout.setVisible(!isText);
        });

        // --- BINDING AND INITIAL STATE ---
        binder.forField(templateName).asRequired("Template name is required").bind(Template::getTemplateName, Template::setTemplateName);
        binder.forField(path).bind(Template::getPath, Template::setPath); // Path is not required for file-only templates
        binder.setBean(template);

        if (template.getProjectFiles() != null && !template.getProjectFiles().isEmpty()) {
            sourceType.setValue("File Upload");
            String fileNames = template.getProjectFiles().stream()
                    .map(ProjectFile::getFileName)
                    .collect(Collectors.joining(", "));
            uploadStatus.setText("Associated file(s): " + fileNames);
        } else {
            sourceType.setValue("Text Content");
            contentArea.setValue(template.getContent() != null ? template.getContent() : "");
        }

        // --- SAVE BUTTON LOGIC ---
        Button saveButton = new Button("Save", e -> {
            try {
                binder.writeBean(template);
                String selectedSource = sourceType.getValue();

                if ("Text Content".equals(selectedSource)) {
                    template.setContent(contentArea.getValue());
                    template.getProjectFiles().clear(); // Enforce mutual exclusivity
                } else { // "File Upload"
                    template.setContent(null); // Enforce mutual exclusivity
                    if (buffer.getInputStream() != null && buffer.getFileName() != null && !buffer.getFileName().isBlank()) {
                        // A new file was uploaded
                        String fileName = buffer.getFileName();
                        InputStream inputStream = buffer.getInputStream();
                        byte[] fileBytes = inputStream.readAllBytes();

                        // Check if a file with this name already exists to avoid duplicates
                        Optional<ProjectFile> existingFileOpt = projectFileRepository.findByFileName(fileName);
                        ProjectFile fileToAssociate = existingFileOpt.orElseGet(() -> new ProjectFile(fileName, fileBytes));
                        if (existingFileOpt.isPresent()) {
                            fileToAssociate.setContent(fileBytes); // Update content if file exists
                        }

                        template.getProjectFiles().clear(); // Replace existing files with the new one
                        template.getProjectFiles().add(fileToAssociate);
                    }
                }

                templateRepository.save(template);
                Notification.show("Template saved successfully.", 3000, Notification.Position.BOTTOM_START);
                refreshGrid();
                dialog.close();

            } catch (ValidationException ex) {
                Notification.show("Please correct the highlighted errors.");
            } catch (IOException ex) {
                Notification.show("Error reading uploaded file.", 5000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Error saving template: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        VerticalLayout formLayout = new VerticalLayout(templateName, path, sourceType, contentArea, fileLayout);
        formLayout.setSizeFull();
        formLayout.setFlexGrow(1, contentArea);

        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void confirmDeleteDialog(Template template) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirm Deletion");
        confirmDialog.add(new H2("Are you sure you want to delete the template '" + template.getTemplateName() + "'?"));
        confirmDialog.add("This will not delete any associated static files, only the template itself.");

        Button deleteButton = new Button("Delete", e -> {
            try {
                templateRepository.delete(template);
                Notification.show("Template deleted.", 3000, Notification.Position.BOTTOM_START);
                refreshGrid();
            } catch (Exception ex) {
                Notification.show("Error deleting template: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
            confirmDialog.close();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancel", e -> confirmDialog.close());
        confirmDialog.getFooter().add(cancelButton, deleteButton);
        confirmDialog.open();
    }
}