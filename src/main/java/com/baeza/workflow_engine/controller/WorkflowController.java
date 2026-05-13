package com.baeza.workflow_engine.controller;

import com.baeza.workflow_engine.domain.WorkflowGraph;
import com.baeza.workflow_engine.engine.WorkflowEngineService;
import com.baeza.workflow_engine.entity.WorkflowExecution;
import com.baeza.workflow_engine.entity.WorkflowTemplate;
import com.baeza.workflow_engine.repository.WorkflowExecutionRepository;
import com.baeza.workflow_engine.repository.WorkflowTemplateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowEngineService engineService;
    private final WorkflowTemplateRepository templateRepository;
    private final WorkflowExecutionRepository executionRepository; // Para monitorização
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkflowController(
            WorkflowEngineService engineService,
            WorkflowTemplateRepository templateRepository,
            WorkflowExecutionRepository executionRepository) {
        this.engineService = engineService;
        this.templateRepository = templateRepository;
        this.executionRepository = executionRepository;
    }

    // 1. Execução Direta (Draft/Teste)
    @PostMapping("/execute")
    public ResponseEntity<?> executeWorkflow(@RequestBody WorkflowRequest request) {
        if (request == null || request.graph() == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Payload inválido."));
        }
        WorkflowExecution execution = engineService.startWorkflow(request.graph(), request.initialContext());
        return ResponseEntity.accepted().body(execution);
    }

    // 2. Salvar Template
    @PostMapping("/templates")
    public ResponseEntity<?> createTemplate(@RequestBody WorkflowGraph graph) {
        try {
            String graphJson = objectMapper.writeValueAsString(graph);
            WorkflowTemplate template = new WorkflowTemplate(graph.workflowId(), graph.name(), graphJson);
            templateRepository.save(template);
            return ResponseEntity.ok(Map.of("mensagem", "Template salvo!", "templateId", graph.workflowId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));
        }
    }

    // 3. Gatilho via ID (Produção)
    @PostMapping("/trigger/{templateId}")
    public ResponseEntity<?> triggerTemplate(@PathVariable String templateId, @RequestBody Map<String, Object> initialContext) {
        return templateRepository.findById(templateId)
                .map(template -> {
                    try {
                        WorkflowGraph graph = objectMapper.readValue(template.getGraphJson(), WorkflowGraph.class);
                        WorkflowExecution execution = engineService.startWorkflow(graph, initialContext);
                        return ResponseEntity.accepted().body(execution);
                    } catch (Exception e) {
                        return ResponseEntity.internalServerError().body(Map.of("erro", "Erro no template."));
                    }
                }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Monitorização: Listar tudo
    @GetMapping("/executions")
    public ResponseEntity<List<WorkflowExecution>> listExecutions() {
        return ResponseEntity.ok(executionRepository.findAll());
    }

    // 5. Monitorização: Detalhe por ID
    @GetMapping("/executions/{id}")
    public ResponseEntity<WorkflowExecution> getExecution(@PathVariable UUID id) {
        return executionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}