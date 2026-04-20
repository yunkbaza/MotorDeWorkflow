package com.baeza.workflow_engine.domain;

import java.util.Map;

// O 'Record' no Java cria automaticamente construtores, getters e o toString.
// É perfeito para dados imutáveis.
public record WorkflowNode(
        String id,
        NodeType type,
        Map<String, Object> config
) {}