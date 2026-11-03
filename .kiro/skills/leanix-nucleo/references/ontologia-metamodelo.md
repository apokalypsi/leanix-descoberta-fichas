---
id: ref-ontologia-metamodelo
nome: Ontologia do meta-modelo LeanIX (v4 + customizados do tenant)
tipo: referencia_skill
versao: '1.0'
fonte: 'Espelha a steering metamodelo-leanix (v4 SAP LeanIX + customizações do tenant configurado).'
pertence_a: skill leanix-nucleo
---

# Meta-modelo LeanIX (v4 + customizado do tenant)

> **DIRECIONAL — a organização adotou o meta-modelo do SAP LeanIX** como padrão de modelagem e fonte de
> verdade para classificar/relacionar qualquer elemento de arquitetura. **TOGAF/ArchiMate/APQC** entram
> só como **apoio conceitual** (vocabulário, camadas, ABB/SBB como lente), sempre **traduzidos para os
> Fact Sheets/relações do LeanIX**. **Em conflito de modelagem, prevalece o LeanIX**; para o estado
> real, o tenant via MCP. Nunca proponha estrutura "TOGAF-pura" que não caiba nos tipos do LeanIX.

## Princípios de modelagem
- **Padrão primeiro:** prefira tipos/subtipos/relações padrão a customização.
- **Pense de trás para a frente:** defina a decisão/relatório que o Fact Sheet suporta antes de criá-lo.
- **Relações explícitas** melhoram filtros/matrizes; hierarquias rasas (2–3 níveis).
- **Ontologia inegociável:** `Provider → IT Component → Application` (nunca ligue Provider direto na App).
- **Interface** precisa das **duas pontas** (provedora + consumidora) + o **Data Object** que trafega.
- **IT Component** nasce ligado a **Provider + Tech Category + Application** que o consome (Tríade de Ouro).

## Tipos v4 (padrão)
Objective · Platform · Initiative · Business Capability · Business Context · Organization ·
**Application** (subtipos: Business Application, AI Agent, Microservice, Extension) · **Interface**
(API, Logical, MCP Server) · Data Object · **IT Component** (Hardware/IaaS/PaaS/SaaS/Service/Software/
AI Model) · Tech Category · Provider · System.

## Tipos customizados do tenant (não existem no v4 padrão)
| Tipo (técnico) | Rótulo | Papel |
|---|---|---|
| `ApplicationServices` | Application Service | Comportamento exposto da app ("verbo infinitivo + substantivo alvo") |
| `TechnicalStack` | Tech Category | = Tech Category do v4, renomeado no tenant |

## Como responder
- **O que é / que Fact Sheet usar** → esta ontologia; confirme campos via `get_workspace_context`.
- **Instância concreta** → `filter_inventory`/`get_fact_sheet_details`; cite ID + nome; campo vazio = lacuna.
- Nunca invente tipos, subtipos, relações, etapas ou valores de SINGLE_SELECT: confirme via MCP.
