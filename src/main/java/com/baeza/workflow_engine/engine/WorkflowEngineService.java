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

    public WorkflowExecution runWorkflow(WorkflowGraph graph) {
        WorkflowExecution execution = repository.save(new WorkflowExecution(graph.workflowId()));
        System.out.println("\n🚀 Iniciando Workflow: " + graph.name() + " | ID: " + execution.getId());

        try {
            execution.setStatus(ExecutionStatus.RUNNING);
            repository.save(execution);

            // --- ARQUITETURA SÉNIOR: PREPARAÇÃO DO GRAFO ---

            // 1. Otimização O(1): Dicionário rápido de Nós
            Map<String, WorkflowNode> nodeMap = graph.nodes().stream()
                    .collect(Collectors.toMap(WorkflowNode::id, Function.identity()));

            // 2. Descobrir quem são os "Alvos" (Targets) para encontrar as Raízes
            Set<String> targetIds = graph.edges().stream()
                    .map(edge -> edge.target())
                    .collect(Collectors.toSet());

            // 3. Identificar os nós iniciais (Os que não são alvo de ninguém)
            List<WorkflowNode> startNodes = graph.nodes().stream()
                    .filter(node -> !targetIds.contains(node.id()))
                    .toList();

            if (startNodes.isEmpty()) {
                throw new IllegalStateException("Grafo inválido: Ciclo infinito ou sem ponto de partida detectado!");
            }

            // --- ARQUITETURA SÉNIOR: EXECUÇÃO BFS COM MEMÓRIA (CONTEXTO) ---

            Queue<WorkflowNode> queue = new LinkedList<>(startNodes);
            Set<String> visited = new HashSet<>();

            // O COFRE! Aqui guardamos o que cada nó produziu para os próximos poderem usar.
            Map<String, Object> workflowContext = new java.util.concurrent.ConcurrentHashMap<>();

            while (!queue.isEmpty()) {
                WorkflowNode currentNode = queue.poll();

                if (visited.contains(currentNode.id())) continue;
                visited.add(currentNode.id());

                execution.setCurrentNodeId(currentNode.id());
                repository.save(execution);

                NodeExecutor executor = executors.get(currentNode.type());
                if (executor == null) {
                    throw new IllegalArgumentException("Motor não tem executor para o tipo: " + currentNode.type());
                }

                // 🌟 EXECUTA E GUARDA O RESULTADO NA MEMÓRIA GLOBAL!
                Object result = executor.execute(currentNode, execution.getId().toString(), workflowContext);
                if (result != null) {
                    // Guarda na memória com a chave sendo o ID do nó (ex: "n1": "dados do http...")
                    workflowContext.put(currentNode.id(), result);
                }

                List<WorkflowNode> nextNodes = graph.edges().stream()
                        .filter(edge -> edge.source().equals(currentNode.id()))
                        .map(edge -> nodeMap.get(edge.target()))
                        .toList();

                queue.addAll(nextNodes);
            }
            // --- FIM COM SUCESSO ---
            execution.setStatus(ExecutionStatus.COMPLETED);
            execution.setCompletedAt(LocalDateTime.now());
            System.out.println("🧠 Memória Final do Workflow: " + workflowContext);

        } catch (Exception e) {
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setCompletedAt(LocalDateTime.now());
            System.err.println("❌ Erro catastrófico no workflow: " + e.getMessage() + "\n");
        }

        return repository.save(execution);
    }
}