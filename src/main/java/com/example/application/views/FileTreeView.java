package com.example.application.views;

import com.example.application.entity.Entity;
import com.example.application.entity.Project;
import com.example.application.repository.EntityRepository;
import com.example.application.repository.ProjectRepository;
import com.example.application.service.CodeGeneratorService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.*;

@Route("files/:projectId") // The URL for this view, e.g., /files/1
@AnonymousAllowed // For simplicity, allows access without login
public class FileTreeView extends VerticalLayout implements BeforeEnterObserver {

    private final ProjectRepository projectRepository;
    private final EntityRepository entityRepository;
    private final CodeGeneratorService codeGeneratorService;

    private final H1 header = new H1();
    private final SplitLayout splitLayout = new SplitLayout();
    private final TreeGrid<FileNode> treeGrid = new TreeGrid<>();
    private final TextArea contentViewer = new TextArea();

    public FileTreeView(ProjectRepository projectRepository,
                        EntityRepository entityRepository,
                        CodeGeneratorService codeGeneratorService) {
        this.projectRepository = projectRepository;
        this.entityRepository = entityRepository;
        this.codeGeneratorService = codeGeneratorService;

        setSizeFull();
        configureLayout();
    }

    private void configureLayout() {
        contentViewer.setReadOnly(true);
        contentViewer.setSizeFull();

        treeGrid.addHierarchyColumn(FileNode::getName).setHeader("File Tree");
        treeGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(node -> {
                if (node.isLeaf()) {
                    // The content is now stored in the node itself
                    contentViewer.setValue(node.getContent());
                } else {
                    contentViewer.clear();
                }
            });
        });

        splitLayout.setSizeFull();
        splitLayout.addToPrimary(treeGrid);
        splitLayout.addToSecondary(contentViewer);
        splitLayout.setSplitterPosition(30);

        add(header, splitLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Get the project ID from the URL parameter
        Optional<Long> projectIdOpt = event.getRouteParameters().get("projectId").map(Long::parseLong);

        if (projectIdOpt.isEmpty()) {
            showError("No project ID was provided in the URL.");
            return;
        }

        Long projectId = projectIdOpt.get();
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            showError("Project with ID " + projectId + " could not be found.");
            return;
        }

        Project project = projectOpt.get();
        header.setText("Generated Files: " + project.getName());
        UI.getCurrent().getPage().setTitle("Files: " + project.getName());

        // Generate the file content and build the tree
        List<Entity> entities = entityRepository.findByProjectIdWithFields(project.getId());
        Map<String, String> projectFiles = codeGeneratorService.generateProjectFiles(project, entities);

        if (projectFiles.isEmpty()) {
            showError("No files were generated for this project.");
            return;
        }

        List<FileNode> rootNodes = buildFileTree(projectFiles);
        treeGrid.setItems(rootNodes, FileNode::getChildren);
    }

    private void showError(String message) {
        header.setText("Error");
        splitLayout.setVisible(false);
        add(message);
        Notification.show(message);
    }

    private List<FileNode> buildFileTree(Map<String, String> projectFiles) {
        Map<String, FileNode> nodeMap = new HashMap<>();
        List<FileNode> roots = new ArrayList<>();

        List<String> sortedPaths = new ArrayList<>(projectFiles.keySet());
        Collections.sort(sortedPaths);

        for (String path : sortedPaths) {
            String[] parts = path.split("/");
            FileNode parentNode = null;
            StringBuilder currentPath = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (currentPath.length() > 0) {
                    currentPath.append("/");
                }
                currentPath.append(part);
                String pathKey = currentPath.toString();

                FileNode currentNode = nodeMap.get(pathKey);
                if (currentNode == null) {
                    // If it's the last part, it's a file, so get its content
                    String content = (i == parts.length - 1) ? projectFiles.get(path) : null;
                    currentNode = new FileNode(part, pathKey, content);
                    nodeMap.put(pathKey, currentNode);

                    if (parentNode == null) {
                        roots.add(currentNode);
                    } else {
                        parentNode.getChildren().add(currentNode);
                    }
                }
                parentNode = currentNode;
            }
        }
        return roots;
    }

    // Helper class for the file tree, now with content
    public static class FileNode {
        private final String name;
        private final String fullPath;
        private final String content; // Store content for leaf nodes
        private final List<FileNode> children = new ArrayList<>();

        public FileNode(String name, String fullPath, String content) {
            this.name = name;
            this.fullPath = fullPath;
            this.content = content;
        }

        public String getName() { return name; }
        public String getFullPath() { return fullPath; }
        public String getContent() { return content; }
        public List<FileNode> getChildren() { return children; }
        public boolean isLeaf() { return children.isEmpty(); }
    }
}