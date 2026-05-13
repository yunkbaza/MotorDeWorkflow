package com.baeza.workflow_engine.repository;

import com.baeza.workflow_engine.entity.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, String> {
}