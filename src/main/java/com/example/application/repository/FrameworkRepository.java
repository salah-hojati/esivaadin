package com.example.application.repository;

import com.example.application.entity.Framework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrameworkRepository extends JpaRepository<Framework, Long> {
    Optional<Framework> findByName(String name);


}

