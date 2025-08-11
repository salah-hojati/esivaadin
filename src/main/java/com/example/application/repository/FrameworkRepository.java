package com.example.application.repository;

import com.example.application.entity.Framework;
import com.example.application.entity.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrameworkRepository extends JpaRepository<Framework, Long> {
    Optional<Framework> findByName(String name);


  /*  @Query("SELECT f FROM Framework f WHERE :projectType MEMBER OF f.projectTypes OR f.appliesToAll = true")
    List<Framework> findByProjectTypeOrAll(@Param("projectType") ProjectType projectType);*/
}


