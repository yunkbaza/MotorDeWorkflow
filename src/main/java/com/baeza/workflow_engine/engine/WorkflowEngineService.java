package com.baeza.workflow_engine.engine;

import com.baeza.workflow_engine.domain.NodeType;
import com.baeza.workflow_engine.domain.WorkflowGraph;
import com.baeza.workflow_engine.domain.WorkflowNode;
import com.baeza.workflow_engine.entity.ExecutionStatus;
import com.baeza.workflow_engine.entity.WorkflowExecution;
import com.baeza.workflow_engine.repository.WorkflowExecutionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WorkflowEngineService {

    private final WorkflowExecutionRepository repository;
    private final Map<NodeType, NodeExecutor> executors;

    public WorkflowEngineService(
            WorkflowExecutionRepository repository,
            List<NodeExecutor> executorList
    ) {
        this.repository = repository;
        this.executors = executorList.stream()
                .collect(Collectors.toMap(NodeExecutor::getType, Function.identity()));
    }

    // 1. Este método é muito rápido. Só salva no banco como PENDING e devolve a resposta para a API!
    public WorkflowExecution startWorkflow(WorkflowGraph graph, Map<String, Object> initialContext) {
        WorkflowExecution execution = repository.save(new WorkflowExecution(graph.workflowId()));
        System.out.println("\n⏳ Recebido Workflow: " + graph.name() + " | ID: " + execution.getId());

        // Dispara o trabalho pesado noutra Thread e não espera que ele acabe!
        processGraphAsync(execution, graph, initialContext);

        return execution;
    }

    // 2. O trabalho pesado roda em Background!
    @org.springframework.scheduling.annotation.Async
    public void processGraphAsync(WorkflowExecution execution, WorkflowGraph graph, Map<String, Object> initialContext) {
        System.out.println("🚀 Executando em Background | ID: " + execution.getId());

        try {
            execution.setStatus(ExecutionStatus.RUNNING);
            repository.save(execution);

            Map<String, WorkflowNode> nodeMap = graph.nodes().stream().collect(Collectors.toMap(WorkflowNode::id, Function.identity()));
            Set<String> targetIds = graph.edges().stream().map(edge -> edge.target()).collect(Collectors.toSet());
            List<WorkflowNode> startNodes = graph.nodes().stream().filter(node -> !targetIds.contains(node.id())).toList();

            Queue<WorkflowNode> queue = new LinkedList<>(startNodes);
            Set<String> visited = new HashSet<>();
            Map<String, Object> workflowContext = new java.util.concurrent.ConcurrentHashMap<>(initialContext != null ? initialContext : Map.of());

            // --- LOOP PRINCIPAL DO MOTOR ---
            while (!queue.isEmpty()) {
                WorkflowNode currentNode = queue.poll();

                if (visited.contains(currentNode.id())) continue;
                visited.add(currentNode.id());

                execution.setCurrentNodeId(currentNode.id());
                repository.save(execution);

                NodeExecutor executor = executors.get(currentNode.type());

                int maxRetries = (currentNode.config().containsKey("retries")) ? (int) currentNode.config().get("retries") : 0;
                int attempts = 0;
                boolean success = false;
                Exception lastException = null;
                Object result = null; // Variável criada fora para podermos usar no Routing!

                // --- 🛡️ SISTEMA DE RETENTATIVAS ---
                while (attempts <= maxRetries && !success) {
                    try {
                        // 🌟 EXECUTA E GUARDA O RESULTADO NA MEMÓRIA GLOBAL!
                        result = executor.execute(currentNode, execution.getId().toString(), workflowContext);
                        if (result != null) {
                            workflowContext.put(currentNode.id(), result);
                        }
                        success = true; // Se chegou aqui, não deu erro! Pode sair do loop de retries.
                    } catch (Exception e) {
                        attempts++;
                        lastException = e;
                        if (attempts <= maxRetries) {
                            System.out.println("⚠️ Nó " + currentNode.id() + " falhou. Tentando novamente (" + attempts + "/" + maxRetries + ")...");
                            Thread.sleep(2000);
                        }
                    }
                } // Fim do Loop de Retries

                // Se o nó falhou definitivamente, o workflow é abortado!
                if (!success) {
                    throw new RuntimeException("Nó falhou após " + maxRetries + " tentativas! Erro: " + lastException.getMessage());
                }

                // --- 🔀 A MAGIA DO ROUTING (IF / ELSE) ---
                String routeDecision = null;
                if (currentNode.type() == NodeType.DECISION && result != null) {
                    routeDecision = String.valueOf(result); // O executor de Decisão escolheu o caminho!
                }

                // O Java pede que a variável seja 'final' para usar dentro do .filter()
                final String finalRouteDecision = routeDecision;

                // 4. Descobrir os próximos caminhos (Edges)
                List<WorkflowNode> nextNodes = graph.edges().stream()
                        .filter(edge -> edge.source().equals(currentNode.id()))
                        // Se não for decisão (null) passa tudo. Se for decisão, a seta tem de ter o mesmo label (ex: "SP")
                        .filter(edge -> finalRouteDecision == null || finalRouteDecision.equals(edge.label()))
                        .map(edge -> nodeMap.get(edge.target()))
                        .toList();

                queue.addAll(nextNodes);

            } // FIM DO LOOP PRINCIPAL WHILE(!queue.isEmpty())

            // --- CONCLUÍDO COM SUCESSO ---
            execution.setStatus(ExecutionStatus.COMPLETED);
            execution.setCompletedAt(LocalDateTime.now());
            System.out.println("🧠 Memória Final: " + workflowContext);
            System.out.println("✅ Workflow concluído em Background com sucesso!\n");

        } catch (Exception e) {
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setCompletedAt(LocalDateTime.now());
            System.err.println("❌ Workflow Abortado no Background: " + e.getMessage() + "\n");
        }

        repository.save(execution);
    }
}