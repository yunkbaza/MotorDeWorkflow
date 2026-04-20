package com.baeza.workflow_engine.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_executions")
public class WorkflowExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "workflow_id", nullable = false)
    private String workflowId; // O ID do Grafo que está a ser executado

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    // Aqui guardamos em que nó o motor parou (perfeito para retomar se o PC desligar!)
    @Column(name = "current_node_id")
    private String currentNodeId;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // O JPA exige um construtor vazio
    public WorkflowExecution() {}

    public WorkflowExecution(String workflowId) {
        this.workflowId = workflowId;
        this.status = ExecutionStatus.PENDING;
        this.startedAt = LocalDateTime.now();
    }

    // --- GETTERS E SETTERS ---
    // (Podes gerar automaticamente no VS Code clicando com o botão direito -> Source Action -> Generate Getters and Setters)

    public UUID getId() { return id; }

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }

    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }

    public String getCurrentNodeId() { return currentNodeId; }
    public void setCurrentNodeId(String currentNodeId) { this.currentNodeId = currentNodeId; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}