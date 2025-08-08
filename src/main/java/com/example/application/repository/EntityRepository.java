package com.example.application.repository;

import com.example.application.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional; // Make sure to import Optional

public interface EntityRepository extends JpaRepository<Entity, Long> {

    // This method causes the LazyInitializationException in some contexts
    List<Entity> findByProjectId(Long projectId);

    // This correctly loads entities and their fields for the grid
    @Query("SELECT e FROM Entity e LEFT JOIN FETCH e.fields WHERE e.project.id = :projectId")
    List<Entity> findByProjectIdWithFields(@Param("projectId") Long projectId);

    // --- ADD THIS NEW METHOD ---
    // This will find a single entity and its fields, preventing the exception
    @Query("SELECT e FROM Entity e LEFT JOIN FETCH e.fields WHERE e.id = :id")
    Optional<Entity> findByIdWithFields(@Param("id") Long id);
}