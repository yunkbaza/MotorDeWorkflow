package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import com.baeza.workflow_engine.engine.utils.StringInterpolator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendEmailExecutor implements NodeExecutor {

    @Override
    public NodeType getType() { return NodeType.SEND_EMAIL; }

    @Override
    public Object execute(WorkflowNode node, String executionId, Map<String, Object> context) {
        // Agora o envio de email também aceita variáveis da memória!
        String rawTo = (String) node.config().get("to");
        String rawSubject = (String) node.config().get("subject");

        String to = StringInterpolator.interpolate(rawTo, context);
        String subject = StringInterpolator.interpolate(rawSubject, context);

        System.out.println("📧 [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + " -> Email enviado para: " + to + " | Assunto: " + subject);

        return Map.of("status", "enviado", "destinatario", to);
    }
}