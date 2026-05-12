package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendEmailExecutor implements NodeExecutor {

    @Override
    public NodeType getType() { return NodeType.SEND_EMAIL; }

    @Override
    public Object execute(WorkflowNode node, String executionId, Map<String, Object> context) {
        String to = (String) node.config().get("to");
        String subject = (String) node.config().get("subject");

        System.out.println("📧 [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + " -> Email enviado para: " + to);
        return Map.of("status", "enviado", "destinatario", to); // Opcional: devolver dados úteis
    }
}