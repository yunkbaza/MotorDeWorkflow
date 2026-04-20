package com.baeza.workflow_engine.entity;

public enum ExecutionStatus {
    PENDING,    // Na fila para começar
    RUNNING,    // A ser processado agora mesmo
    COMPLETED,  // Terminou com sucesso
    FAILED      // Deu erro (ex: API externa caiu)
}