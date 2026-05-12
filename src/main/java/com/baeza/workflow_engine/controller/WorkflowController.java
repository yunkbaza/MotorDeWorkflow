package com.baeza.workflow_engine.controller;

import com.baeza.workflow_engine.domain.WorkflowGraph;
import com.baeza.workflow_engine.engine.WorkflowEngineService;
import com.baeza.workflow_engine.entity.WorkflowExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowEngineService engineService;

    public WorkflowController(WorkflowEngineService engineService) {
        this.engineService = engineService;
    }

    @PostMapping("/execute")
    public ResponseEntity<WorkflowExecution> executeWorkflow(@RequestBody WorkflowGraph graph) {
        WorkflowExecution execution = engineService.runWorkflow(graph);
        return ResponseEntity.ok(execution);
    }
}