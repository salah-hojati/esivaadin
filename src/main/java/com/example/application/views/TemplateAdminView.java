package com.example.application.views;

import com.example.application.entity.Template;
import com.example.application.repository.TemplateRepository;
import com.example.application.utils.ToFtlConverter;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("admin/templates")
@PermitAll
public class TemplateAdminView extends VerticalLayout {

    private final TemplateRepository templateRepository;
    private final Grid<Template> grid = new Grid<>(Template.class, false);
    private final Binder<Template> binder = new Binder<>(Template.class);

    // --- MODIFIED: Simplified constructor ---
    public TemplateAdminView(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;

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

    private void refreshGrid() {
        grid.setItems(templateRepository.findAll());
    }

    private void openTemplateDialog(Template template) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(template.getId() == null ? "Add New Template" : "Edit Template");
        dialog.setWidth("70vw");
        dialog.setHeight("90vh");

        // --- MODIFIED: Form fields are now much simpler ---
        TextField templateName = new TextField("Template Name");
        templateName.setPlaceholder("e.g., pom, entity-class");

        TextField path = new TextField("Path");
        path.setPlaceholder("e.g., pom.xml or src/main/java/com/example/domain/${entity.name}.java");
        path.setWidthFull();

        TextArea content = new TextArea("Template Content");
        content.setSizeFull();

        VerticalLayout formLayout = new VerticalLayout(templateName, path, content);
        formLayout.setSizeFull();
        formLayout.setFlexGrow(1, content); // Make the text area expand

        // --- MODIFIED: Data binding is also simpler ---
        binder.forField(templateName).asRequired("Template name is required").bind(Template::getTemplateName, Template::setTemplateName);
        binder.forField(path).asRequired("Path is required").bind(Template::getPath, Template::setPath);
        binder.forField(content).asRequired("Content cannot be empty").bind(Template::getContent, Template::setContent);

        binder.setBean(template);

        Button saveButton = new Button("Save", e -> {
            if (binder.validate().isOk()) {
                try {
                    template.setContent(ToFtlConverter.convertToFtl(template.getContent()));
                    binder.writeBean(template);
                    templateRepository.save(template);
                    Notification.show("Template saved successfully.", 3000, Notification.Position.BOTTOM_START);
                    refreshGrid();
                    dialog.close();
                } catch (ValidationException ex) {
                    Notification.show("An unexpected validation error occurred. Please check the form.");
                    ex.printStackTrace();
                } catch (Exception ex) {
                    // Catch potential database errors, like unique constraint violation on templateName
                    Notification.show("Error saving template. A template with this name may already exist.", 5000, Notification.Position.MIDDLE);
                    ex.printStackTrace();
                }
            } else {
                Notification.show("Please correct the highlighted errors before saving.");
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void confirmDeleteDialog(Template template) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirm Deletion");
        confirmDialog.add(new H2("Are you sure you want to delete this template?"));
        confirmDialog.add(new VerticalLayout(
                new HorizontalLayout(new H2("Template:"), new H2(template.getTemplateName()))
        ));

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