---
id: ref-descoberta-repo-para-fichas
nome: Descoberta — do repositório para o conjunto de fichas do LeanIX
tipo: referencia_skill
versao: '1.1'
fonte: 'Meta-modelo real do tenant configurado (dataModel + tagGroups) + heurística de varredura de fonte.'
status: ativo
pertence_a: skill leanix-descoberta-fichas
---

# Descoberta — varrer um repositório e identificar TODAS as fichas

Objetivo: dado um **repositório ou pasta de documentos**, varrer o fonte, **inferir todos os Fact
Sheets** que aquele sistema geraria no LeanIX (não só a app principal — também IT Components,
Interfaces, Data Objects, Provider, etc.), com **tipo + subtipo + tags + relações**, e entregar o
conjunto de fichas para cadastro. Um sistema vira **vários** Fact Sheets ligados, nunca um só.

> **Regra de ouro:** todo item inferido traz **origem** (arquivo:trecho). O que não tem evidência no
> fonte é **lacuna** (campo a preencher), nunca suposição. Tecnologia sempre com **versão** quando o
> fonte declarar (lockfile/manifesto/Dockerfile); ausente = "— não declarada".

## 1. O que varrer (ordem de prioridade)

1. **Manifestos e metadados do projeto:** `README*`, `package.json`, `pyproject.toml`,
   `requirements.txt`, `*.csproj`, `pom.xml`, `build.gradle(.kts)`, `go.mod`, `Cargo.toml`.
2. **Lockfiles** (versão efetiva): `package-lock.json`, `yarn.lock`, `pnpm-lock.yaml`, `poetry.lock`,
   `Pipfile.lock`, `packages.lock.json`, `go.sum`, `gradle.lockfile`.
3. **Contêiner/infra:** `Dockerfile`, `docker-compose.yml`, `*.tf`, manifests k8s, `helm/`.
4. **Integração/config:** `mcp.json`/`mcpServers`, specs OpenAPI/Swagger, `.proto`, config de filas/
   tópicos, SDKs de serviços externos, `.env.example` (nomes de serviço, não segredos).
5. **IA/agente:** config de modelo (`model: ...`), prompts, definição de ferramentas/tools.
6. **Dados:** entidades/models/schemas, migrations, coleções, índices, vector store.
7. **Propriedade/ciclo:** `CODEOWNERS`, `author`/`maintainers`, tags de release, status (POC/prod).

> Repositório grande: delegue a varredura a um **sub-agente de contexto** e trabalhe sobre o retorno.

## 2. Sinal no repo → Fact Sheet (tipo · subtipo · tag · relação)

| Sinal no fonte | Fact Sheet (tipo/subtipo) | Tag(s) | Como liga |
|---|---|---|---|
| Nome/propósito do sistema (README, `name`) | **Application** (subtipo conforme §3 de `estrutura-subtipos-relacoes`) | ABB de Aplicação; Linguagem | nó central |
| System prompt + LLM + loop de agente | **Application / aiAgent** | ABB "AI Agent" | usa AI Model; expõe/consome MCP |
| Serviço deployável isolado | **Application / microservice** | ABB "Service Layer…" | N:M com business application |
| Plugin/extensão de um host | **Application / extension** | — | liga ao host (Application) |
| Modelo/LLM (`gpt-4o`, `claude-3-5`, Bedrock) | **ITComponent / aiModel (ou llm)** | — | Provider → AI Model → App |
| Fornecedor do modelo (OpenAI/Anthropic/AWS/Azure/Google) | **Provider** | — | `relITComponentToProvider` |
| Runtime/linguagem/framework/lib (lockfile) **com versão** | **ITComponent / software** | Linguagem; Artefato de Software | `relApplicationToITComponent` |
| Banco relacional (Postgres/MySQL/Oracle) | **ITComponent / software (ou service)** | SGBD "Relacional (SQL)" | + Provider + Tech Category |
| Cache (Redis/Memcached) | **ITComponent / service** | SGBD "Em Memória (Cache)" | idem |
| Mongo / OpenSearch / Cassandra / Neo4j | **ITComponent** | SGBD "Documento / Busca e Indexação / Colunar / Grafo" | idem |
| Vector store (pgvector/Pinecone/OpenSearch) | **ITComponent** | SGBD conforme | usado pelo agente |
| Hospedagem (App Service/Functions/EC2/contêiner) | **ITComponent / paas · iaas · service** | — | `relApplicationToITComponent` |
| SaaS consumido (não é o sistema) | **ITComponent / saas** (+ Application se for a solução de negócio — exceção SaaS) | — | ver exceção SaaS |
| Servidor MCP (`mcp.json`, `mcpServers`) | **Interface / mcpServer** | — | provider/consumer + Data Object |
| API REST/gRPC exposta/consumida (OpenAPI/.proto) | **Interface / api** | — | duas pontas + Data Object |
| Tópico/fila de eventos (Kafka/SNS/SQS/pub-sub) | **Interface / Event** | — | duas pontas + Data Object |
| Pipeline/ETL em lote | **Interface / ETL** | — | duas pontas + Data Object |
| Entidade/model/schema/coleção/índice | **DataObject** | — | `relApplicationToDataObject` (CRUD) |
| Dado pessoal/sensível (LGPD) | **DataObject** `dataClassification` + sinalizar privacidade | — | não transcrever o dado |
| Capacidade de negócio apoiada (domínio) | **BusinessCapability** (reusar existente) | — | `relApplicationToBusinessCapability` |
| Processo/jornada/produto apoiado | **BusinessContext** (subtipo) | ABBs de Processos/Cadeia de valor | `relApplicationToBusinessContext` |
| Área dona/usuária (CODEOWNERS/author) | **Organization** (subtipo) + Subscription (pessoa) | ABBs de Organização | `relApplicationToOrganization` |
| Contrato/licença do fornecedor | **Contract** (subtipo) *(custo é restrito)* | — | liga a App/ITComponent/Provider |
| Ambiente/instância SAP | **System / sap** | — | liga a Application/ITComponent |
| Iniciativa/projeto que criou o sistema | **Initiative** (subtipo) | Estilo de Iniciativas | `relInitiativeToApplication` |
| Meta/OKR que o sistema atende | **Objective** | Direcional Estratégico | `relObjectiveToBusinessCapability` |
| Serviço exposto pela app ("oferta") | **ApplicationServices** | — | `relApplicationServicesToApplication` |

> **Exceção SaaS:** produto SaaS que **entrega capacidade de negócio** e precisa rastrear hosting →
> crie **Application** (funcional) **+** ITComponent/saas (placeholder técnico).

## 3. Passo a passo da descoberta

1. **Escopo** — confirme a pasta/repo e se é **um** sistema ou vários (mono-repo). Um sistema por
   grupo de fichas.
2. **Varrer e coletar evidência** (arquivo:trecho) por sinal da §1–§2.
3. **Levantar a stack COM VERSÃO** — abra lockfiles/manifestos/Dockerfile; cada tecnologia relevante
   vira um **ITComponent** com nome + **versão efetiva** (preferir o lock à faixa), alias (pacote),
   ecossistema, subtipo, fornecedor, dependência direta×transitiva, Tech Category sugerida e origem.
4. **Classificar cada item** no tipo/subtipo (use `leanix-classificar-metamodelo` em dúvidas) e
   **aplicar tags** extraíveis (`tags-grupos-leanix`).
5. **Montar uma ficha por Fact Sheet** — **formato** em `template-ficha-cadastro.md`; **campos do
   tipo/subtipo** no template do tipo (`templates-por-tipo/TPL-NN-<Tipo>.md`); tags em
   `tags-grupos-leanix.md`; **relações e ontologia** em `estrutura-subtipos-relacoes.md`.
5b. **Enriquecer cada ficha (OBRIGATÓRIO — SKILL.md §2C):**
   - **Descrição rica:** escreva `description` de 2–5 frases **sintetizada do fonte** (propósito,
     escopo, encaixe no negócio, integrações) — não copie o `metadata.description`. Com origem.
   - **Responsável/assinatura:** ache no fonte (CODEOWNERS, `spec.owner`, author/maintainers). Pessoa →
     **Responsible**; só grupo → vira a Organization/team (passo 5c) e a pessoa fica **lacuna**.
   - **Capacidade de negócio:** infira a BusinessCapability apoiada; **procure via MCP** →
     **reusar** (ID) se existir, **sugerir cadastro** de capacidade nova se não existir (ou sem MCP).
   - **Organização/equipe:** o time dono (`spec.owner` = `group:default/tribo-...`) vira
     **Organization subtipo `team`**; **procure via MCP** → **reusar** (ID) ou **sugerir cadastro**.
   - **Governança de IA (se houver sinal de IA):** há LLM/modelo, agente, SDK de IA, prompts,
     `mcp.json` ou ML? Então **exiba os campos de IA** na ficha — `lxAiUsage`/`lxAiType`/`lxAiPotential`/
     `lxAiRisk` (Application), subtipo `aiAgent` (`lxAiAgentType`/`lxAiAgentNature`/`lxAiVerified`),
     `lxAiTechnologyTaxonomy`/`lxAiVerified` (ITComponent/aiModel), `lxAiVerified`/`lxAvailableTools`/
     `lxMcpServer*` (Interface/mcpServer). Preencha o inferível (com origem); risco/verificação sem
     evidência = lacuna "a definir pelo COE de IA / autoridade de arquitetura/governança". Nunca inventar o risco.
6. **Conectar o grafo** respeitando a ontologia (Provider→ITComponent→Application; Interface com as
   duas pontas + Data Object; IT Component com Provider+Tech Category+App; App→Capability; App→Organization).
7. **(Opcional) MCP** — se houver, confirme campos/valores ao vivo e **reuse** Fact Sheets existentes
   (AI Model, Provider, IT Components por nome+versão, **BusinessCapability**, **Organization/team**,
   Interfaces, Data Objects): referencie o **ID** em vez de duplicar; o que não existir vira **sugestão
   de cadastro**. Sem MCP, marque "verificar reúso/match via MCP no cadastro".
8. **Entregar** o conjunto: lista de fichas (criar × reusar), diagrama do grafo instanciado, resumo
   (nº de fichas por tipo, lacunas por severidade) e **próximo passo** (ordem de cadastro: Provider →
   AI Model/ITComponents → Application → Interfaces/Data Objects → ancoragem de negócio).

## 4. Entregável (conjunto de fichas) — agrupado por APLICAÇÃO

**Regra de organização — agrupar por Application.** A unidade de saída é a **Application** (nó central;
Interfaces/IT Components/Data Objects pertencem a uma app). A estrutura de pastas depende de quantas
Applications o repositório gera:

- **1 Application (comum)** → um conjunto na pasta do repo:
  ```
  <saida>/<repo>/
  ├── fichas-leanix.md      (consolidado)
  ├── leanix-applications.md · leanix-itcomponents.md · leanix-interfaces.md · leanix-dataobjects.md   (os 4 base, sempre)
  ├── leanix-providers.md · leanix-businesscapabilities.md · leanix-businesscontexts.md · leanix-objectives.md …   (um por tipo adicional identificado)
  ```
- **2+ Applications (mono-repo ou repo que gera várias apps)** → **divida por aplicação**, uma subpasta
  por app:
  ```
  <saida>/<repo>/
  ├── _INDICE.md            (lista as apps do repo + links)
  ├── <app-A>/  → fichas-leanix.md + leanix-<tipo>.md por tipo (os 4 base + Provider/Capability/Context/Objective… quando houver)
  └── <app-B>/  → idem
  ```
  Cada subpasta contém **só** os Fact Sheets daquela app. Fact Sheets **compartilhados** entre apps
  (Provider, IT Component de plataforma, Organization/team, Capability) aparecem em cada app que os usa,
  marcados como **reúso (mesmo ID/nome)** — nunca duplicados como fichas distintas.

Em cada conjunto (de 1 app), entregue **um consolidado + um arquivo por tipo**. O conteúdo é o mesmo;
muda a granularidade de leitura.

### 4.1 Consolidado — `fichas-leanix.md` (visão geral do sistema)
Um arquivo com a visão do sistema inteiro (ver SKILL.md §2C.7 — regra dura de formato):
- **`## 0. Evidência coletada` NUMERADA `E1…En`** — tabela `# | Sinal | Valor | Origem (arquivo:trecho)`.
  Os arquivos por tipo **citam o código `Ex`** (ex.: "E12") na coluna Origem, em vez de repetir o
  `arquivo:trecho`. (Rastreabilidade: a evidência vive numerada num só lugar e os demais arquivos a referenciam.)
- **Inventário de fichas:** tabela `Fact Sheet | Tipo/Subtipo | Nome | Tags | Status
  (criar/reusar/validar) | Reúso por ID (tenant) | Origem`. A coluna **Reúso por ID** usa os estados
  padronizados: `ID real` · `reúso a confirmar via MCP` · `não existe → sugerir cadastro`.
- **`## Divergências registradas`** — rótulo/versão/nome declarado no catálogo × realidade do fonte
  (ex.: `framework: strands` × runtime LangGraph; pg16 × pg17 do tenant). Diga o que prevalece
  (regra: o fonte) e a recomendação. Sem divergência: "nenhuma divergência identificada".
- **Uma ficha por Fact Sheet** (resumida) + **grafo** (Mermaid, cores oficiais por tipo) + **resumo**
  (fichas por tipo, lacunas por severidade, ordem de cadastro).

### 4.2 Um arquivo POR TIPO de metamodelo (fichas detalhadas — para cadastrar no LeanIX)
Além do consolidado, gere **um arquivo por tipo** (`leanix-<tipo em minúsculas>.md`), cada ficha
**campo a campo** no formato dos `templates-por-tipo/TPL-NN-<Tipo>.md` (todos os campos, rótulo PT,
agrupamento temático, checkbox).

> **Não se limite aos 4 base.** Os 4 primeiros (Application, ITComponent, Interface, DataObject) saem
> **sempre**. Os demais tipos saem **quando houver evidência** no fonte — cada tipo identificado vira
> seu próprio `leanix-<tipo>.md` (mesmo padrão, usando o `TPL-NN` do tipo). Tipo **sem** sinal no fonte
> **não** vira arquivo (fica como lacuna no consolidado) — **nunca invente um tipo sem evidência**.

> **Regra dura (SKILL.md §2C.7.1): ficha campo a campo por ITEM — nunca só tabela consolidada.** Vale
> inclusive para os tipos de volume (ITComponent, Interface, DataObject com muitos itens): a tabela
> consolidada é só o **inventário de abertura** do arquivo e vem **seguida** das fichas detalhadas.
> Padrão permitido (igual ao conjunto de referência): detalhar as fichas representativas e fechar com
> **"demais itens seguem o mesmo padrão"** + os valores que variam (nome, versão, subtipo). Campos de
> custo aparecem como `(RESTRITO — não exibir)`, não removidos (§2C.7.5).

| Arquivo | Conteúdo | Diagrama obrigatório |
|---|---|---|
| **`leanix-applications.md`** | cada Application (todos os campos do TPL-01; governança de IA e autenticação quando houver) | a app ligada a Capability/Organization/Interfaces/IT Components/Data Objects |
| **`leanix-itcomponents.md`** | cada IT Component com **versão** (TPL-02; obrigatórios lifecycle/lxTechnologyAssessment*) | **Provider → IT Component → Application(s)** que o consomem |
| **`leanix-interfaces.md`** | cada Interface (TPL-03; category/dataFlowDirection/interfaceType/frequency/lxAuthentication) | **PROVEDOR → Interface → CONSUMIDOR** + o Data Object que trafega |
| **`leanix-dataobjects.md`** | cada Data Object (TPL-04; `dataClassification`, PII/LGPD sem transcrever o dado) | **em qual(is) Interface(s) trafega** + Applications que fazem CRUD |

**Arquivos adicionais — gere quando houver evidência (não se limite aos 4 base):**

| Arquivo | Tipo (TPL) | Conteúdo | Diagrama obrigatório |
|---|---|---|---|
| **`leanix-providers.md`** | Provider (TPL-09) | cada fornecedor/dono por trás de IT Components e sistemas integrados (SaaS, cloud, times donos de API) | **Provider → IT Components / Interfaces** que fornece |
| **`leanix-businesscapabilities.md`** | BusinessCapability (TPL-06) | a(s) capacidade(s) de negócio que a app apoia (§2C.3; linguagem de negócio, "o quê") | **Application → BusinessCapability** (+ hierarquia da capacidade) |
| **`leanix-businesscontexts.md`** | BusinessContext (TPL-07) | o processo/jornada de negócio que o sistema apoia | **Application → BusinessContext** |
| **`leanix-objectives.md`** | Objective (TPL-14) | o objetivo/OKR que o sistema serve | **Objective → Application / Capability** |
| **`leanix-<tipo>.md`** | demais TPL-NN | qualquer outro tipo identificado com evidência (Organization, Platform, TechnicalStack/Tech Category, Initiative…) | vínculo do tipo com a Application/ecossistema |

> **Por que os dois:** o consolidado dá a visão do sistema; os arquivos por tipo dão o detalhe campo a
> campo que o time usa para **cadastrar no LeanIX**. Os **4 base** criam o arquivo mesmo sem Fact Sheet
> no fonte (com "nenhum \<tipo\> com evidência (lacuna) — a levantar quando houver código" + o que seria
> esperado); os **tipos adicionais** só geram arquivo **quando a evidência existir** — sem sinal no
> fonte, o tipo fica apenas como lacuna no consolidado, sem arquivo próprio.

- O cadastro é **manual, pelo time** — a skill entrega as fichas estruturadas (LeanIX é leitura).

> **Agente de IA não tem fluxo separado:** é apenas uma **Application subtipo `aiAgent`**. O repositório
> de um agente gera Application/`aiAgent` + IT Component/`aiModel` (o LLM) + Interfaces + Data Objects,
> pelas mesmas regras desta descoberta — com a governança de IA (SKILL.md §2C.5) e a autenticação
> (§2C.6). Use o template `templates-por-tipo/TPL-01-Application.md` (categoria `aiAgent`).
