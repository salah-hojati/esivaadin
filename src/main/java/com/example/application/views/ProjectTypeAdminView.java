package com.example.application.views;

import com.example.application.entity.Framework;
import com.example.application.entity.ProjectType;
import com.example.application.repository.FrameworkRepository;
import com.example.application.repository.ProjectTypeRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

@Route("admin/project-types")
public class ProjectTypeAdminView extends AbstractAdminView {

    private final ProjectTypeRepository projectTypeRepository;
    private final FrameworkRepository frameworkRepository;
    private final Grid<ProjectType> grid = new Grid<>(ProjectType.class, false);
    private final Binder<ProjectType> binder = new Binder<>(ProjectType.class);

    public ProjectTypeAdminView(ProjectTypeRepository projectTypeRepository, FrameworkRepository frameworkRepository) {
        this.projectTypeRepository = projectTypeRepository;
        this.frameworkRepository = frameworkRepository;

        Button addButton = new Button("Add Project Type", e -> openDialog(new ProjectType()));
        configureGrid();

        add(addButton, grid);
        setSizeFull();
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(ProjectType::getName).setHeader("Name").setSortable(true);
        grid.addColumn(projectType -> projectType.getFrameworks().stream()
                        .map(Framework::getName)
                        .collect(Collectors.joining(", ")))
                .setHeader("Associated Frameworks");

        grid.addComponentColumn(item -> new Button("Edit", e -> openDialog(item)));
        grid.addComponentColumn(item -> {
            Button deleteButton = new Button("Delete", e -> deleteItem(item));
            deleteButton.getStyle().set("color", "var(--lumo-error-text-color)");
            return deleteButton;
        });
    }

    private void refreshGrid() {
        grid.setItems(projectTypeRepository.findAll());
    }

    private void openDialog(ProjectType projectType) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(projectType.getId() == null ? "Add New Project Type" : "Edit Project Type");

        binder.setBean(projectType);

        VerticalLayout formLayout = createFormLayout(dialog);
        dialog.add(formLayout);
        dialog.open();
    }

    private VerticalLayout createFormLayout(Dialog dialog) {
        TextField nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(ProjectType::getName, ProjectType::setName);

        MultiSelectComboBox<Framework> frameworkSelect = new MultiSelectComboBox<>("Associated Frameworks");
        List<Framework> allFrameworks = frameworkRepository.findAll();
        frameworkSelect.setItems(allFrameworks);
        frameworkSelect.setItemLabelGenerator(Framework::getName);

        binder.forField(frameworkSelect)
                .bind(ProjectType::getFrameworks, ProjectType::setFrameworks);

        Button saveButton = new Button("Save", e -> saveItem(dialog));
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        return new VerticalLayout(nameField, frameworkSelect, buttonLayout);
    }

    private void saveItem(Dialog dialog) {
        try {
            ProjectType projectTypeToSave = binder.getBean();
            binder.writeBean(projectTypeToSave);
            projectTypeRepository.save(projectTypeToSave);

            Notification.show("Project Type saved successfully.");
            refreshGrid();
            dialog.close();
        } catch (ValidationException e) {
            Notification.show("Please fill in all required fields.");
        } catch (Exception e) {
            Notification.show("Error: Could not save Project Type. A type with this name may already exist.");
            e.printStackTrace();
        }
    }

    private void deleteItem(ProjectType item) {
        projectTypeRepository.delete(item);
        refreshGrid();
    }
}