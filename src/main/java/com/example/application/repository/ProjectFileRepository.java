package com.example.application.repository;

import com.example.application.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
}