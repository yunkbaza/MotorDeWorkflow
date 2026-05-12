package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogConsoleExecutor implements NodeExecutor {

    @Override
    public NodeType getType() {
        return NodeType.LOG_CONSOLE;
    }

    @Override
    public Object execute(WorkflowNode node, String executionId, Map<String, Object> context) {
        String message = (String) node.config().getOrDefault("message", "Mensagem vazia");

        // 🌟 A MAGIA DA INTERPOLAÇÃO: Troca as variáveis pelo que está na memória!
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String keyToFind = "{{" + entry.getKey() + "}}";
            if (message.contains(keyToFind) && entry.getValue() != null) {
                message = message.replace(keyToFind, entry.getValue().toString());
            }
        }

        System.out.println("🖥️ [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + "\n" + message + "\n");

        return message;
    }
}