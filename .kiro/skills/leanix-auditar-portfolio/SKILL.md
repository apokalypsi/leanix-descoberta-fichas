---
name: leanix-auditar-portfolio
description: ESTRATÉGIA DE PORTFÓLIO no SAP LeanIX — avalia o VALOR e o RISCO das aplicações para decidir o que manter, investir, migrar ou eliminar: obsolescência (lifecycle), redundância funcional, acoplamento/integrações, risco, aderência a princípios, classificação TIME e 6R, análise AS-IS→TO-BE e racionalização. Use quando o usuário pedir avaliação/racionalização de portfólio, TIME, 6R, obsolescência, redundância, mapa de integrações ou roadmap de modernização. NÃO use para completude/associações obrigatórias do catálogo (→ leanix-auditar-metamodelo). Consulta via MCP (somente leitura) e recomenda; não escreve.
metadata:
  versao: "1.0"
  tipo: skill
  categoria: leanix-leitura
  autor: "Arquitetura Corporativa"
  somente_leitura: true
---

# Auditoria de portfólio (LeanIX)

Método ponta a ponta de auditoria. **Grounding primeiro** (LeanIX via MCP, somente leitura); separa
achado conclusivo de lacuna de dados; termina em **recomendação** (handoff ao time, não escreve no
catálogo). Requer **VPN** + MCP `leanix` ativo.

## Passo a passo
1. **Escopo** — confirme o recorte (portfólio / domínio / capability / owner / lista / 1 app) e o
   objetivo (racionalizar, reduzir risco, preparar transição). Em varredura ampla, obtenha
   `totalCount` antes; se > ~200, restrinja com filtros.
2. **Coletar (LeanIX, leitura)** — lifecycle, Business Criticality, Functional/Technical Fit, hosting,
   6R, owners (Business/Technology), capacidades, interfaces (consumidas/fornecidas), Data Objects,
   IT Components/Providers, Quality Seal/completude. Use `get_workspace_context` + `filter_inventory`
   + `get_fact_sheet_details`.
3. **Analisar pelos eixos** (abaixo), percorrendo as relações do meta-modelo.
4. **Classificar e rotular** — TIME/6R por evidência; natureza (`Conclusivo`/`Lacuna de dados`/
   `Requer validação`) e severidade (`Crítico`/`Alto`/`Médio`/`Baixo`).
5. **Priorizar** — risco × impacto × esforço.
6. **Recomendar e entregar** — tabela de achados + recomendações acionáveis + diagramas; entregue em
   Markdown (se pedido, em outro formato) e salve em `documentos-gerados/`. Encerre com Resumo
   Executivo + Próximo Passo.

## Eixos de análise
| Eixo | O que investigar | Sinais de alerta |
|---|---|---|
| Obsolescência/lifecycle | fase do ciclo de vida | muitos `phaseOut`/`endOfLife`; IT Component obsoleto sob app crítica |
| Redundância/sobreposição | apps com mesma função | mesma Business Capability, Data Objects/interfaces equivalentes, nomes próximos |
| Acoplamento/integrações | interfaces consumidas/fornecidas | SPOF, integração ponto-a-ponto proliferada, dependência sem dono |
| Rastreabilidade/impacto | o que é impactado se X mudar/sair | app a desativar com consumidores ativos |
| Aderência a princípios | confronto com os princípios de arquitetura cadastrados | violações 🔴, gaps 🟡 sem mitigação |
| Qualidade do dado | Quality Seal, completude, owners | selo quebrado, sem owner, campos vazios |
| Risco | criticidade × fit × obsolescência | missionCritical com Technical Fit inappropriate e sem dono |

## Frameworks
- **TIME:** Tolerate · Invest · Migrate · Eliminate (por valor × qualidade técnica).
- **6R:** Rehost · Replatform · Rearchitect · Repurchase · Retain · Retire.
- Use a taxonomia **configurada no workspace**; não classifique sem evidência.

## Armadilhas
- Concluir sem evidência (inventar owner/integração) → marque lacuna.
- Exibir TCO/custo → **proibido** (ver `leanix-nucleo` → `references/seguranca-conteudo.md`).
- Recomendar "eliminar" sem checar quem **consome** as interfaces (quebra dependência).

> Cadeia canônica de rastreio: Provider → IT Component → Application → Interface → Application
> (+ Data Objects, owners) — ver `leanix-nucleo` → `references/ontologia-metamodelo.md`.
> Método de racionalização: TIME (Tolerate/Invest/Migrate/Eliminate) + 6R.
> **Rastreabilidade/impacto:** antes de recomendar eliminar/migrar, percorra o grafo de relações do
> Fact Sheet via MCP (`count_fact_sheet_relations`, `get_fact_sheet_details`) para entender o raio de
> impacto ("quem consome / o que quebra se X sair").
