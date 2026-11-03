---
id: ref-template-ficha-cadastro
nome: Template — ficha de cadastro de Fact Sheet no LeanIX
tipo: referencia_skill
versao: '1.1'
status: ativo
pertence_a: skill leanix-descoberta-fichas
---

# Template — ficha de cadastro de `<Tipo>` [`<Subtipo>`]

> **Este arquivo é o FORMATO do entregável** (como organizar a ficha: seções A–H, regras de
> enriquecimento, descrição rica, ancoragem, resumo). **Os CAMPOS de cada tipo vêm dos
> `templates-por-tipo/TPL-NN-<Tipo>.md`** (a lista exaustiva, com valores de seleção em checkbox,
> gerada da API). Fluxo: pegue a estrutura daqui + os campos do template do tipo → preencha.
>
> Esqueleto a preencher pela skill. Uma ficha por Fact Sheet. Copie as tabelas, filtre os campos
> condicionais pelo subtipo, e marque cada campo como **Conclusivo** (tem valor), **Lacuna** (vazio,
> a preencher) ou **Validar** (inferência a confirmar). Não invente valor nem campo.

**Tipo:** `<Tipo>` · **Subtipo (`category`):** `<valor>` · **Camada:** `<camada v4>`
**Regra de nome:** `<como o displayName é formado>` · **Hierarquia:** até `<N>` níveis
**Workflow de aprovação:** `<obrigatórios para aprovar>`

> **Blocos de nível do CONSOLIDADO (`fichas-leanix.md`), não da ficha individual** — monte-os uma vez
> por sistema (SKILL.md §2C.7):
> - **`## 0. Evidência coletada` numerada `E1…En`** (`# | Sinal | Valor | Origem arquivo:trecho`); as
>   fichas por tipo citam o código `Ex` na coluna Origem.
> - **`## Divergências registradas`** (rótulo/versão/nome do catálogo × fonte; o que prevalece + a
>   recomendação). Sem divergência: "nenhuma divergência identificada".

## A. Identificação
| Campo | Tipo | Obrig.? | Valores permitidos | Valor | Status | Origem |
|---|---|---|---|---|---|---|
| `name` | BASE_FIELD | Sim | — | | | |
| `description` | BASE_FIELD | (workflow) | — | *(ver A1 — descrição rica)* | | |
| `category` (subtipo) | SINGLE_SELECT | <> | <lista> | | | |
| `alias` / `externalId` | STRING | | — | | | |

### A1. Descrição rica (obrigatória)
> 2–5 frases **sintetizadas do fonte** (não copiar o `metadata.description`): o que é / o que faz
> (propósito e escopo), como se encaixa no negócio e com quem integra; para ITComponent inclua a
> função + versão; para Interface o que trafega e as pontas; para Data Object o conceito de negócio.
> Cite a origem (arquivo:trecho). É **obrigatória para aprovar** Application e ITComponent.

`<escreva aqui a descrição rica — texto corrido, com as origens entre parênteses>`

## B. Campos próprios do tipo (só os do subtipo escolhido)
| Campo | Tipo | Obrig.? | Valores permitidos | Valor | Status | Origem |
|---|---|---|---|---|---|---|
| `<campo>` | `<TIPO>` | | `{...}` | | | |

> Campos **condicionais**: inclua apenas os ativados pelo subtipo/gatilho atual. **A lista completa dos
> campos do tipo (com valores de seleção) está em `templates-por-tipo/TPL-NN-<Tipo>.md`.** **Dado
> restrito (TCO/custo):** liste o campo com o valor fixo **`(RESTRITO — não exibir)`** (não remova a
> linha) — deixa explícito que o campo existe e foi omitido por política (SKILL.md §2C.7.5).

## C. Ciclo de vida
| Campo | Tipo | Valor | Status | Origem |
|---|---|---|---|---|
| `lifecycle` | LIFECYCLE | plan/phaseIn/active/phaseOut/endOfLife | | |

## C2. Tags (classificação transversal)
| Tag group | Modo (SINGLE/MULTIPLE) | Tag(s) aplicada(s) | Status | Origem |
|---|---|---|---|---|
| `<grupo>` | <> | `<tag>` | | |

> Só os grupos que valem para o tipo (ver `tags-grupos-leanix.md`). No modo descoberta, extraia do
> fonte (linguagem, tipo de banco, ABB de aplicação); as de negócio = "a definir pelo time".

## D. Relações mínimas (ontologia)
| Relação | Destino | Mult. | Alvo (nome) | Reúso por ID (tenant) | Atributos da relação | Status |
|---|---|---|---|---|---|---|
| `relXToY` | `<Tipo>` | `A:B` | | `ID real` / `reúso a confirmar via MCP` / `não existe → sugerir cadastro` | | reúso / novo / validar |

> Respeite a ontologia: Provider → ITComponent → Application; Interface com as duas pontas + Data
> Object; IT Component com Provider + Tech Category + Application. A coluna **Reúso por ID** usa os três
> estados padronizados (SKILL.md §2C.7.4); sem MCP nunca inventar ID — fica em "a confirmar" ou
> "sugerir cadastro". **Interface** detalha as três relações: provedora
> (`relInterfaceToProviderApplication`), consumidora (`relInterfaceToConsumerApplication`) e
> `relInterfaceToITComponent`, além do Data Object que trafega.

## E. Assinaturas / responsáveis (pessoas)
| Papel | Pessoa | Obrig.? | Status | Origem |
|---|---|---|---|---|
| Responsible | | (workflow) | | CODEOWNERS / `spec.owner` / author / maintainers |
| Accountable | | recomendado | | |
| Observer | | opcional | | |

> Procure o responsável no fonte (CODEOWNERS, `catalog-info.yaml:spec.owner`, `AUTHORS`/`MAINTAINERS`,
> `author` de manifestos). **Pessoa** → Responsible. Só **grupo/time** → o time vai para a seção G2
> (Organization/team) e o **nome da pessoa fica lacuna** ("indicar pessoa do time"). Nunca inventar
> nome/e-mail. `Responsible` sem pessoa = **bloqueio de aprovação** da Application.

## G. Ancoragem de negócio (reusar via MCP → senão sugerir cadastro)

### G1. Capacidade de negócio (`relApplicationToBusinessCapability`)
| Capacidade inferida (do fonte) | Existe no catálogo? | Ação | ID (se reúso) | Origem |
|---|---|---|---|---|
| `<nome de negócio — "o quê">` | sim / não / a validar via MCP | **reusar** / **sugerir cadastro** | | |

> 1) Infira a capacidade pelo propósito. 2) Procure via MCP (`FactSheetTypes=["BusinessCapability"]`):
> achou → **reusar** (ID + nome, não duplicar); não achou/sem MCP → **sugerir cadastro** de uma
> BusinessCapability nova (nome + justificativa + hierarquia) marcada "a validar/cadastrar".

### G2. Organização — equipe dona (`relApplicationToOrganization`, subtipo `team`)
| Time (do fonte) | Subtipo | Existe no catálogo? | Ação | ID (se reúso) | Origem |
|---|---|---|---|---|---|
| `<ex.: squad-exemplo>` | team (ou businessUnit) | sim / não / a validar via MCP | **reusar** / **sugerir cadastro** | | `catalog-info.yaml:spec.owner` |

> Mesmo fluxo: procure via MCP (`FactSheetTypes=["Organization"]`, subtipo `team`); achou → reusar;
> não achou/sem MCP → sugerir cadastro de Organization/team nova. Esta equipe é a âncora do Responsible (E).

## H. Governança de IA (SÓ se houver sinal de IA no fonte — então é obrigatória)
> Incluir esta seção sempre que o fonte tiver IA (LLM/modelo, agente, SDK de IA, prompts, `mcp.json`,
> ML). Preencha o que der para inferir com origem; o resto é lacuna (nível de risco/verificação =
> decisão do COE de IA / parecer da autoridade de arquitetura/governança — nunca inventar).

| Campo | Tipo (FS) | Valores | Valor | Status | Origem |
|---|---|---|---|---|---|
| `lxAiUsage` | Application | unknown/noAiUsage/aiAvailable/aiEnabled | | | |
| `lxAiType` | Application | neuralNet/llm/machineLearning/Other | | | |
| `lxAiPotential` | Application | low/medium/high/veryHigh | | | |
| `lxAiRisk` | Application | minimal/limited/high/unacceptable | | lacuna (COE de IA) | |
| `lxAiAgentType` | Application/aiAgent | autonomous/conversational | | | |
| `lxAiAgentNature` | Application/aiAgent | standard/custom/standardExtended/background | | | |
| `lxAiVerified` | App/aiAgent · ITComp/aiModel · Interface/mcpServer | verified/unverified/rejected | | | |
| `lxAiTechnologyTaxonomy` | ITComponent/aiModel | llm/modelOptimization/modelGovernanceSecurity/otherAi | | | |
| `lxAvailableTools` / `lxMcpServerType` / `lxMcpServerClassification` | Interface/mcpServer | — / remote,local / read,write | | | |

> Vínculos a destacar: **AI Model** (ITComponent/aiModel) que o sistema usa → Provider; e o parecer do
> **COE de IA** da autoridade de arquitetura/governança, quando aplicável.

## F. Resumo
- Campos preenchidos: **N** · em lacuna: **M** · a validar: **K**.
- **Descrição rica:** ok / pendente. **Responsável:** pessoa / só grupo (pessoa em lacuna).
- **Capacidade de negócio:** reusada (ID) / a cadastrar. **Organização/equipe:** reusada (ID) / a cadastrar.
- **Governança de IA:** há IA? sim/não — se sim, campos de IA exibidos (valor/lacuna).
- Relações a criar: **…** · reúso de Fact Sheets existentes: **…** (se MCP usado).
- **Próximo passo:** `<o que o time cadastra primeiro>`.
- Observação: o agente **recomenda/estrutura**; o cadastro é **manual** (LeanIX é leitura).
