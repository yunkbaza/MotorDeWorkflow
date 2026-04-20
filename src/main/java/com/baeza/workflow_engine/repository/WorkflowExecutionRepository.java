package com.baeza.workflow_engine.repository;

import com.baeza.workflow_engine.entity.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, UUID> {
    // Só com esta linha, já temos os métodos save(), findById(), findAll(), delete() prontos a usar!
}