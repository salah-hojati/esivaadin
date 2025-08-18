package com.example.application.repository;

import com.example.application.entity.Template;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//@Transactional(Transactional.TxType.REQUIRES_NEW)

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    // This method is now simpler as templateName is unique
    Optional<Template> findByTemplateName(String templateName);
}