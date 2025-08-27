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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Route("admin/templates")
@PermitAll
public class TemplateAdminView extends AbstractAdminView {

    private final TemplateRepository templateRepository;
    private final ProjectFileRepository projectFileRepository;
    private final Grid<Template> grid = new Grid<>(Template.class, false);
    private final Binder<Template> binder = new Binder<>(Template.class);

    public TemplateAdminView(TemplateRepository templateRepository, ProjectFileRepository projectFileRepository) {
        this.templateRepository = templateRepository;
        this.projectFileRepository = projectFileRepository;

        setSizeFull();
        add(new H1("Template Management"));

        Button addTemplateButton = new Button("Add New Template", VaadinIcon.PLUS.create());
        addTemplateButton.addClickListener(e -> openTemplateDialog(new Template()));

        add(addTemplateButton);
        configureGrid();
        add(grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Template::getTemplateName).setHeader("Template Name").setSortable(true).setFlexGrow(1);
        grid.addColumn(Template::getPath).setHeader("Path").setSortable(true).setFlexGrow(1);
        grid.addColumn(this::getTemplateSourceInfo).setHeader("Source").setSortable(true).setFlexGrow(2);

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
        sourceType.setItems("Text Content", "File-based");

        TextArea contentArea = new TextArea("Template Content");
        contentArea.setSizeFull();

        // --- NEW: Grid for selecting multiple files ---
        Grid<ProjectFile> projectFileGrid = new Grid<>(ProjectFile.class, false);
        projectFileGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        projectFileGrid.addColumn(ProjectFile::getFileName).setHeader("File Name").setSortable(true);
        projectFileGrid.addColumn(this::formatFileSize).setHeader("Size").setFlexGrow(0);
        projectFileGrid.setItems(projectFileRepository.findAll());
        projectFileGrid.setSizeFull();

        // --- LAYOUT AND VISIBILITY LOGIC ---
        VerticalLayout fileLayout = new VerticalLayout(projectFileGrid);
        fileLayout.setSizeFull();
        fileLayout.setPadding(false);

        sourceType.addValueChangeListener(e -> {
            boolean isText = "Text Content".equals(e.getValue());
            contentArea.setVisible(isText);
            fileLayout.setVisible(!isText);
        });

        // --- BINDING AND INITIAL STATE ---
        binder.forField(templateName).asRequired("Template name is required").bind(Template::getTemplateName, Template::setTemplateName);
        binder.forField(path).bind(Template::getPath, Template::setPath);
        binder.setBean(template);

        if (template.getProjectFiles() != null && !template.getProjectFiles().isEmpty()) {
            sourceType.setValue("File-based");



            if(template.getProjectFiles() !=null){
                template.getProjectFiles().forEach(
                        projectFile -> {projectFileGrid.getSelectionModel().select(projectFile);
                        }
                );
            }
         //   projectFileGrid.getSelectionModel().select(template.getProjectFiles());
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
                } else { // "File-based"
                    template.setContent(null); // Enforce mutual exclusivity
                    Set<ProjectFile> selectedFiles = projectFileGrid.getSelectedItems();
                    template.setProjectFiles(new HashSet<>(selectedFiles)); // Update the set
                }

                templateRepository.save(template);
                Notification.show("Template saved successfully.", 3000, Notification.Position.BOTTOM_START);
                refreshGrid();
                dialog.close();

            } catch (ValidationException ex) {
                Notification.show("Please correct the highlighted errors.");
            } catch (Exception ex) {
                Notification.show("Error saving template: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        VerticalLayout formLayout = new VerticalLayout(templateName, path, sourceType, contentArea, fileLayout);
        formLayout.setSizeFull();
        formLayout.setFlexGrow(1, contentArea, fileLayout);

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

    private String formatFileSize(ProjectFile file) {
        if (file.getContent() == null || file.getContent().length == 0) {
            return "0 B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(file.getContent().length) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(file.getContent().length / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}