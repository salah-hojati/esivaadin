package com.example.application.repository;

import com.example.application.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    Optional<ProjectFile> findByFileName(String fileName);
}