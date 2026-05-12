package com.baeza.workflow_engine.engine;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;

import java.util.Map;

public interface NodeExecutor {
    NodeType getType();

    // Agora recebe o "contexto" (memória global) e devolve um resultado
    Object execute(WorkflowNode node, String executionId, Map<String, Object> context);
}