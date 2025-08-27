package com.example.application.views;

import com.example.application.entity.ProjectFile;
import com.example.application.repository.ProjectFileRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.text.DecimalFormat;

@Route("admin/project-files")
@PermitAll
public class ProjectFileAdminView extends AbstractAdminView {

    private final ProjectFileRepository projectFileRepository;
    private final Grid<ProjectFile> grid = new Grid<>(ProjectFile.class, false);
    private final Binder<ProjectFile> binder = new Binder<>(ProjectFile.class);

    public ProjectFileAdminView(ProjectFileRepository projectFileRepository) {
        this.projectFileRepository = projectFileRepository;

        setSizeFull();
        add(new H1("Project File Management"));

        Button addFileButton = new Button("Upload New File", VaadinIcon.PLUS.create());
        addFileButton.addClickListener(e -> openEditDialog(new ProjectFile()));

        add(addFileButton);
        configureGrid();
        add(grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(ProjectFile::getFileName).setHeader("File Name").setSortable(true);
        grid.addColumn(this::formatFileSize).setHeader("Size").setSortable(true).setFlexGrow(0);
        grid.addComponentColumn(this::createEditButton).setHeader("Edit").setFlexGrow(0);
        grid.addComponentColumn(this::createDeleteButton).setHeader("Delete").setFlexGrow(0);
    }

    private String formatFileSize(ProjectFile file) {
        if (file.getContent() == null || file.getContent().length == 0) {
            return "0 B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(file.getContent().length) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(file.getContent().length / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private Button createEditButton(ProjectFile file) {
        return new Button("Edit", VaadinIcon.EDIT.create(), e -> openEditDialog(file));
    }

    private Button createDeleteButton(ProjectFile file) {
        Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.addClickListener(e -> deleteFile(file));
        return deleteButton;
    }

    private void refreshGrid() {
        grid.setItems(projectFileRepository.findAll());
    }

    private void openEditDialog(ProjectFile projectFile) {
        Dialog dialog = new Dialog();
        boolean isNewFile = projectFile.getId() == null;
        dialog.setHeaderTitle(isNewFile ? "Upload New File" : "Edit File: " + projectFile.getFileName());
        dialog.setWidth("500px");

        binder.setBean(projectFile);

        TextField fileNameField = new TextField("File Name");
        binder.forField(fileNameField)
                .asRequired("File name is required")
                .bind(ProjectFile::getFileName, ProjectFile::setFileName);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload uploader = new Upload(buffer);
        uploader.setAcceptedFileTypes(".*");
        uploader.addSucceededListener(event -> {
            // When a file is uploaded, automatically populate the file name field if it's empty
            if (fileNameField.getValue() == null || fileNameField.getValue().isBlank()) {
                fileNameField.setValue(event.getFileName());
            }
        });

        Span uploadStatus = new Span();
        if (!isNewFile) {
            uploadStatus.setText("Upload a new file to replace the existing one.");
        } else {
            uploadStatus.setText("Select a file to upload.");
        }

        Button saveButton = new Button("Save", e -> {
            try {
                binder.writeBean(projectFile);

                // Check if a file was uploaded
                if (buffer.getInputStream() != null && buffer.getFileName() != null && !buffer.getFileName().isBlank()) {
                    projectFile.setContent(buffer.getInputStream().readAllBytes());
                } else if (isNewFile) {
                    // A new ProjectFile must have content
                    Notification.show("Please upload a file.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                projectFileRepository.save(projectFile);
                Notification.show("File saved successfully.", 3000, Notification.Position.BOTTOM_START);
                refreshGrid();
                dialog.close();

            } catch (ValidationException ex) {
                Notification.show("Please correct the form errors.", 3000, Notification.Position.MIDDLE);
            } catch (IOException ex) {
                Notification.show("Error reading uploaded file.", 5000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Error saving file: A file with this name may already exist.", 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(new VerticalLayout(fileNameField, uploader, uploadStatus));
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void deleteFile(ProjectFile file) {
        try {
            projectFileRepository.delete(file);
            Notification.show("File '" + file.getFileName() + "' deleted.", 3000, Notification.Position.BOTTOM_START);
            refreshGrid();
        } catch (DataIntegrityViolationException e) {
            Notification.show("Cannot delete file '" + file.getFileName() + "' because it is currently used by one or more templates.", 5000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            Notification.show("Error deleting file: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }
}