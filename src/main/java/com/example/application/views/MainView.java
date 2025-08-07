package com.example.application.views;

import com.example.application.entity.Entity;
import com.example.application.entity.Field;
import com.example.application.entity.Project;
import com.example.application.repository.EntityRepository;
import com.example.application.repository.ProjectRepository;
import com.example.application.service.CodeGeneratorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Route("")
@PermitAll
public class MainView extends VerticalLayout {
    // Project components
    private ComboBox<Project> projectSelector;
    private TextField newProjectName;
    private ComboBox<String> projectTypeCombo;
    private RadioButtonGroup<String> buildToolSelector;
    private Button saveProjectButton;
    private Button downloadProjectButton;
    private Button newProjectButton;

    // Entity components
    private Button addEntityButton;
    private Grid<EntityModel> entityGrid;

    private final EntityRepository entityRepository;
    private final ProjectRepository projectRepository;
    private final CodeGeneratorService codeGeneratorService;

    public MainView(EntityRepository entityRepository, ProjectRepository projectRepository, CodeGeneratorService codeGeneratorService) {
        this.entityRepository = entityRepository;
        this.projectRepository = projectRepository;
        this.codeGeneratorService = codeGeneratorService;

        add(new H1("Project Management"));
        setupProjectLayout();

        add(new H1("Entities"));
        setupEntityLayout();

        refreshProjectSelector();
        updateUIState();
    }

    private void setupProjectLayout() {
        projectSelector = new ComboBox<>("Select Existing Project");
        projectSelector.setItemLabelGenerator(Project::getName);
        projectSelector.setWidth("300px");
        projectSelector.addValueChangeListener(e -> {
            handleProjectSelection(e.getValue());
            refreshEntityGrid();
            updateUIState();
        });

        newProjectName = new TextField("Project Name");
        projectTypeCombo = new ComboBox<>("Project Type");
        projectTypeCombo.setItems("Web", "API", "Job");
        projectTypeCombo.setPlaceholder("Select project type");

        buildToolSelector = new RadioButtonGroup<>();
        buildToolSelector.setLabel("Build Tool");
        buildToolSelector.setItems("Maven", "Gradle");

        HorizontalLayout projectDetailsLayout = new HorizontalLayout(newProjectName, projectTypeCombo, buildToolSelector);
        projectDetailsLayout.setAlignItems(Alignment.BASELINE);

        saveProjectButton = new Button("Save Project", e -> saveOrUpdateProject());
        downloadProjectButton = new Button("Download Project", e -> downloadProject());
        newProjectButton = new Button("New Project", e -> projectSelector.clear());
        HorizontalLayout buttonLayout = new HorizontalLayout(saveProjectButton, downloadProjectButton, newProjectButton);

        add(projectSelector, projectDetailsLayout, buttonLayout);
    }

    private void setupEntityLayout() {
        addEntityButton = new Button("Add Entity", e -> openEntityDialog(null)); // Pass null for new entity
        entityGrid = new Grid<>(EntityModel.class, false);
        entityGrid.addColumn(EntityModel::getName).setHeader("Entity Name").setSortable(true);
        entityGrid.addColumn(entity -> entity.getFields().size()).setHeader("# Fields");

        // Add Edit Button Column
        entityGrid.addComponentColumn(entityModel -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> openEntityDialog(entityModel));
            return editButton;
        }).setHeader("Edit");

        // Add Remove Button Column
        entityGrid.addComponentColumn(entityModel -> {
            Button removeButton = new Button("Remove");
            removeButton.getStyle().set("color", "var(--lumo-error-text-color)");
            removeButton.addClickListener(e -> confirmRemoveEntity(entityModel));
            return removeButton;
        }).setHeader("Remove");

        add(addEntityButton, entityGrid);
    }

    private void handleProjectSelection(Project project) {
        if (project != null) {
            newProjectName.setValue(project.getName());
            projectTypeCombo.setValue(project.getProjectType());
            buildToolSelector.setValue(project.getBuildTool());
        } else {
            newProjectName.clear();
            projectTypeCombo.clear();
            buildToolSelector.clear();
        }
    }

    private void saveOrUpdateProject() {
        String projectName = newProjectName.getValue();
        String projectType = projectTypeCombo.getValue();
        String buildTool = buildToolSelector.getValue();

        if (projectName == null || projectName.trim().isEmpty() || projectType == null || buildTool == null) {
            Notification.show("Please provide a project name, type, and build tool.");
            return;
        }

        Project selectedProject = projectSelector.getValue();
        Project projectToSave = (selectedProject != null) ? selectedProject : new Project();

        projectToSave.setName(projectName.trim());
        projectToSave.setProjectType(projectType);
        projectToSave.setBuildTool(buildTool);

        try {
            Project savedProject = projectRepository.save(projectToSave);
            Notification.show("Project '" + savedProject.getName() + "' saved successfully.");
            refreshProjectSelector();
            projectSelector.setValue(savedProject);
        } catch (Exception e) {
            Notification.show("Error: Could not save project. A project with this name might already exist.");
        }
    }


    // Inside your MainView class
    private void downloadProject() {
        Project selectedProject = projectSelector.getValue();
        if (selectedProject == null) {
            Notification.show("Please select a project to download.");
            return;
        }

        List<Entity> entities = entityRepository.findByProjectIdWithFields(selectedProject.getId());
        if (entities.isEmpty()) {
            Notification.show("Project has no entities to generate.");
            return;
        }

        try {
            byte[] zipData = codeGeneratorService.generateAndZipProject(selectedProject, entities);

            StreamResource streamResource = new StreamResource(selectedProject.getName() + ".zip",
                    () -> new ByteArrayInputStream(zipData));

            // Refined approach for triggering the download
            Anchor anchor = new Anchor(streamResource, "Download Project");
            anchor.getElement().setAttribute("download", true);
            anchor.getElement().getStyle().set("display", "none");
            add(anchor);

            anchor.getElement().executeJs("setTimeout(() => this.click(), 0);");

            Notification.show("Project download started...");

        } catch (IOException e) {
            e.printStackTrace();
            Notification.show("Error generating project zip file.", 3000, Notification.Position.MIDDLE);
        }
    }


    private void refreshProjectSelector() {
        List<Project> projects = projectRepository.findAll();
        Project selected = projectSelector.getValue();
        projectSelector.setItems(projects);
        if (selected != null) {
            projects.stream()
                    .filter(p -> p.getId().equals(selected.getId()))
                    .findFirst()
                    .ifPresent(projectSelector::setValue);
        }
    }

    private void refreshEntityGrid() {
        Project selectedProject = projectSelector.getValue();
        if (selectedProject != null) {
            List<Entity> projectEntities = entityRepository.findByProjectIdWithFields(selectedProject.getId());
            List<EntityModel> entityModels = projectEntities.stream()
                    .map(this::convertToEntityModel)
                    .collect(Collectors.toList());
            entityGrid.setItems(entityModels);
        } else {
            entityGrid.setItems(new ArrayList<>());
        }
    }

    /**
     * --- MODIFIED ---
     * Converts a JPA Entity to its UI Model, now including the relationshipType.
     */
    private EntityModel convertToEntityModel(Entity entity) {
        List<FieldModel> fieldModels = entity.getFields().stream()
                .map(field -> new FieldModel(field.getId(), field.getName(), field.getType(), field.isRequired(), field.getRelationshipType()))
                .collect(Collectors.toList());
        EntityModel model = new EntityModel(entity.getName(), fieldModels);
        model.setId(entity.getId());
        return model;
    }

    private void updateUIState() {
        boolean projectSelected = projectSelector.getValue() != null;
        addEntityButton.setEnabled(projectSelected);
        downloadProjectButton.setEnabled(projectSelected);
        newProjectButton.setEnabled(projectSelected);
        saveProjectButton.setEnabled(true);
    }

    private void confirmRemoveEntity(EntityModel entityToRemove) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirm Removal");
        confirmDialog.add(new VerticalLayout(new H2("Are you sure you want to remove the entity '" + entityToRemove.getName() + "'?")));

        Button confirmButton = new Button("Confirm", e -> {
            entityRepository.deleteById(entityToRemove.getId());
            Notification.show("Entity '" + entityToRemove.getName() + "' removed.");
            confirmDialog.close();
            refreshEntityGrid();
        });
        confirmButton.getStyle().set("color", "var(--lumo-error-text-color)");

        Button cancelButton = new Button("Cancel", e -> confirmDialog.close());
        confirmDialog.getFooter().add(cancelButton, confirmButton);
        confirmDialog.open();
    }

    private void openEntityDialog(EntityModel entityToEdit) {
        Project selectedProject = projectSelector.getValue();
        if (selectedProject == null) {
            Notification.show("Please select a project before adding an entity.");
            return;
        }

        boolean isEditing = entityToEdit != null;
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(isEditing ? "Edit Entity" : "Add New Entity to Project: " + selectedProject.getName());

        TextField entityNameField = new TextField("Entity Name");
        VerticalLayout fieldsLayout = new VerticalLayout();
        fieldsLayout.setPadding(false);
        List<FieldModel> tempFieldModels = new ArrayList<>();

        if (isEditing) {
            entityNameField.setValue(entityToEdit.getName());
            tempFieldModels.addAll(entityToEdit.getFields());
            tempFieldModels.forEach(fm -> renderFieldRow(fm, fieldsLayout, tempFieldModels, entityToEdit));
        }

        Button addFieldButton = createAddFieldButton(fieldsLayout, tempFieldModels, entityToEdit);
        Button saveEntityButton = createSaveEntityButton(isEditing, entityToEdit, dialog, entityNameField, tempFieldModels, selectedProject);

        dialog.add(entityNameField, addFieldButton, fieldsLayout, saveEntityButton);
        dialog.open();
    }

    /**
     * --- MODIFIED ---
     * Now displays the relationship type in a read-only field.
     */
    private void renderFieldRow(FieldModel fieldModel, VerticalLayout layout, List<FieldModel> modelList, EntityModel entityModel) {
        HorizontalLayout fieldRow = new HorizontalLayout();
        fieldRow.setAlignItems(Alignment.BASELINE);

        TextField idField = new TextField();
        if (fieldModel.getId() != null) {
            idField.setValue(fieldModel.getId().toString());
        }
        idField.setVisible(false);

        TextField name = new TextField("Field Name");
        name.setValue(fieldModel.getName());
        name.setReadOnly(true);

        TextField type = new TextField("Type");
        type.setValue(fieldModel.getType());
        type.setReadOnly(true);

        // New field to display the relationship
        TextField relationship = new TextField("Relationship");
        if (fieldModel.getRelationshipType() != null) {
            relationship.setValue(fieldModel.getRelationshipType());
        } else {
            relationship.setVisible(false); // Hide if it's not a relationship
        }
        relationship.setReadOnly(true);

        Checkbox required = new Checkbox("Required", fieldModel.isRequired());
        required.setReadOnly(true);

        Button removeFieldButton = new Button("X", e -> {
            if (fieldModel.getId() != null && entityModel != null && entityModel.getId() != null) {
                entityRepository.findByIdWithFields(entityModel.getId()).ifPresent(entity -> {
                    boolean removed = entity.getFields().removeIf(field -> field.getId().equals(fieldModel.getId()));
                    if (removed) {
                        entityRepository.save(entity);
                        Notification.show("Field '" + fieldModel.getName() + "' removed from database.");
                    }
                });
            }
            modelList.remove(fieldModel);
            layout.remove(fieldRow);
        });
        removeFieldButton.getStyle().set("color", "var(--lumo-error-text-color)");
        removeFieldButton.setTooltipText("Remove field");

        fieldRow.add(idField, name, type, relationship, required, removeFieldButton);
        layout.add(fieldRow);
    }

    /**
     * --- MODIFIED ---
     * This method now contains the logic to show/hide the relationship radio buttons.
     */
    private Button createAddFieldButton(VerticalLayout fieldsLayout, List<FieldModel> tempFieldModels, EntityModel entityToEdit) {
        return new Button("Add Field", e -> {
            Dialog fieldDialog = new Dialog();
            TextField fieldName = new TextField("Field Name");
            ComboBox<String> fieldType = new ComboBox<>("Type");
            Checkbox required = new Checkbox("Required");

            // 1. Create the relationship selector and hide it by default
            RadioButtonGroup<String> relationshipSelector = new RadioButtonGroup<>("Relationship Type");
            relationshipSelector.setItems("OneToOne", "ManyToOne", "OneToMany");
            relationshipSelector.setVisible(false);

            // 2. Get all possible types (primitives + entities)
            final List<String> standardTypes = List.of("String", "Integer", "Boolean", "Double", "Date");
            final Project currentProject = projectSelector.getValue();

            // This variable is now declared final and assigned only once.
            final List<String> entityTypeNames;
            if (currentProject != null) {
                entityTypeNames = entityRepository.findByProjectId(currentProject.getId()).stream()
                        .map(Entity::getName)
                        // Filter out the current entity's name if we are editing to prevent self-reference
                        .filter(name -> entityToEdit == null || !name.equals(entityToEdit.getName()))
                        .collect(Collectors.toList());
            } else {
                entityTypeNames = new ArrayList<>();
            }

            List<String> allPossibleTypes = new ArrayList<>(standardTypes);
            allPossibleTypes.addAll(entityTypeNames);
            fieldType.setItems(allPossibleTypes);

            // 3. Add a listener to show/hide the relationship selector
            fieldType.addValueChangeListener(event -> {
                // This now works because entityTypeNames is effectively final
                if (entityTypeNames.contains(event.getValue())) {
                    relationshipSelector.setVisible(true);
                } else {
                    relationshipSelector.setVisible(false);
                    relationshipSelector.clear();
                }
            });

            Button saveFieldBtn = new Button("Save", ev -> {
                if (!fieldName.isEmpty() && fieldType.getValue() != null) {
                    // Get the relationship value, which may be null
                    String relationship = relationshipSelector.getValue();
                    FieldModel newField = new FieldModel(null, fieldName.getValue(), fieldType.getValue(), required.getValue(), relationship);
                    tempFieldModels.add(newField);
                    renderFieldRow(newField, fieldsLayout, tempFieldModels, entityToEdit);
                    fieldDialog.close();
                } else {
                    Notification.show("Please fill all field details");
                }
            });

            // Add the new radio button group to the dialog
            fieldDialog.add(fieldName, fieldType, relationshipSelector, required, saveFieldBtn);
            fieldDialog.open();
        });
    }

    /**
     * --- MODIFIED ---
     * Now saves the relationshipType when creating or updating an entity.
     */
    private Button createSaveEntityButton(boolean isEditing, EntityModel entityToEdit, Dialog dialog, TextField entityNameField, List<FieldModel> tempFieldModels, Project selectedProject) {
        return new Button(isEditing ? "Update Entity" : "Save Entity", e -> {
            String entityName = entityNameField.getValue();
            if (entityName == null || entityName.trim().isEmpty()) {
                Notification.show("Please provide an entity name.");
                return;
            }

            if (isEditing) {
                entityRepository.findByIdWithFields(entityToEdit.getId()).ifPresent(existingEntity -> {
                    existingEntity.setName(entityName);

                    // Add new fields (removals are handled by the 'X' button)
                    tempFieldModels.stream()
                            .filter(fm -> fm.getId() == null)
                            .forEach(newFieldModel -> {
                                Field newField = new Field(newFieldModel.getName(), newFieldModel.getType(), newFieldModel.isRequired(), newFieldModel.getRelationshipType());
                                existingEntity.getFields().add(newField);
                            });

                    entityRepository.save(existingEntity);
                    Notification.show("Entity '" + entityName + "' updated.");
                });
            } else {
                // Create a new entity
                List<Field> fieldList = tempFieldModels.stream()
                        .map(fm -> new Field(fm.getName(), fm.getType(), fm.isRequired(), fm.getRelationshipType()))
                        .collect(Collectors.toList());
                Entity newEntity = new Entity(entityName, fieldList);
                newEntity.setProject(selectedProject);
                entityRepository.save(newEntity);
                Notification.show("Entity '" + entityName + "' saved to project '" + selectedProject.getName() + "'.");
            }

            dialog.close();
            refreshEntityGrid();
        });
    }
}