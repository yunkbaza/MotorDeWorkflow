package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import com.baeza.workflow_engine.engine.utils.StringInterpolator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DecisionExecutor implements NodeExecutor {

    @Override
    public NodeType getType() {
        return NodeType.DECISION;
    }

    @Override
    public Object execute(WorkflowNode node, String executionId, Map<String, Object> context) {
        // Pega o valor que queremos avaliar (ex: "{{estado_cliente}}")
        String rawValue = (String) node.config().get("value");

        // Converte na variável real que está na memória (ex: "SP")
        String evaluatedValue = StringInterpolator.interpolate(rawValue, context);

        System.out.println("🔀 [DECISÃO " + executionId + "] | O motor avaliou o caminho para: '" + evaluatedValue + "'");

        // Devolve o valor. O "Cérebro" vai usar isto para escolher a seta certa!
        return evaluatedValue;
    }
}