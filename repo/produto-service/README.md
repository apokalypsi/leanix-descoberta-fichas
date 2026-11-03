# produto-service

> ⚠️ **Aviso — projeto de teste gerado por IA.** Este `produto-service` **não é um sistema real**.
> Foi criado artificialmente, gerado por IA, com o único objetivo de servir de **alvo de teste para
> validar as skills de descoberta do LeanIX** neste template. Não está em produção e **não deve ser
> usado como referência de segurança ou de implementação**. Credenciais, senhas e configurações aqui
> presentes são **exemplos fictícios** (ex.: senhas default, serviços sem autenticação) e existem
> apenas para o cenário de teste — **não** refletem práticas de produção.

Sistema de exemplo em **Java 21 / Spring Boot 3.3** para servir de alvo de teste da descoberta LeanIX.
CRUD de **Produto** com:

- **API REST** de CRUD (`/api/v1/produtos`) protegida por **OAuth2** (JWT Bearer, Resource Server) com **idempotência** por header `Idempotency-Key`;
- **Consumo de evento Kafka** (tópico `estoque.movimentos`) para ajuste de estoque, idempotente por `eventId`;
- **Cache e idempotência em Redis** (cache de produto e de endereço; chave de idempotência com TTL);
- **Integração com o ViaCEP** para enriquecer o endereço de origem do produto a partir do CEP;
- Persistência em **PostgreSQL** com migrations **Flyway**;
- **OpenAPI/Swagger**, **Actuator** (health/metrics) e testes com **JUnit/Mockito**.

## Arquitetura (visão rápida)

```
Cliente ──REST──> ProdutoController ──> ProdutoService ──> ProdutoRepository ──> PostgreSQL
                                   │                   └──> ViaCepClient ──HTTP──> ViaCEP (cache Redis)
                                   └──> IdempotencyService ──> Redis (SET NX + TTL)

Kafka (estoque.movimentos) ──> EstoqueEventConsumer ──(idempotente)──> ProdutoService
```

- **Segurança:** OAuth2 Resource Server — a API valida o JWT emitido pelo Authorization Server (`OAUTH2_ISSUER_URI`). Escritas exigem o scope `produtos:write`, leituras `produtos:read`. Health e Swagger ficam públicos.
- **ITComponents** esperados: Spring Boot, Spring Security / OAuth2 Resource Server, PostgreSQL, Redis, Apache Kafka, Flyway, springdoc, Lombok, MapStruct.
- **Interfaces** esperadas: `produto-service-api` (REST, expõe), `estoque.movimentos` (Kafka Event, consome), `viacep-api` (REST, consome).
- **Data Objects** esperados: Produto, Endereço (ViaCEP), Evento de Estoque.
- **Provider** esperado: ViaCEP (externo), AWS (hospedagem, conforme catalog).

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/v1/produtos` | Cria produto (idempotente via `Idempotency-Key`) |
| GET | `/api/v1/produtos` | Lista produtos |
| GET | `/api/v1/produtos/{id}` | Busca por id (cacheado) |
| PUT | `/api/v1/produtos/{id}` | Atualiza produto |
| DELETE | `/api/v1/produtos/{id}` | Remove produto |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui.html` | Documentação OpenAPI |

## Evento Kafka consumido

Tópico `estoque.movimentos`, payload JSON:

```json
{ "eventId": "evt-123", "sku": "TEC-001", "delta": -2, "origem": "pedido-service" }
```

`delta` positivo entra no estoque, negativo sai (nunca abaixo de zero). `eventId` garante idempotência.

## Como rodar

Suba a infraestrutura e a aplicação com Docker Compose:

```bash
docker compose up --build
```

Ou localmente (precisa de PostgreSQL, Redis e Kafka — veja `.env.example`):

```bash
mvn spring-boot:run
```

Testes:

```bash
mvn test
```

## Estrutura

```
src/main/java/com/exemplo/produtoservice/
├── ProdutoServiceApplication.java
├── config/        RestClient, Redis cache, Kafka consumer, OpenAPI
├── controller/    ProdutoController (REST CRUD)
├── service/       ProdutoService (regras + cache)
├── repository/    ProdutoRepository (JPA)
├── domain/        Produto (entidade)
├── dto/           ProdutoRequest, ProdutoResponse, EnderecoViaCep
├── mapper/        ProdutoMapper (MapStruct)
├── client/        ViaCepClient (integração ViaCEP, cacheada)
├── idempotency/   IdempotencyService (Redis SET NX)
├── kafka/         EstoqueEvent, EstoqueEventConsumer
└── exception/     exceções de domínio + GlobalExceptionHandler
```

> Projeto sintético, sem dados reais — feito para exercitar a descoberta de Fact Sheets do LeanIX.
