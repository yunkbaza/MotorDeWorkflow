package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import com.baeza.workflow_engine.engine.utils.StringInterpolator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JsonExtractExecutor implements NodeExecutor {

    // O Jackson é o padrão ouro do Java para lidar com JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NodeType getType() {
        return NodeType.JSON_EXTRACT;
    }

    @Override
    public Object execute(WorkflowNode node, String executionId, Map<String, Object> context) {
        // 1. Onde está o JSON gigante? (ex: "{{dados_endereco}}")
        String rawSource = (String) node.config().get("source");
        String jsonString = StringInterpolator.interpolate(rawSource, context);

        // 2. Qual é a chave que queremos roubar? (ex: "logradouro")
        String field = (String) node.config().get("field");

        System.out.println("🔍 [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + " -> Extraindo a chave: '" + field + "'");

        try {
            // Transforma o texto numa árvore navegável
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode targetNode = rootNode.path(field);

            if (targetNode.isMissingNode()) {
                System.out.println("⚠️ Chave '" + field + "' não encontrada no JSON.");
                return null;
            }

            // Devolve o valor limpo para o Cofre de Memória!
            return targetNode.asText();

        } catch (Exception e) {
            throw new RuntimeException("Falha ao tentar extrair o campo '" + field + "' do JSON: " + e.getMessage());
        }
    }
}