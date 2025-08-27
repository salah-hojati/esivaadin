package com.example.application.views;

import com.example.application.entity.Framework;
import com.example.application.entity.Template;
import com.example.application.repository.FrameworkRepository;
import com.example.application.repository.TemplateRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.HashSet;
import java.util.Set;

@Route("admin/frameworks")
@PermitAll
public class FrameworkAdminView extends AbstractAdminView {

    private final FrameworkRepository frameworkRepository;
    private final TemplateRepository templateRepository;
    private final Grid<Framework> grid = new Grid<>(Framework.class, false);
    private final Binder<Framework> binder = new Binder<>(Framework.class);

    public FrameworkAdminView(FrameworkRepository frameworkRepository, TemplateRepository templateRepository) {
        this.frameworkRepository = frameworkRepository;
        this.templateRepository = templateRepository;

        Button addButton = new Button("Add Framework");
        addButton.addClickListener(e -> openAddDialog());

        configureGrid();

        add(addButton, grid);
        setSizeFull();
        grid.setSizeFull();
        setPadding(true);

        refreshGrid();
    }

    private void configureGrid() {
        grid.setColumns("name");
        grid.addColumn(f -> f.getTemplates().size()).setHeader("# Templates");
        grid.addComponentColumn(this::createEditButton).setHeader("Edit");
        grid.addComponentColumn(this::createDeleteButton).setHeader("Delete");
    }

    private Button createEditButton(Framework item) {
        return new Button("Edit", e -> openEditDialog(item));
    }

    private Button createDeleteButton(Framework item) {
        return new Button("Delete", e -> deleteItem(item));
    }

    private void refreshGrid() {
        grid.setItems(frameworkRepository.findAll());
    }

    private void openAddDialog() {
        openEditDialog(new Framework());
    }

    private void openEditDialog(Framework item) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(item.getId() == null ? "Add New Framework" : "Edit Framework");
        dialog.setWidth("600px");

        binder.setBean(item);

        VerticalLayout formLayout = createFormLayout(dialog, item);
        dialog.add(formLayout);

        dialog.open();
    }

    private VerticalLayout createFormLayout(Dialog dialog, Framework framework) {
        TextField nameField = new TextField("Name");
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(Framework::getName, Framework::setName);

        Grid<Template> templateGrid = new Grid<>(Template.class, false);
        templateGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        templateGrid.addColumn(Template::getTemplateName).setHeader("Template Name");
        templateGrid.addColumn(Template::getPath).setHeader("Path");
        templateGrid.setItems(templateRepository.findAll());
        templateGrid.setHeight("300px");

        if (framework.getTemplates() != null) {
            // --- FIX: Use setValue() for multi-select components ---
            framework.getTemplates().forEach(
                    template -> {templateGrid.getSelectionModel().select(template);
                    }
            );
           // templateGrid.getSelectionModel().select(framework.getTemplates());

        }

        Button saveButton = new Button("Save", e -> saveItem(dialog, templateGrid.getSelectedItems()));
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        VerticalLayout formLayout = new VerticalLayout(nameField, templateGrid, buttonLayout);
        formLayout.setPadding(true);
        formLayout.setSpacing(true);
        return formLayout;
    }

    private void saveItem(Dialog dialog, Set<Template> selectedTemplates) {
        try {
            Framework frameworkToSave = binder.getBean();
            binder.writeBean(frameworkToSave);

            frameworkToSave.setTemplates(new HashSet<>(selectedTemplates));

            frameworkRepository.save(frameworkToSave);

            Notification.show("Framework saved successfully.");
            refreshGrid();
            dialog.close();
        } catch (com.vaadin.flow.data.binder.ValidationException e) {
            Notification.show("Please fill in all required fields.");
        } catch (Exception e) {
            Notification.show("Error: Could not save framework. A framework with this name may already exist.");
            e.printStackTrace();
        }
    }

    private void deleteItem(Framework item) {
        frameworkRepository.delete(item);
        refreshGrid();
    }
}