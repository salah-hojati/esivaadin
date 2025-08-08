package com.example.application.views;

import com.example.application.entity.ProjectType;
import com.example.application.repository.ProjectTypeRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("admin/project-types")
@PermitAll
public class ProjectTypeAdminView extends VerticalLayout {

    private final ProjectTypeRepository projectTypeRepository;
    private final Grid<ProjectType> grid = new Grid<>(ProjectType.class);
    private final Binder<ProjectType> binder = new Binder<>(ProjectType.class);

    public ProjectTypeAdminView(ProjectTypeRepository projectTypeRepository) {
        this.projectTypeRepository = projectTypeRepository;

        // UI components
        Button addButton = new Button("Add Project Type");
        addButton.addClickListener(e -> openAddDialog());

        grid.setColumns("name"); // Display the 'name' property
        grid.addComponentColumn(item -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> openEditDialog(item));
            return editButton;
        });
        grid.addComponentColumn(item -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(e -> deleteItem(item));
            return deleteButton;
        });

        add(addButton, grid);
        setSizeFull();
        grid.setSizeFull();
        setPadding(true);

        refreshGrid();
    }

    private void refreshGrid() {
        grid.setItems(projectTypeRepository.findAll());
    }

    private void openAddDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Project Type");

        ProjectType projectType = new ProjectType();
        binder.setBean(projectType);

        VerticalLayout formLayout = createFormLayout(dialog);
        dialog.add(formLayout);

        dialog.open();
    }

    private void openEditDialog(ProjectType item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Project Type");

        binder.setBean(item);

        VerticalLayout formLayout = createFormLayout(dialog);
        dialog.add(formLayout);

        dialog.open();
    }

    private VerticalLayout createFormLayout(Dialog dialog) {
        TextField nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(ProjectType::getName, ProjectType::setName);

        Button saveButton = new Button("Save");
        saveButton.addClickListener(e -> saveItem(dialog));

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        VerticalLayout formLayout = new VerticalLayout(nameField, buttonLayout);
        formLayout.setPadding(true);
        return formLayout;
    }

    private void saveItem(Dialog dialog) {
        try {
            binder.writeBean(binder.getBean());
            projectTypeRepository.save(binder.getBean());
            refreshGrid();
            dialog.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(ProjectType item) {
        projectTypeRepository.delete(item);
        refreshGrid();
    }
}