package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import com.baeza.workflow_engine.engine.utils.StringInterpolator;
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
        // Pega a mensagem bruta e passa pelo nosso Canivete Suíço! 🔪
        String rawMessage = (String) node.config().getOrDefault("message", "Mensagem vazia");
        String message = StringInterpolator.interpolate(rawMessage, context);

        System.out.println("🖥️ [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + "\n" + message + "\n");

        return message;
    }
}