package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import com.baeza.workflow_engine.engine.utils.StringInterpolator;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class HttpRequestExecutor implements NodeExecutor {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public NodeType getType() { return NodeType.HTTP_REQUEST; }

    @Override
    public Object execute(WorkflowNode node, String executionId, Map<String, Object> context) {
        // 🌟 Interpola o URL! Se houver um {{cep}} no cofre, ele substitui aqui.
        String rawUrl = (String) node.config().get("url");
        String finalUrl = StringInterpolator.interpolate(rawUrl, context);

        String method = (String) node.config().getOrDefault("method", "GET");

        System.out.println("🌐 [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + " -> " + method + " " + finalUrl);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(finalUrl))
                    .method(method, HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("A API retornou erro: " + response.statusCode());
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Falha HTTP: " + e.getMessage());
        }
    }
}