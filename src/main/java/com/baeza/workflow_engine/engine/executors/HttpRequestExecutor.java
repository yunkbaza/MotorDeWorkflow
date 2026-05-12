package com.baeza.workflow_engine.engine.executors;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.engine.NodeExecutor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class HttpRequestExecutor implements NodeExecutor {

    // Cliente HTTP moderno do Java 21 (rápido e nativo)
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public NodeType getType() { return NodeType.HTTP_REQUEST; }

    @Override
    public Object execute(WorkflowNode node, String executionId, Map<String, Object> context) {
        String url = (String) node.config().get("url");
        String method = (String) node.config().getOrDefault("method", "GET");

        System.out.println("🌐 [EXECUÇÃO " + executionId + "] | Nó: " + node.id() + " -> A fazer " + method + " para " + url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method(method, HttpRequest.BodyPublishers.noBody()) // MVP: sem body para já
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("✅ [SUCESSO HTTP] Status: " + response.statusCode());

            // Devolvemos o corpo da resposta (ex: um JSON em string) para ser guardado na memória do motor!
            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Falha na requisição HTTP no nó " + node.id() + ": " + e.getMessage());
        }
    }
}