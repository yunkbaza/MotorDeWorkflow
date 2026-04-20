package com.baeza.workflow_engine.domain;

import java.util.List;

public record WorkflowGraph(
        String workflowId,
        String name,
        List<WorkflowNode> nodes,
        List<WorkflowEdge> edges
) {}