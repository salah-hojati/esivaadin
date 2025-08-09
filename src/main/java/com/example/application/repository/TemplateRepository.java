package com.example.application.repository;

import com.example.application.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    /**
     * Finds a template by its specific characteristics.
     */
    Optional<Template> findFirstByBuildToolAndProjectTypeAndFrameworkAndTemplateName(
        String buildTool, String projectType, String framework, String templateName
    );

    @Query("SELECT t FROM Template t WHERE t.buildTool IN ( :buildTool , '*') AND " +
            "t.projectType IN (:projectType, '*') AND " +
            "t.framework IN (:framework, '*')")
    List<Template> findApplicableTemplates(@Param("buildTool") String buildTool,
                                           @Param("projectType") String projectType,
                                           @Param("framework") String framework);
}