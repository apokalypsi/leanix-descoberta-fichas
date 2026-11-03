---
inclusion: auto
name: validacao-leanix
description: Procedimento obrigatório de validação ao consultar, afirmar, documentar ou alterar qualquer informação do SAP LeanIX (Fact Sheet, aplicação, IT Component, provedor, relacionamento, meta-modelo, cores, campos, regras de catálogo). Use sempre que o trabalho tocar o LeanIX — cruzar doc oficial SAP + dado real via MCP e citar as fontes.
---

# Regra: validação de componentes da sua organização no LeanIX

Antes de **validar, afirmar, documentar ou alterar** qualquer informação sobre um
componente da sua organização no LeanIX (Fact Sheet, aplicação, IT Component, provedor,
relacionamento, ciclo de vida, meta-modelo, cores por tipo, campos obrigatórios,
regras de catálogo, etc.), o Kiro **DEVE consultar as duas fontes abaixo** e cruzar
os resultados. Nunca valide por memória ou suposição.

## Fontes obrigatórias (nesta ordem)

1. **Documentação oficial do SAP LeanIX** — fonte canônica de conceitos,
   meta-modelo, APIs e comportamento da plataforma.
   - Portal: https://help.sap.com/docs/leanix?locale=pt-BR
   - **Meta-modelo (referência obrigatória para tipos de Fact Sheet, campos e
     relacionamentos):** https://help.sap.com/docs/leanix/ea/meta-model?locale=pt-BR
   - Use as ferramentas de web (busca/fetch) para localizar a página específica do
     tópico em questão. Prefira sempre a documentação oficial a blogs ou terceiros.

   **Links diretos por tema (documentação oficial SAP LeanIX):**
   | Tema | URL |
   |---|---|
   | Meta-modelo (visão geral, pt-BR) | https://help.sap.com/docs/leanix/ea/meta-model?locale=pt-BR |
   | Configuração do meta-modelo | https://docs-eam.leanix.net/docs/configuration |
   | Fact Sheets (estrutura, tipos e subtipos) | https://docs-eam.leanix.net/docs/factsheets |
   | Inventário (repositório central) | https://docs-eam.leanix.net/docs/inventory |
   | Ciclo de vida / catálogo de IT Components (obsolescência) | https://docs-eam.leanix.net/docs/lifecycle-catalog |
   | APIs disponíveis (visão geral) | https://docs-eam.leanix.net/reference/available-apis |
   | GraphQL — tutoriais | https://docs-eam.leanix.net/docs/graphql-tutorials |
   | GraphQL — básico | https://docs-eam.leanix.net/docs/graphql-basics |
   | GraphQL — avançado (incl. lifecycle em mutations) | https://docs-eam.leanix.net/reference/graphql-advanced |
   | REST APIs | https://docs-eam.leanix.net/reference/rest-apis |
   | Comunidade LeanIX (dúvidas, boas práticas, casos de uso) | https://community.leanix.net/ |

   > A **Comunidade LeanIX** (https://community.leanix.net/) é fonte **complementar**:
   > útil para boas práticas, discussões e casos de uso, mas **não substitui** a
   > documentação oficial nem o dado real do MCP. Em caso de conflito, prevalecem a
   > doc oficial (conceito) e o MCP (dado do tenant).

2. **Nosso servidor MCP do LeanIX** (workspace configurado) — fonte do **dado real** do
   nosso tenant.
   - Servidor MCP: `leanix` (ver `.kiro/settings/mcp.json`).
   - Ferramentas típicas: `search_mcp_tools`, `get_mcp_tools_schema`,
     `list_fact_sheets`, `single_fact_sheet`, `filter_inventory`.
   - Fluxo: se houver Progressive Tool Discovery, comece por `search_mcp_tools` →
     `get_mcp_tools_schema` → chamada da ferramenta; senão, use diretamente as
     ferramentas do toolset `inventory`.

## Quando consultar (obrigatório, não responder de memória)

Sempre que a resposta depender de **dado real** do tenant: existência/atributos de uma aplicação,
responsáveis (owners), integrações consumidas/fornecidas, tecnologias (IT Components/Providers),
lifecycle, criticidade, fit, capacidades de negócio, princípios, ABBs.

## Procedimento de validação

1. **Entenda o conceito na doc oficial** (o que o LeanIX define para aquele
   tipo/campo/regra).
2. **Confirme o dado real via MCP** no tenant configurado (o componente existe? quais
   seus atributos, dono, ciclo de vida, relacionamentos?).
3. **Cruze os dois**: a definição oficial x o dado real da organização. Aponte
   divergências (ex.: campo obrigatório pela doc, mas vazio no nosso tenant).
4. **Cite as fontes** na resposta: link(s) da doc oficial consultada **e** a(s)
   ferramenta(s) MCP usada(s) com o ID do Fact Sheet correspondente.

## Regras

- **Nunca** afirme algo sobre um componente da organização sem ter consultado o MCP para o
  dado real daquele tenant.
- **Nunca** descreva comportamento/conceito do LeanIX sem respaldo na doc oficial;
  se a doc não puder ser consultada no momento, **declare isso explicitamente** em
  vez de apresentar suposição como fato.
- Em caso de conflito: para **conceito/plataforma**, prevalece a **doc oficial**;
  para **o estado atual do componente**, prevalece o **dado do MCP** (tenant real).
- Operações de **escrita** no LeanIX (criar/atualizar/excluir) exigem **confirmação
  explícita** do usuário, mesmo após a validação.
- **Nunca** exponha o valor do token de API nas respostas; refira-se a ele por nome.
- **Conclusivo × lacuna de dados:** sem o dado no catálogo, declare "lacuna de dados" — nunca
  invente ID, nome ou valor. **Sem MCP/VPN:** declare "não foi possível consultar o LeanIX" e trate
  como lacuna; não suponha.
- **Somente leitura:** quando a análise indicar mudança, **recomende** (o que criar/ajustar/relacionar,
  com ID existente para reuso); a execução é manual, pela equipe responsável.

## Controle de escopo (antes de varredura ampla)

1. Confirme o escopo com o usuário (portfólio / capability / domínio / owner / lista / 1 app).
2. Obtenha o **`totalCount`** primeiro; se exceder ~200 itens, **restrinja com filtros** antes de
   trazer linhas.
3. Traga só os campos necessários à decisão.

> Detalhe e material portável: skill `leanix-nucleo` → `references/grounding.md`.
