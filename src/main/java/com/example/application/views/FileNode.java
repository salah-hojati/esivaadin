package com.example.application.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple data model to represent a file or directory in a tree structure.
 */
public class FileNode {
    private final String name;
    private final String fullPath;
    private final boolean isDirectory;
    private boolean selected = true; // Default to selected
    private final FileNode parent;
    private final List<FileNode> children = new ArrayList<>();

    public FileNode(String name, String fullPath, boolean isDirectory, FileNode parent) {
        this.name = name;
        this.fullPath = fullPath;
        this.isDirectory = isDirectory;
        this.parent = parent;
    }

    public void addChild(FileNode child) {
        children.add(child);
    }

    // Getters
    public String getName() { return name; }
    public String getFullPath() { return fullPath; }
    public boolean isDirectory() { return isDirectory; }
    public boolean isSelected() { return selected; }
    public FileNode getParent() { return parent; }
    public List<FileNode> getChildren() { return children; }

    // Setter for selection
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileNode fileNode = (FileNode) o;
        return Objects.equals(fullPath, fileNode.fullPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullPath);
    }
}