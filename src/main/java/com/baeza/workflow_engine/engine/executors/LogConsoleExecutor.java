package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import org.springframework.stereotype.Component;

@Component
public class LogConsoleExecutor implements NodeExecutor {

    @Override
    public NodeType getType() {
        return NodeType.LOG_CONSOLE;
    }

    @Override
    public void execute(WorkflowNode node, String executionId) {
        // Pega na mensagem de dentro do JSON de configuração do nó
        String message = (String) node.config().getOrDefault("message", "Mensagem vazia");

        System.out.println("🖥️ [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + " -> " + message);
    }
}