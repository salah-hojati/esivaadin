package com.example.application.views;

import com.example.application.entity.Framework;
import com.example.application.repository.FrameworkRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("admin/frameworks")
@PermitAll
public class FrameworkAdminView extends VerticalLayout {

    private final FrameworkRepository frameworkRepository;
    private final Grid<Framework> grid = new Grid<>(Framework.class);
    private final Binder<Framework> binder = new Binder<>(Framework.class);

    public FrameworkAdminView(FrameworkRepository frameworkRepository) {
        this.frameworkRepository = frameworkRepository;

        // UI components
        Button addButton = new Button("Add Framework");
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
        grid.setItems(frameworkRepository.findAll());
    }

    private void openAddDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Framework");

        Framework framework = new Framework();
        binder.setBean(framework);

        VerticalLayout formLayout = createFormLayout(dialog);
        dialog.add(formLayout);

        dialog.open();
    }

    private void openEditDialog(Framework item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Framework");

        binder.setBean(item);

        VerticalLayout formLayout = createFormLayout(dialog);
        dialog.add(formLayout);

        dialog.open();
    }

    private VerticalLayout createFormLayout(Dialog dialog) {
        TextField nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(Framework::getName, Framework::setName);

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
            frameworkRepository.save(binder.getBean());
            refreshGrid();
            dialog.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteItem(Framework item) {
        frameworkRepository.delete(item);
        refreshGrid();
    }
}