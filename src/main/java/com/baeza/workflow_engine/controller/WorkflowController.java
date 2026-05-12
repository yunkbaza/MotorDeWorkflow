package com.baeza.workflow_engine.controller;

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
    public ResponseEntity<WorkflowExecution> executeWorkflow(@RequestBody WorkflowRequest request) {
        WorkflowExecution execution = engineService.startWorkflow(request.graph(), request.initialContext());
        return ResponseEntity.accepted().body(execution); // Retorna 202 imediatamente!
    }
}