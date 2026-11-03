---
id: ref-tags-grupos-leanix
nome: Tag groups configurados do tenant configurado (por tipo, modo, valores)
tipo: referencia_skill
versao: '1.0'
fonte: 'Extraído do tenant configurado via GraphQL allTagGroups (20 grupos). Extração AAAA-MM-DD.'
status: ativo
pertence_a: skill leanix-descoberta-fichas
---

# Tag groups configurados (classificação transversal)

As **tags** são classificação transversal (não é campo, não é Fact Sheet). O tenant tem **20 tag
groups** configurados. Cada grupo é `SINGLE` (uma tag) ou `MULTIPLE` (várias), restrito a certos
tipos de Fact Sheet. Ao montar uma ficha, **aplique as tags dos grupos que valem para o tipo** — e,
na varredura de repositório, várias tags saem direto do fonte (linguagem, tipo de banco, artefato).

> **Use apenas as tags listadas aqui** (ou reconfirme via MCP/GraphQL). Não invente tag nem grupo.
> Tags não substituem campo nem relação — são rótulo. Se algo tem identidade/ciclo de vida próprio,
> é Fact Sheet; se é dado estável da FS, é campo; se é classificação pequena/transversal, é tag.

## Grupos por tipo de Fact Sheet

### Application
- **ABBs de Aplicação** (MULTIPLE; também ITComponent, Platform) — a "categoria de aplicação" (o que o
  sistema É funcionalmente). 41 tags: AI Agent · AI Assistant · BPMN · BRMS · BaaS · BackEnd/Core
  Systems · CCM · CMS · CRM · Contact Center · DAM · DCS · DMS · DXP · Data Lake · Decision Support
  System (DSS/SSD) · ECM · ERP · Event Layer · External Systems · FSM · FastData Layer · FrontEnd
  Systems · HR Platform · Instant Messaging Platforms · LMS · MAM · Mobile App · Module · OAS · OLAP ·
  Process Mining · Project Management Tool · RPA · SSO · Service Layer (PaaS + Microservices) ·
  Serviço de Geolocalização e Mapas · Sourcing Platform · Support Systems · WCMS · WebApp.
- **ABB de Aplicação - Framework de Negócios** (MULTIPLE) — Motor Avaliativo · Motor de Aprendizagem.
- **Linguagem de Programação** (MULTIPLE; também ITComponent) — C# · Java · Kotlin · Node · Python.
  → **extraível do repo** (manifestos/lockfiles/extensões de arquivo).
- **Especificação de Linguagem** (MULTIPLE; também ITComponent) — JDK 9 · JDK 10 · JDK 11 · JDK 12 ·
  JDK 21. → extraível do repo (`global.json`, `pom.xml`, imagem base).

### ITComponent
- **ABBs de Aplicação** (MULTIPLE — compartilhado) · **Linguagem de Programação** · **Especificação de
  Linguagem** (ver acima; extraíveis do repo).
- **Tipo de Ativo de Rede** (MULTIPLE) — CDN · DNS · Domínio · Subdomínio · VPN.
- **Tipo Sistema de Gerenciamento de Banco de Dados** (MULTIPLE) — Em Memória (Cache) · Ledger ·
  NoSQL - Busca e Indexação · NoSQL - Chave-valor · NoSQL - Colunar · NoSQL - Documento · NoSQL - Grafo
  · Relacional (SQL). → **extraível do repo** (compose/IaC/drivers: Postgres=Relacional, Redis=Cache,
  Mongo=Documento, OpenSearch=Busca e Indexação, Neo4j=Grafo…).
- **Tipo de Artefato de Software** (MULTIPLE) — Biblioteca · Database IDE · Framework de
  Desenvolvimento · IDE · Linguagem de Dados · Linguagem de Estilo/Front-end · Linguagem de Marcação ·
  Linguagem de Programação · Linguagem de Script · Linguagem de Template · Linguagem para Back-end ·
  Linguagem para Desktop · Linguagem para Front-end · Linguagem para Mobile.

### BusinessContext
- **ABBs de Organização** (SINGLE; também Organization) — Aliança de negócio · Corporativo ·
  Plataforma · SQUAD · Tribo · Área de Negócio.
- **ABBs de Cadeia de valor** (SINGLE) — Cadeias de Valor · Jornadas da Arquitetura de Negócio.
- **ABBs de Processos** (SINGLE) — Etapa (Subprocesso) · Macroprocesso · Processo.
- **Produtos Comerciais** (SINGLE) — Segmento · Subdivisão · Tipo.
- **Layers TO** (MULTIPLE; também Objective) — Aliança PV1..PV4 · Corporativo · Plataforma.

### Organization
- **ABBs de Organização** (SINGLE — compartilhado; ver BusinessContext).

### Objective
- **Direcional Estratégico** (SINGLE) — Ambição · KR Anual · KR Must Win Battle · Must Win Battle ·
  Objetivo Anual.
- **Layers TO** (MULTIPLE — compartilhado com BusinessContext).

### Initiative
- **PRIME** (MULTIPLE) — COGDP.
- **Estilo de Iniciativas** (MULTIPLE) — Estruturante · Oportunidade · Quick Win · Racionalização ·
  Transformação.
- **Tipo de Impacto** (MULTIPLE) — Engajamento · Estratégico · Experiência · Financeiro ·
  Operacional · Regulatório/Compliance · Reputacional.
- **Atores Impactados** (MULTIPLE) — Cliente · Corporativo · Lideranças · Ponta
  (Parceiros/Polos/Franquias) · Público Externo · Área de Negócio.

### Todos os tipos
- **Iniciativas de TO** (SINGLE) — Iniciativa de TO.
- **Metas Converge** (SINGLE) — Ciclo 1 · Ciclo 2 (após data de corte).

## Tags extraíveis direto do repositório (varredura)

| Sinal no repo | Tag group | Tag(s) |
|---|---|---|
| `package.json`/Node, `.ts/.js` | Linguagem de Programação | Node |
| `requirements.txt`/`pyproject`, `.py` | Linguagem de Programação | Python |
| `pom.xml`/`build.gradle`, `.java` | Linguagem de Programação | Java |
| `.kt`, Kotlin no gradle | Linguagem de Programação | Kotlin |
| `.csproj`, `.cs` | Linguagem de Programação | C# |
| `global.json`/`pom.xml` (JDK) | Especificação de Linguagem | JDK 9/10/11/12/21 |
| Postgres/MySQL/Oracle/SQL Server | Tipo SGBD | Relacional (SQL) |
| Redis/Memcached | Tipo SGBD | Em Memória (Cache) |
| MongoDB | Tipo SGBD | NoSQL - Documento |
| OpenSearch/Elasticsearch | Tipo SGBD | NoSQL - Busca e Indexação |
| Cassandra/Bigtable | Tipo SGBD | NoSQL - Colunar |
| Neo4j | Tipo SGBD | NoSQL - Grafo |
| CDN/DNS/VPN em IaC | Tipo de Ativo de Rede | CDN/DNS/VPN |
| biblioteca/framework no lockfile | Tipo de Artefato de Software | Biblioteca / Framework de Desenvolvimento |
| agente autônomo / assistente | ABBs de Aplicação | AI Agent / AI Assistant |
| microserviço | ABBs de Aplicação | Service Layer (PaaS + Microservices) / Module |
| front SPA/web | ABBs de Aplicação | WebApp / FrontEnd Systems |
| API/back-end | ABBs de Aplicação | BackEnd/Core Systems / Service Layer |

> As demais tags (ABBs de negócio, Pilares, Direcional Estratégico, Produtos) são **decisão de
> negócio/arquitetura**, não saem do código — marque "a definir pelo time" na ficha.
