package com.baeza.workflow_engine.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_templates")
public class WorkflowTemplate {

    @Id
    private String id; // Ex: "onboarding_baza_v1"

    @Column(nullable = false)
    private String name;

    // Vamos guardar o JSON bruto do Grafo para o podermos carregar dinamicamente
    @Column(columnDefinition = "TEXT", nullable = false)
    private String graphJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public WorkflowTemplate() {}

    public WorkflowTemplate(String id, String name, String graphJson) {
        this.id = id;
        this.name = name;
        this.graphJson = graphJson;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getGraphJson() { return graphJson; }
}