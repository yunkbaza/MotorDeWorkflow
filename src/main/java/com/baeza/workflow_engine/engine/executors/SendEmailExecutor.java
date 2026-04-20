package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import org.springframework.stereotype.Component;

@Component
public class SendEmailExecutor implements NodeExecutor {

    @Override
    public NodeType getType() {
        return NodeType.SEND_EMAIL;
    }

    @Override
    public void execute(WorkflowNode node, String executionId) {
        String emailDestino = (String) node.config().get("to");
        String subject = (String) node.config().get("subject");

        System.out.println("📧 [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + " -> A enviar email para: " + emailDestino + " com assunto: " + subject);
    }
}