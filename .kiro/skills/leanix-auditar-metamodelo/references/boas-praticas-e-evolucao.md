# Boas práticas SAP LeanIX (R1–R12) e roteiro de evolução (v2)

Material de apoio da skill `leanix-auditar-metamodelo`. Leia este arquivo quando precisar do
catálogo completo de regras auditáveis (ancoradas nas boas práticas oficiais da SAP LeanIX) ou ao
montar a seção "Sugestões de evolução (v2)" do relatório. O corpo da skill (`SKILL.md`) implementa na
**v1** as regras R1, R6, R8 e as regras específicas de Provider/IT Component/Interface/Application.

## Índice
- [1. Boas práticas (fonte oficial SAP LeanIX) → regras auditáveis](#1-boas-práticas-fonte-oficial-sap-leanix--regras-auditáveis)
- [2. Sugestões de evolução (sementes para a v2)](#2-sugestões-de-evolução-sementes-para-a-v2)

---

## 1. Boas práticas (fonte oficial SAP LeanIX) → regras auditáveis

As regras desta skill são ancoradas nas boas práticas oficiais da SAP LeanIX para um "bom Fact Sheet":
completude, conexão (relações), propriedade (ownership), frescor (freshness) e governança (quality
seal). Catálogo de regras padronizadas e **como auditar via MCP** (verificado no tenant):

| Regra | Boa prática (SAP LeanIX) | Como auditar (MCP) |
|---|---|---|
| **R1 Responsável** | todo FS precisa de subscriber Responsible/Accountable; sem ele o Quality Seal não aprova | faceta `DataQuality=_noResponsible_` (contar) / campo `subscriptions` (listar) |
| **R2 Quality Seal** | selo sinaliza dado completo e aprovado; quebra com mudança por não-responsável ou por inatividade | faceta `lxState=BROKEN_QUALITY_SEAL` |
| **R3 Atributos obrigatórios** | FS fica em Draft e não aprova o selo se faltam atributos/relação/papel obrigatórios | faceta `lxState=DRAFT` |
| **R4 Completude** | score por pesos; clientes maduros ficam em 75–85% (qualidade > quantidade) | campo `completion.percentage` por FS |
| **R5 Frescor** | selo quebra após intervalo de renovação (30d/3m/6m/1a) sem atualização | campo `updatedAt` vs. intervalo |
| **R6 Conexão** | FS isolado tem pouco valor; App deve ligar a Business Capability, Organization e IT Component | faceta de relação com `__missing__` |
| **R7 Lifecycle** | estado preenchido (Plan/Phase In/Active/Phase Out/End of Life) | campo `lifecycle` |
| **R8 Nome/Descrição** | nome claro e objetivo; descrição focada no negócio | `displayName`/`description` + faceta `DataQuality=_noDescription_` |
| **R9 Hierarquia rasa** | capability 2–3 níveis, 5–10 itens por nível | faceta `hierarchyLevel` + relToParent/Child |
| **R10 Tags com parcimônia** | tag não conta p/ completude e não substitui atributo/relação; evitar excesso | faceta `_TAGS_` |
| **R11 Meta-modelo enxuto** | preferir configuração padrão; custom só quando necessário | `get_fact_sheet_types`/`get_workspace_context` |
| **R12 Campos de julgamento** | Business Criticality, Functional/Technical Fit de forma consistente (survey), não ad-hoc | ler os campos (consistência = heurística) |

> **Fonte (parafraseado p/ conformidade):** "What Makes a Good Fact Sheet" (SAP LeanIX Community);
> docs oficiais Quality Seal, Mandatory Attributes, Fact Sheet Completeness, Creating Fact Sheets,
> General Modeling Guidelines (docs-eam.leanix.net / help.sap.com/docs/leanix). Em conflito conceito ×
> dado: conceito prevalece a doc oficial; estado do catálogo prevalece o MCP.
> **v1 implementa:** R1, R6, R8 e as regras específicas de Provider/IT Component abaixo.
> **R2, R3, R4, R5, R7, R9–R12** entram no roteiro (seção "Sugestões de evolução").

---

## 2. Sugestões de evolução (sementes para a v2)

Ao final do relatório, proponha regras para a próxima versão. Priorize as **regras de boas práticas
SAP LeanIX ainda não implementadas** (R2–R5, R7, R9–R12 da tabela acima), que valem para TODO tipo de
Fact Sheet:
- **R2 Quality Seal quebrado** — reportar % por tipo (`lxState=BROKEN_QUALITY_SEAL`); no tenant há
  milhares de selos quebrados.
- **R3 Fact Sheets em Draft** — faltam atributos/relação/papel obrigatórios (`lxState=DRAFT`).
- **R4 Completude abaixo do alvo** — `completion.percentage` < 75% por tipo (alvo de maturidade 75–85%).
- **R5 Frescor** — FS não atualizados além do intervalo de renovação (`updatedAt`), risco de dado obsoleto.
- **R7 Lifecycle vazio** — FS sem estado de ciclo de vida.
- **R9 Hierarquia** — capability com profundidade > 3 níveis ou nós com filhos demais.
- **R10 Tags** — uso excessivo/ruído; tag no lugar de atributo/relação.

Regras específicas por tipo (também v2):
- **Provider:** criticidade/qualidade preenchidas (`providerCriticality`/`providerQuality`); provider
  sem contrato mas com IT Component ativo; `externalId` ausente; `endOfLife` com IT Components ativos.
- **IT Component:** sem Tech Category (`relITComponentToTechnologyStack`); sem aplicação consumidora
  (`relITComponentToApplication`); lifecycle vazio; hierarquia pai/filho inconsistente.
- **Ecossistema (R6 Conexão):** Application sem Business Capability/owner; Interface órfã (sem provider
  ou consumer); cobertura de owners; completude média por tipo.

> Confirmar faceta/relação via `get_workspace_context` antes de implementar cada nova regra.
