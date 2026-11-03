---
id: ref-checklist-execucao-descoberta-fichas
nome: Checklist de Verificação de Execução — Descoberta/Ficha LeanIX
tipo: referencia_skill
versao: '1.0'
fonte: 'Arquitetura Corporativa; derivado do SKILL.md da leanix-descoberta-fichas (seções 0, 1, 2A, 2B, 2C, 4)'
revisao: '2026-11'
status: ativo
pertence_a: skill leanix-descoberta-fichas
---

# Checklist de verificação — a skill `leanix-descoberta-fichas` realmente executou tudo?

Use este checklist **no fim do trabalho**, antes de entregar, para confirmar que cada passo
obrigatório do `SKILL.md` foi de fato cumprido — não só iniciado. Marque `[x]` o que foi comprovado,
`[ ]` o que faltou (e corrija antes de entregar) e `N/A` o que não se aplica ao modo/contexto, **com
justificativa**. Um item sem evidência **não** pode ser marcado como feito.

> **Regra de ouro (vale para o checklist inteiro):** todo valor da ficha vem de **evidência**
> (o que o usuário informou OU `arquivo:trecho` do fonte) OU fica como **lacuna**. Se encontrar
> qualquer campo com valor inventado (sem origem), o item correspondente **falha**.

---

## Bloco A — Frescor dos templates (SKILL §0) · SEMPRE

- [ ] Li `metamodelo_extraido_em` do(s) `TPL-NN-<Tipo>.md` (ou do `_INDICE.md`) que usei e calculei os dias decorridos.
- [ ] Dias ≤ 30 → segui normalmente **OU** dias > 30 → **avisei o usuário** no início da resposta (template possivelmente defasado) e ofereci revalidar via MCP.
- [ ] Se revalidei via MCP: comparei campo a campo; atualizei só a **data** (meta-modelo igual) **ou** o conteúdo + `versao_template` (meta-modelo mudou), **preservando a estrutura** do template.
- [ ] Se não havia MCP/VPN: mantive o aviso de possível defasagem e declarei a limitação (não inventei mudança).

## Bloco B — Modo e entrada (SKILL §1)

- [ ] Identifiquei explicitamente o **modo**: `FICHA` (tipo específico) ou `DESCOBERTA` (repositório/pasta).
- [ ] **Modo DESCOBERTA:** o **alvo** (pasta local ou repositório) foi informado pelo usuário/orquestradora. Se não veio, **perguntei antes de varrer** — não assumi o workspace atual.
- [ ] Registrei no cabeçalho da entrega **qual alvo foi varrido** (caminho/repo + data).
- [ ] **Modo FICHA:** confirmei o **tipo** e o **subtipo** (`category`); em dúvida de classificação, acionei `leanix-classificar-metamodelo`.

---

## Bloco C — Modo FICHA (SKILL §2A) · pular se foi só DESCOBERTA

- [ ] **Passo 0** — tipo e subtipo confirmados (o subtipo define os campos condicionais).
- [ ] **Passo 1** — carreguei o `templates-por-tipo/TPL-NN-<Tipo>.md` do tipo (fonte de verdade dos campos).
- [ ] **Passo 2** — montei a ficha **campo a campo**: cada campo com tipo de dado, obrigatoriedade, opções (se seleção), **valor OU "— lacuna"** e **origem**.
- [ ] Filtrei os campos **condicionais** pelo subtipo escolhido (incluí só os aplicáveis).
- [ ] Respeitei os **obrigatórios** e o **workflow de aprovação** (ex.: `description` em Application/ITComponent; `Responsible` em Application).
- [ ] Cada campo classificado como **Conclusivo** / **Lacuna** / **Requer validação**.
- [ ] **Passo 3 (opcional)** — se havia MCP: confirmei campos/valores ao vivo e verifiquei **reúso** (não duplicar); se divergiu da referência, prevaleceu o MCP.
- [ ] **Passo 4** — entreguei ficha por seção + **Resumo** (preenchidos × lacunas; relações; tags) + **Próximo passo**, salvo em `documentos-gerados/`.

## Bloco D — Modo DESCOBERTA (SKILL §2B) · pular se foi só FICHA

- [ ] **Passo 0** — defini o escopo: **quantas Applications** o repo gera (1 → um conjunto; 2+ → subpasta por app + `_INDICE.md`); listei os arquivos relevantes (manifestos, lockfiles, Dockerfile/IaC, `mcp.json`, OpenAPI, schemas, CODEOWNERS).
- [ ] **Passo 1** — varri o fonte e coletei **cada sinal com origem (`arquivo:trecho`)**; levantei a **stack COM VERSÃO** (versão efetiva do lock; `latest`/ausente = "— não declarada").
- [ ] **Passo 2** — classifiquei **tipo + subtipo** de cada item e apliquei as **tags extraíveis do repo**.
- [ ] **Passo 3** — montei **uma ficha por Fact Sheet** no formato do template do tipo, com Conclusivo × Lacuna × Validar + origem.
- [ ] **Passo 4** — conectei o **grafo** respeitando a ontologia (Provider → ITComponent → Application; Interface com as 2 pontas + Data Object; IT Component com Provider + Tech Category + Application).
- [ ] **Passo 5 (opcional)** — com MCP, confirmei e **reusei** Fact Sheets por **ID**; sem MCP, marquei "verificar reúso/match no cadastro".
- [ ] **Passo 6** — entreguei o conjunto **agrupado por Application** em `documentos-gerados/`.

### D.1 — Arquivos obrigatórios do conjunto (SKILL §2B Passo 6)

- [ ] `fichas-leanix.md` (consolidado) presente, com: evidência numerada `E1…En` + inventário de fichas + ficha resumida por FS + `## Divergências registradas` + **grafo Mermaid** (cores oficiais) + resumo (fichas por tipo; lacunas por severidade; **ordem de cadastro**).
- [ ] **Os 4 arquivos base SEMPRE criados** (tipo sem evidência = "nenhum \<tipo\> — lacuna"): `leanix-applications.md`, `leanix-itcomponents.md`, `leanix-interfaces.md`, `leanix-dataobjects.md`.
- [ ] Arquivos **adicionais** gerados quando houve evidência: `leanix-providers.md`, `leanix-businesscapabilities.md`, `leanix-businesscontexts.md`, `leanix-objectives.md` (e outros tipos identificados) — **nunca** um tipo sem evidência.
- [ ] Em cada arquivo por tipo: **ficha campo a campo por item** (não só tabela consolidada) + diagrama por item (Interface: provedor→consumidor + Data Object; Data Object: Interface + apps CRUD; Provider: componentes/interfaces que fornece; Capability/Context/Objective: vínculo com a Application).
- [ ] **Contagem conferida:** em cada arquivo por tipo, **nº de fichas detalhadas = nº de itens do inventário** de abertura. Nenhum item resumido/ocultado (steering `fichas-leanix-completas`).

### D.2 — Cobertura dos tipos do meta-modelo (modo DESCOBERTA)

Confirme que **cada um dos 15 tipos do meta-modelo foi considerado** na varredura — ou seja, que para
todo tipo houve uma decisão consciente: **gerou ficha** (tem evidência), **declarou lacuna** (tipo
esperado sem sinal no fonte) ou **marcou "não se aplica"** (tipo que o fonte não produz). Um tipo que
simplesmente **não foi avaliado** é uma falha de cobertura — não um `N/A`.

- [ ] **Passei pelos 15 tipos** (lista abaixo), decidindo para cada um: ficha gerada · lacuna · N/A justificado. Nenhum tipo ficou sem avaliação.
- [ ] Os **4 tipos base** foram **sempre** materializados como arquivo (ficha ou "nenhum — lacuna"): `Application`, `ITComponent`, `Interface`, `DataObject`.
- [ ] Todo tipo com **evidência no fonte** virou arquivo `leanix-<tipo>.md`; nenhum tipo foi gerado **sem evidência** (não inventei tipo).
- [ ] Registrei no consolidado (`fichas-leanix.md`, no resumo) a **matriz de cobertura por tipo**, para o leitor ver o que foi coberto, o que ficou em lacuna e o que não se aplica.

| # | Tipo | Decisão: ficha / lacuna / N/A | Evidência ou justificativa |
|---|---|---|---|
| 01 | `Application` (Aplicação) — **base** | | |
| 02 | `ITComponent` (Componente de TI) — **base** | | |
| 03 | `Interface` (Interface) — **base** | | |
| 04 | `DataObject` (Objeto de Dados) — **base** | | |
| 05 | `ApplicationServices` (Serviço de Aplicação) | | |
| 06 | `BusinessCapability` (Capacidade de Negócio) | | |
| 07 | `BusinessContext` (Contexto de Negócio) | | |
| 08 | `Organization` (Organização) | | |
| 09 | `Provider` (Provedor) | | |
| 10 | `Contract` (Contrato) | | |
| 11 | `Platform` (Plataforma) | | |
| 12 | `TechnicalStack` (Tech Category) | | |
| 13 | `System` (Sistema) | | |
| 14 | `Objective` (Objetivo) | | |
| 15 | `Initiative` (Iniciativa) | | |

> **Fonte dos 15 tipos:** `references/templates-por-tipo/_INDICE.md` (extração do tenant em `AAAA-MM-DD`).
> Se a contagem de tipos mudar numa revalidação (Bloco A), **atualize esta tabela** junto com o `_INDICE.md`.
> No **modo FICHA** esta verificação é `N/A` (trabalha-se um tipo por vez) — confirme apenas que o tipo
> escolhido é um dos 15 do meta-modelo.

---

## Bloco E — Enriquecimento OBRIGATÓRIO (SKILL §2C) · TODA ficha, nos dois modos

- [ ] **§2C.1 — bloco `## 0. Descrição rica (OBRIGATÓRIA)`** em toda ficha: 2–5 frases sintetizadas do fonte (não cópia do `metadata.description`), com origem. Texto genérico não conta. Em `Application` e `ITComponent`, `description` é **obrigatória para aprovar** (sem ela = bloqueio de workflow).
- [ ] **§2C.1 — descrição por natureza do tipo:** `### Responsabilidade de negócio` (tipos de negócio), `### Descrição técnica` (tipos de tecnologia) **ou ambos** (tipos mistos).
- [ ] **§2C.1 — bloco `## Diagrama (Mermaid — cores oficiais por tipo)`** em toda ficha, com a cor oficial de cada tipo e legenda quando houver 3+ tipos.
- [ ] **§2C.2 — Subscriptions/responsáveis:** procurei o dono (CODEOWNERS, `catalog-info.yaml`, AUTHORS/MAINTAINERS, manifestos). Pessoa → `Responsible`; só grupo → equipe + pessoa em lacuna. Não inventei nome/e-mail. Sinalizei bloqueio de aprovação se faltar pessoa em Application.
- [ ] **§2C.3 — BusinessCapability:** inferi a capacidade; procurei via MCP (reúso por ID) **ou**, sem achar/sem MCP, **sugeri cadastro novo** marcado "a validar/cadastrar".
- [ ] **§2C.4 — Organization/time:** extraí o time do fonte; procurei via MCP (subtipo `team`/`businessUnit`) **ou** sugeri cadastro novo; é a âncora do Responsible.
- [ ] **§2C.5 — Governança de IA:** havendo **qualquer sinal de IA** no fonte, incluí a subseção "Governança de IA" com os campos do tipo (Application/ITComponent-aiModel/Interface-mcpServer), valor com origem **ou** lacuna. `lxAiRisk`/`lxAiVerified`/`lxTechnologyAssessment` marcados como **decisão do COE de IA / autoridade de arquitetura/governança** (nunca inferidos).
- [ ] **§2C.6 — Autenticação:** em Application (`aiAgent`) e Interface (`api`/`logicalInterface`/`mcpServer`), mapeei `lxAuthentication` pela evidência; mecanismo sem enum → `other` + descrição em `lxOtherAuthentication`; sem evidência = "a validar".

### E.1 — Formato de entrega do conjunto (SKILL §2C.7, regra dura do modo DESCOBERTA)

- [ ] **(1)** Ficha **campo a campo por item**, para **TODOS os itens** de todo tipo (inclusive os de volume), com campos obrigatórios e opções marcáveis `[x]`; tabela consolidada só como **inventário de abertura**, seguida das fichas de todos os itens. **PROIBIDO** "demais itens seguem o mesmo padrão"/"…"/"fichas representativas" — nº de fichas = nº de itens do inventário (steering `fichas-leanix-completas`).
- [ ] **(2)** Tabela de **evidência numerada `E1…En`** no `fichas-leanix.md` (`# | Sinal | Valor | Origem (arquivo:trecho)`); os arquivos por tipo referenciam cada código `E<n>` (ex.: `E12`) na coluna Origem, em vez de repetir o `arquivo:trecho`.
- [ ] **(3)** Bloco `## Divergências registradas` (rótulo declarado × realidade do fonte, com o que prevalece) **ou** declaração explícita "nenhuma divergência identificada".
- [ ] **(4)** Coluna `Reúso por ID (tenant)` com estado padronizado: `ID real` · `reúso a confirmar via MCP` · `não existe → sugerir cadastro`. Nunca ID inventado.
- [ ] **(5)** Campos de custo/TCO exibidos como `(RESTRITO — não exibir)` — listados, **não** removidos.

---

## Bloco F — Ontologia e regras inegociáveis (SKILL §4)

- [ ] `Provider → ITComponent → Application` respeitado (nunca Provider ligado direto na Application).
- [ ] Nenhum **ITComponent órfão**: cada um liga a Provider + Tech Category + Application consumidora.
- [ ] Toda **Interface** tem as **duas pontas** (provedora e consumidora) + o **Data Object** que trafega.
- [ ] "O quê" (BusinessCapability) × "como/onde" (BusinessContext) × "quem" (Organization) separados.
- [ ] Subtipos corretos (aiAgent/microservice/extension → Application; mcpServer/api/logical/event/etl → Interface; aiModel/llm → ITComponent); `lxMicroserviceType` (backend/ui/data) preenchido quando houver microsserviço.
- [ ] Tecnologia **com versão** (`release`) sempre que o fonte declarar.
- [ ] TCO/custo tratado como **restrito** (não exibido) em toda a entrega.

## Bloco G — Entrega e conformidade do projeto

- [ ] Saída salva na **pasta do alvo** sob `documentos-gerados/<alvo>/` (steering `entrega-e-saida`), não solta na raiz; links entre artefatos relativos.
- [ ] Arquivos gerados em **UTF-8 sem BOM**, sem mojibake (steering `encoding-utf8`).
- [ ] **Nada escrito no catálogo LeanIX** — a entrega é ficha(s) pronta(s) para cadastro **manual** (somente leitura).
- [ ] Resposta em **PT-BR**, citando a fonte (Fact Sheet por ID+nome quando via MCP; `arquivo:trecho` no modo descoberta).
- [ ] Pedido complexo encerrado com **Resumo Executivo + Próximo Passo Acionável**.

---

## Veredito final

> Só declare a tarefa concluída se **todos os itens aplicáveis dos blocos acima estiverem `[x]`**.
> Qualquer `[ ]` pendente = corrija antes de entregar. Qualquer `N/A` = justifique.
> Encontrou valor inventado (sem origem) em qualquer campo = **reprovado**: volte e substitua por lacuna.
> No modo DESCOBERTA, a **matriz de cobertura dos 15 tipos (Bloco D.2)** deve estar inteira — nenhum
> tipo sem decisão (ficha / lacuna / N/A justificado).
