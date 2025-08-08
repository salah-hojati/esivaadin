package com.example.application.views;

import com.example.application.entity.*;
import com.example.application.repository.EntityRepository;
import com.example.application.repository.FrameworkRepository;
import com.example.application.repository.ProjectRepository;
import com.example.application.repository.ProjectTypeRepository;
import com.example.application.service.CodeGeneratorService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
public class MainView extends VerticalLayout {
    // ... (fields declarations are unchanged) ...
    private ComboBox<Project> projectSelector;
    private TextField newProjectName;
    private ComboBox<ProjectType> projectTypeCombo; // Changed to hold ProjectType objects
    private RadioButtonGroup<String> buildToolSelector;
    private ComboBox<String> javaVersionCombo;
    private Button saveProjectButton;
    private Button downloadProjectButton;
    private Button newProjectButton;
    private Button showGeneratedFilesButton;
    private ComboBox<String> webFrameworkCombo;
    private ComboBox<String> apiFrameworkCombo;
    private ComboBox<String> jobFrameworkCombo;
    private ComboBox<String> toolsCombo;
    private Button addEntityButton;
    private Grid<EntityModel> entityGrid;
    private final EntityRepository entityRepository;
    private final ProjectRepository projectRepository;
    private final ProjectTypeRepository projectTypeRepository;
    private final FrameworkRepository frameworkRepository;
    private final CodeGeneratorService codeGeneratorService;


    public MainView(EntityRepository entityRepository, ProjectRepository projectRepository,
                    ProjectTypeRepository projectTypeRepository, FrameworkRepository frameworkRepository,
                    CodeGeneratorService codeGeneratorService) {
        this.entityRepository = entityRepository;
        this.projectRepository = projectRepository;
        this.projectTypeRepository = projectTypeRepository;
        this.frameworkRepository = frameworkRepository;
        this.codeGeneratorService = codeGeneratorService;

        createAdminMenu();
        add(new H1("Project Management"));
        setupProjectLayout();
        add(new H1("Entities"));
        setupEntityLayout();
        refreshProjectSelector();
        updateUIState();
    }

    private void createAdminMenu() {
        HorizontalLayout menu = new HorizontalLayout();
        menu.setSpacing(true);
        menu.getStyle().set("padding-bottom", "20px");
        Button projectTypesButton = new Button("Manage Project Types", e -> UI.getCurrent().navigate(ProjectTypeAdminView.class));
        Button frameworksButton = new Button("Manage Frameworks", e -> UI.getCurrent().navigate(FrameworkAdminView.class));
        Button templatesButton = new Button("Manage Templates", e -> UI.getCurrent().navigate(TemplateAdminView.class));
        menu.add(projectTypesButton, frameworksButton, templatesButton);
        add(menu);
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

        // MODIFIED: Use ProjectType objects and add the listener
        projectTypeCombo = new ComboBox<>("Project Type");
        projectTypeCombo.setItems(projectTypeRepository.findAll());
        projectTypeCombo.setItemLabelGenerator(ProjectType::getName);
        projectTypeCombo.setPlaceholder("Select project type");
        projectTypeCombo.addValueChangeListener(e -> updateFrameworkCombosVisibility(e.getValue()));

        buildToolSelector = new RadioButtonGroup<>();
        buildToolSelector.setLabel("Build Tool");
        buildToolSelector.setItems("Maven", "Gradle");

        javaVersionCombo = new ComboBox<>("Java Version");
        javaVersionCombo.setItems("8", "11", "17", "21");
        javaVersionCombo.setPlaceholder("Select version");

        // Framework combos are now created without items initially
        webFrameworkCombo = new ComboBox<>("Web Framework");
        webFrameworkCombo.setVisible(false);
        apiFrameworkCombo = new ComboBox<>("API Framework");
        apiFrameworkCombo.setVisible(false);
        jobFrameworkCombo = new ComboBox<>("Job Framework");
        jobFrameworkCombo.setVisible(false);
        toolsCombo = new ComboBox<>("Tool");
        toolsCombo.setVisible(false);

        HorizontalLayout projectDetailsLayout = new HorizontalLayout(newProjectName, projectTypeCombo, buildToolSelector, javaVersionCombo);
        projectDetailsLayout.setAlignItems(Alignment.BASELINE);
        HorizontalLayout frameworkLayout = new HorizontalLayout(webFrameworkCombo, apiFrameworkCombo, jobFrameworkCombo, toolsCombo);
        frameworkLayout.setAlignItems(Alignment.BASELINE);
        saveProjectButton = new Button("Save Project", e -> saveOrUpdateProject());
        downloadProjectButton = new Button("Download Project", e -> downloadProject());
        showGeneratedFilesButton = new Button("Show Generated Files", e -> showGeneratedFilesInNewTab());
        newProjectButton = new Button("New Project", e -> projectSelector.clear());
        HorizontalLayout buttonLayout = new HorizontalLayout(saveProjectButton, downloadProjectButton, showGeneratedFilesButton, newProjectButton);
        add(projectSelector, projectDetailsLayout, frameworkLayout, buttonLayout);
    }

    // MODIFIED: Logic to dynamically filter frameworks
    private void updateFrameworkCombosVisibility(ProjectType projectType) {
        webFrameworkCombo.setVisible(false);
        apiFrameworkCombo.setVisible(false);
        jobFrameworkCombo.setVisible(false);
        toolsCombo.setVisible(false);

        webFrameworkCombo.clear();
        apiFrameworkCombo.clear();
        jobFrameworkCombo.clear();
        toolsCombo.clear();

        if (projectType == null) {
            return;
        }

        List<String> associatedFrameworks = projectType.getFrameworks().stream()
                .map(Framework::getName)
                .collect(Collectors.toList());

        switch (projectType.getName()) {
            case "Web":
                webFrameworkCombo.setItems(associatedFrameworks);
                webFrameworkCombo.setVisible(true);
                break;
            case "API":
                apiFrameworkCombo.setItems(associatedFrameworks);
                apiFrameworkCombo.setVisible(true);
                break;
            case "Job":
                jobFrameworkCombo.setItems(associatedFrameworks);
                jobFrameworkCombo.setVisible(true);
                break;
            case "Tools":
                toolsCombo.setItems(associatedFrameworks);
                toolsCombo.setVisible(true);
                break;
        }
    }

    // MODIFIED: Logic to handle setting the ProjectType object
    private void handleProjectSelection(Project project) {
        if (project != null) {
            newProjectName.setValue(project.getName());
            buildToolSelector.setValue(project.getBuildTool());
            javaVersionCombo.setValue(project.getJavaVersion());

            projectTypeRepository.findByName(project.getProjectType()).ifPresent(pt -> {
                projectTypeCombo.setValue(pt); // This will trigger the listener
                String framework = project.getFramework();
                if (framework != null) {
                    switch (pt.getName()) {
                        case "Web":   webFrameworkCombo.setValue(framework); break;
                        case "API":   apiFrameworkCombo.setValue(framework); break;
                        case "Job":   jobFrameworkCombo.setValue(framework); break;
                        case "Tools": toolsCombo.setValue(framework); break;
                    }
                }
            });
        } else {
            newProjectName.clear();
            projectTypeCombo.clear();
            buildToolSelector.clear();
            javaVersionCombo.clear();
        }
    }

    // MODIFIED: Logic to get the project type name from the object
    private void saveOrUpdateProject() {
        String projectName = newProjectName.getValue();
        ProjectType selectedType = projectTypeCombo.getValue();
        String buildTool = buildToolSelector.getValue();
        String javaVersion = javaVersionCombo.getValue();

        if (projectName == null || projectName.trim().isEmpty() || selectedType == null || buildTool == null || javaVersion == null) {
            Notification.show("Please provide a project name, type, build tool, and Java version.");
            return;
        }

        String framework = null;
        if (webFrameworkCombo.isVisible()) framework = webFrameworkCombo.getValue();
        else if (apiFrameworkCombo.isVisible()) framework = apiFrameworkCombo.getValue();
        else if (jobFrameworkCombo.isVisible()) framework = jobFrameworkCombo.getValue();
        else if (toolsCombo.isVisible()) framework = toolsCombo.getValue();

        if (framework == null) {
            Notification.show("Please select a framework/tool for the project type '" + selectedType.getName() + "'.");
            return;
        }

        Project selectedProject = projectSelector.getValue();
        Project projectToSave = (selectedProject != null) ? selectedProject : new Project();

        projectToSave.setName(projectName.trim());
        projectToSave.setProjectType(selectedType.getName()); // Get name from object
        projectToSave.setBuildTool(buildTool);
        projectToSave.setFramework(framework);
        projectToSave.setJavaVersion(javaVersion);

        try {
            Project savedProject = projectRepository.save(projectToSave);
            Notification.show("Project '" + savedProject.getName() + "' saved successfully.");
            refreshProjectSelector();
            projectSelector.setValue(savedProject);
        } catch (Exception e) {
            Notification.show("Error: Could not save project. A project with this name might already exist.");
        }
    }

    // ... The rest of MainView.java remains the same ...
    private void showGeneratedFilesInNewTab() {
        Project selectedProject = projectSelector.getValue();
        if (selectedProject == null) {
            Notification.show("Please select a project to view its files.");
            return;
        }
        String url = "/files/" + selectedProject.getId();
        UI.getCurrent().getPage().open(url, "_blank");
    }

    private void setupEntityLayout() {
        addEntityButton = new Button("Add Entity", e -> openEntityDialog(null));
        entityGrid = new Grid<>(EntityModel.class, false);
        entityGrid.addColumn(EntityModel::getName).setHeader("Entity Name").setSortable(true);
        entityGrid.addColumn(entity -> entity.getFields().size()).setHeader("# Fields");

        entityGrid.addComponentColumn(entityModel -> {
            Button editButton = new Button("Edit");
            editButton.addClickListener(e -> openEntityDialog(entityModel));
            return editButton;
        }).setHeader("Edit");

        entityGrid.addComponentColumn(entityModel -> {
            Button removeButton = new Button("Remove");
            removeButton.getStyle().set("color", "var(--lumo-error-text-color)");
            removeButton.addClickListener(e -> confirmRemoveEntity(entityModel));
            return removeButton;
        }).setHeader("Remove");

        add(addEntityButton, entityGrid);
    }

    private void downloadProject() {
        Project selectedProject = projectSelector.getValue();
        if (selectedProject == null) {
            Notification.show("Please select a project to download.");
            return;
        }
        String downloadUrl = "/download/project/" + selectedProject.getId();
        final Anchor anchor = new Anchor(downloadUrl, "Download");
        anchor.getElement().setAttribute("download", true);
        anchor.getStyle().set("display", "none");
        add(anchor);
        anchor.getElement().executeJs("setTimeout(() => { this.click(); this.remove(); }, 0);");
        Notification.show("Project download started...");
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
        showGeneratedFilesButton.setEnabled(projectSelected);
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

        TextField relationship = new TextField("Relationship");
        if (fieldModel.getRelationshipType() != null) {
            relationship.setValue(fieldModel.getRelationshipType());
        } else {
            relationship.setVisible(false);
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

    private Button createAddFieldButton(VerticalLayout fieldsLayout, List<FieldModel> tempFieldModels, EntityModel entityToEdit) {
        return new Button("Add Field", e -> {
            Dialog fieldDialog = new Dialog();
            TextField fieldName = new TextField("Field Name");
            ComboBox<String> fieldType = new ComboBox<>("Type");
            Checkbox required = new Checkbox("Required");

            RadioButtonGroup<String> relationshipSelector = new RadioButtonGroup<>("Relationship Type");
            relationshipSelector.setItems("OneToOne", "ManyToOne", "OneToMany");
            relationshipSelector.setVisible(false);

            final List<String> standardTypes = List.of("String", "Integer", "Boolean", "Double", "Date");
            final Project currentProject = projectSelector.getValue();

            final List<String> entityTypeNames;
            if (currentProject != null) {
                entityTypeNames = entityRepository.findByProjectId(currentProject.getId()).stream()
                        .map(Entity::getName)
                        .filter(name -> entityToEdit == null || !name.equals(entityToEdit.getName()))
                        .collect(Collectors.toList());
            } else {
                entityTypeNames = new ArrayList<>();
            }

            List<String> allPossibleTypes = new ArrayList<>(standardTypes);
            allPossibleTypes.addAll(entityTypeNames);
            fieldType.setItems(allPossibleTypes);

            fieldType.addValueChangeListener(event -> {
                if (entityTypeNames.contains(event.getValue())) {
                    relationshipSelector.setVisible(true);
                } else {
                    relationshipSelector.setVisible(false);
                    relationshipSelector.clear();
                }
            });

            Button saveFieldBtn = new Button("Save", ev -> {
                if (!fieldName.isEmpty() && fieldType.getValue() != null) {
                    String relationship = relationshipSelector.getValue();
                    FieldModel newField = new FieldModel(null, fieldName.getValue(), fieldType.getValue(), required.getValue(), relationship);
                    tempFieldModels.add(newField);
                    renderFieldRow(newField, fieldsLayout, tempFieldModels, entityToEdit);
                    fieldDialog.close();
                } else {
                    Notification.show("Please fill all field details");
                }
            });

            fieldDialog.add(fieldName, fieldType, relationshipSelector, required, saveFieldBtn);
            fieldDialog.open();
        });
    }

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