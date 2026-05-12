package com.baeza.workflow_engine.domain;

public record WorkflowEdge(
        String source,
        String target,
        String label
) {}