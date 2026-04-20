package com.baeza.workflow_engine.engine;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;

public interface NodeExecutor {
    // Diz ao motor que tipo de tarefa esta classe sabe resolver
    NodeType getType();

    // Onde a lógica real vai acontecer
    void execute(WorkflowNode node, String executionId);
}