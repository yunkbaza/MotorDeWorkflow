# 🚀 Motor de Workflow BazaBank (Enterprise Edition)

Um motor de orquestração de workflows assíncrono, resiliente e baseado em grafos, construído do zero com **Spring Boot 4.0.5** e **Java 21**. Este sistema permite a criação, execução e monitorização de processos de negócio complexos através de uma arquitetura plugável e altamente escalável.

## 🏗️ Arquitetura e Engenharia de Elite

O projeto foi desenhado utilizando padrões da indústria para suportar fluxos de trabalho distribuídos de nível *Enterprise*:

* **Navegação em Grafo (BFS):** O núcleo do motor utiliza um algoritmo de busca em largura (*Breadth-First Search*) para percorrer os nós através de arestas (*edges*), suportando ramificações dinâmicas.
* **Processamento Assíncrono (*Fire and Forget*):** Execução do trabalho pesado em *background* utilizando a anotação `@Async`. A API devolve um `202 Accepted` em milissegundos, evitando bloqueios no cliente.
* **Padrão *Strategy* (Executores Plugáveis):** Cada tipo de nó tem o seu próprio executor isolado, tornando simples adicionar novas capacidades sem alterar o motor principal.
* **Resiliência Nativa (*Auto-Retry*):** Sistema de retentativas automático configurável por nó, garantindo que falhas temporárias na rede não quebrem o fluxo.
* **Memória Global Partilhada (Contexto):** Um "cofre" de dados que viaja com a execução. Os nós podem ler e escrever variáveis, permitindo interpolação dinâmica (ex: `{{nome_cliente}}`).
* **Memória de Longo Prazo (Templates):** Separação clara entre a *definição* do fluxo (salvo no banco de dados) e a *execução* (instâncias a correr).

## 🛠️ Stack Tecnológico

* **Linguagem:** Java 21 (`Records` e `HttpClient` nativo).
* **Framework:** Spring Boot 4.0.5.
* **Persistência de Dados:** PostgreSQL & Spring Data JPA (Hibernate).
* **Segurança:** Spring Security (Proteção *Stateless* via `X-API-KEY`).
* **Manipulação de Dados:** Jackson Databind (Conversão de JSON e extração cirúrgica de dados).
* **Infraestrutura:** Docker & Docker Compose.

## 🔌 Tipos de Nós Suportados

O motor suporta diversos tipos de operações:

| Tipo de Nó | Descrição |
| :--- | :--- |
| `HTTP_REQUEST` | Chamadas REST para APIs externas com interpolação de URLs. |
| `JSON_EXTRACT` | Extrai valores específicos de payloads JSON complexos. |
| `DECISION` | O "cérebro" condicional (If/Else) que direciona o fluxo com base nos dados. |
| `SEND_EMAIL` | Executa o disparo de e-mails dinâmicos utilizando variáveis do contexto. |
| `LOG_CONSOLE` | Regista mensagens formatadas no terminal para auditoria. |

## 🚀 Como Executar Localmente

### 1. Subir a Base de Dados (PostgreSQL)
Certifica-te de que tens o Docker instalado e inicia o banco de dados:
```bash
docker-compose up -d

```

### 2. Configurar a Chave de Segurança

Confirma que o ficheiro `application.properties` tem a tua API Key definida:

```properties
workflow.api.key=BazaMasterKey2026

```

### 3. Iniciar a Aplicação

Compile e arranque o servidor:

```bash
./mvnw spring-boot:run

```

## 📬 Documentação da API

**Nota:** Todas as chamadas requerem o cabeçalho: `X-API-KEY: BazaMasterKey2026`.

### 1. Guardar uma Planta (Template)

`POST /api/workflows/templates`
Guarda a definição do fluxo de trabalho no banco de dados.

### 2. Disparar Workflow (Gatilho)

`POST /api/workflows/trigger/{templateId}`
Inicia uma execução em *background* a partir de um Template salvo.
**Payload:** `{ "nome_cliente": "Allan Baeza" }`

### 3. Execução Direta (Draft)

`POST /api/workflows/execute`
Testa um grafo completo sem o guardar permanentemente.

### 4. Monitorização

* `GET /api/workflows/executions`: Lista todo o histórico.
* `GET /api/workflows/executions/{id}`: Detalha o estado de uma execução específica.

---

Desenvolvido por **Allan Gabriel Baeza Amirati Silva** - *Backend Engineer*
