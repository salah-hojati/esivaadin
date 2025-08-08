package com.example.application.views;

import com.example.application.entity.Framework;
import com.example.application.entity.ProjectType;
import com.example.application.entity.Template;
import com.example.application.repository.FrameworkRepository;
import com.example.application.repository.ProjectTypeRepository;
import com.example.application.repository.TemplateRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A view for performing CRUD operations on project templates.
 * Accessible at the "/admin/templates" URL.
 */
@Route("admin/templates")
@PermitAll // In a real app, you'd secure this, e.g., @RolesAllowed("ADMIN")
public class TemplateAdminView extends VerticalLayout {

    private final TemplateRepository templateRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final FrameworkRepository frameworkRepository;

    private final Grid<Template> grid = new Grid<>(Template.class, false);
    private final Binder<Template> binder = new Binder<>(Template.class);

    public TemplateAdminView(TemplateRepository templateRepository, ProjectTypeRepository projectTypeRepository, FrameworkRepository frameworkRepository) {
        this.templateRepository = templateRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.frameworkRepository = frameworkRepository;

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
        grid.addColumn(Template::getBuildTool).setHeader("Build Tool").setSortable(true).setFlexGrow(0);
        grid.addColumn(Template::getProjectType).setHeader("Project Type").setSortable(true).setFlexGrow(0);
        grid.addColumn(Template::getFramework).setHeader("Framework").setSortable(true).setFlexGrow(0);
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

        // Form fields
        ComboBox<String> buildTool = new ComboBox<>("Build Tool");
        buildTool.setItems("Maven", "Gradle", "*");
        buildTool.setAllowCustomValue(true);

        ComboBox<String> projectType = new ComboBox<>("Project Type");
        // Use new ArrayList to make the list mutable for custom values
        List<String> projectTypeNames = new ArrayList<>(projectTypeRepository.findAll().stream().map(ProjectType::getName).collect(Collectors.toList()));
        projectTypeNames.add("*");
        projectType.setItems(projectTypeNames);
        projectType.setPlaceholder("Select or type a value (e.g., *)");
        projectType.setAllowCustomValue(true);
        projectType.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if (!projectTypeNames.contains(customValue)) {
                projectTypeNames.add(customValue);
                projectType.setItems(projectTypeNames);
            }
            projectType.setValue(customValue);
        });

        ComboBox<String> framework = new ComboBox<>("Framework");
        // Use new ArrayList to make the list mutable for custom values
        List<String> frameworkNames = new ArrayList<>(frameworkRepository.findAll().stream().map(Framework::getName).collect(Collectors.toList()));
        frameworkNames.add("*");
        framework.setItems(frameworkNames);
        framework.setPlaceholder("Select or type a value (e.g., *)");
        framework.setAllowCustomValue(true);
        framework.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            if (!frameworkNames.contains(customValue)) {
                frameworkNames.add(customValue);
                framework.setItems(frameworkNames);
            }
            framework.setValue(customValue);
        });

        TextField templateName = new TextField("Template Name");
        templateName.setPlaceholder("e.g., pom, entity-class");

        TextField path = new TextField("Path");
        path.setPlaceholder("e.g., pom.xml or src/main/java/com/example/domain/${entity.name}.java");
        path.setWidthFull();

        TextArea content = new TextArea("Template Content");
        content.setSizeFull();

        // --- MODIFIED: Added the 'path' field to the layout ---
        VerticalLayout formLayout = new VerticalLayout(buildTool, projectType, framework, templateName, path, content);
        formLayout.setSizeFull();
        formLayout.setFlexGrow(1, content); // Make the text area expand

        // Data binding
        binder.forField(buildTool).asRequired("Build tool is required").bind(Template::getBuildTool, Template::setBuildTool);
        binder.forField(projectType).asRequired("Project type is required").bind(Template::getProjectType, Template::setProjectType);
        binder.forField(framework).asRequired("Framework is required").bind(Template::getFramework, Template::setFramework);
        binder.forField(templateName).asRequired("Template name is required").bind(Template::getTemplateName, Template::setTemplateName);
        binder.forField(path).asRequired("Path is required").bind(Template::getPath, Template::setPath);
        binder.forField(content).asRequired("Content cannot be empty").bind(Template::getContent, Template::setContent);

        // Populate form if editing
        binder.setBean(template);

        // Buttons
        Button saveButton = new Button("Save", e -> {
            // First, explicitly run validation. This will update the UI to show any errors.
            if (binder.validate().isOk()) {
                // If validation passes, it's safe to write the bean and save.
                try {
                    binder.writeBean(template);
                    templateRepository.save(template);
                    Notification.show("Template saved successfully.", 3000, Notification.Position.BOTTOM_START);
                    refreshGrid();
                    dialog.close();
                } catch (ValidationException ex) {
                    // This catch is now a safeguard for any unexpected validation issues.
                    // It shouldn't be reached if binder.validate() is working correctly.
                    Notification.show("An unexpected validation error occurred. Please check the form.");
                    ex.printStackTrace();
                }
            } else {
                // If validation fails, notify the user. The invalid fields will already be highlighted.
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
                new HorizontalLayout(new H2("Build Tool:"), new H2(template.getBuildTool())),
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