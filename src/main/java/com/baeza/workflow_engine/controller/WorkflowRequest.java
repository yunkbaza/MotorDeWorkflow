package com.baeza.workflow_engine.controller;

import com.baeza.workflow_engine.domain.WorkflowGraph;
import java.util.Map;

// O envelope que a nossa API vai receber!
public record WorkflowRequest(
        WorkflowGraph graph,
        Map<String, Object> initialContext
) {}