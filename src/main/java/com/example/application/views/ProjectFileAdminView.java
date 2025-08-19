package com.example.application.views;

import com.example.application.entity.ProjectFile;
import com.example.application.entity.Template;
import com.example.application.repository.ProjectFileRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.stream.Collectors;

@Route("admin/project-files")
@PermitAll
public class ProjectFileAdminView extends VerticalLayout {

    private final ProjectFileRepository projectFileRepository;
    private final Grid<ProjectFile> grid = new Grid<>(ProjectFile.class, false);

    public ProjectFileAdminView(ProjectFileRepository projectFileRepository) {
        this.projectFileRepository = projectFileRepository;

        setSizeFull();
        add(new H1("Project File Management"));

        Button uploadFileButton = new Button("Upload New File", VaadinIcon.UPLOAD.create());
        uploadFileButton.addClickListener(e -> UI.getCurrent().navigate(ProjectFileUploadView.class));

        add(new HorizontalLayout(uploadFileButton));
        configureGrid();
        add(grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(ProjectFile::getFileName).setHeader("File Name").setSortable(true).setFlexGrow(1);
        grid.addColumn(ProjectFile::getContentType).setHeader("Content Type").setSortable(true).setFlexGrow(1);
        grid.addColumn(projectFile -> {
            // Display size in KB or MB for readability
            if (projectFile.getContent() == null) return "0 B";
            double sizeInKb = projectFile.getContent().length / 1024.0;
            if (sizeInKb < 1024) {
                return String.format("%.2f KB", sizeInKb);
            } else {
                return String.format("%.2f MB", sizeInKb / 1024.0);
            }
        }).setHeader("Size").setSortable(true).setFlexGrow(0);

        grid.addColumn(projectFile -> projectFile.getTemplates().stream()
                        .map(Template::getTemplateName)
                        .collect(Collectors.joining(", ")))
                .setHeader("Used By Templates").setFlexGrow(2);

        grid.addComponentColumn(this::createDeleteButton).setHeader("Actions").setFlexGrow(0);
    }

    private Button createDeleteButton(ProjectFile projectFile) {
        Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.addClickListener(e -> confirmDeleteDialog(projectFile));
        return deleteButton;
    }

    private void confirmDeleteDialog(ProjectFile projectFile) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirm Deletion");
        confirmDialog.add(new H2("Are you sure you want to delete this file?"));
        confirmDialog.add(new VerticalLayout(
                new HorizontalLayout(new H2("File:"), new H2(projectFile.getFileName()))
        ));

        Button deleteButton = new Button("Delete", e -> {
            try {
                projectFileRepository.delete(projectFile);
                Notification.show("File deleted.", 3000, Notification.Position.BOTTOM_START);
                refreshGrid();
            } catch (Exception ex) {
                Notification.show("Error deleting file: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
            confirmDialog.close();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancel", e -> confirmDialog.close());

        confirmDialog.getFooter().add(cancelButton, deleteButton);
        confirmDialog.open();
    }

    private void refreshGrid() {
        grid.setItems(projectFileRepository.findAll());
    }
}