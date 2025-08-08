package com.example.application.service.generator;

import com.example.application.entity.Project;

/**
 * Defines a contract for classes that generate project build files.
 */
public interface BuildFileGenerator {
    /**
     * The name of the build tool this generator supports (e.g., "Maven", "Gradle").
     * This will be used as the key in the generator map.
     * @return The build tool name.
     */
    String getBuildToolName();

    /**
     * The name of the file to be generated (e.g., "pom.xml").
     * @return The file name.
     */
    String getFileName();

    /**
     * Generates the content of the build file.
     * @param project The project metadata.
     * @return A string containing the full content of the build file.
     */
    String generate(Project project);
}