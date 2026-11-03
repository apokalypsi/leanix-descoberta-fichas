---
id: ref-grounding
nome: Grounding — consulta ao LeanIX com rigor de evidência
tipo: referencia_skill
versao: '1.0'
fonte: 'Fonte canônica do grounding LeanIX. A steering validacao-leanix resume e aponta para cá.'
pertence_a: skill leanix-nucleo
---

# Grounding — consulta ao LeanIX com rigor de evidência

O **SAP LeanIX é a fonte de verdade** do ecossistema real da organização. Acesso **somente leitura** via MCP
(`leanix`). O agente consulta e **recomenda**; nunca escreve no catálogo.

## Quando consultar (obrigatório, não responder de memória)
Sempre que a resposta depender de **dado real**: existência/atributos de uma aplicação, responsáveis
(owners), integrações consumidas/fornecidas, tecnologias (IT Components/Providers), lifecycle,
criticidade, fit, capacidades de negócio, princípios, ABBs.

## Procedimento de validação (doc oficial + MCP)
Antes de afirmar algo sobre um componente/conceito do LeanIX, cruze **duas fontes**:
1. **Conceito/meta-modelo** → documentação oficial SAP LeanIX
   (https://help.sap.com/docs/leanix/ea/meta-model?locale=pt-BR). Se não puder consultar agora,
   declare isso em vez de supor.
2. **Dado real do tenant configurado** → via MCP. Fluxo: `get_fact_sheet_types` / `get_workspace_context`
   → `filter_inventory` / `search_fact_sheet_by_name` → `get_fact_sheet_details`.
3. **Cruze e cite:** indique a doc oficial consultada **e** a ferramenta MCP usada com o ID do Fact Sheet.

## Controle de escopo (antes de varredura ampla)
1. Confirme o escopo com o usuário (portfólio / capability / domínio / owner / lista / 1 app).
2. Obtenha o **`totalCount`** primeiro; se exceder ~200 itens, **restrinja com filtros** antes de trazer linhas.
3. Traga só os campos necessários à decisão.

## Rigor de evidência (inegociável)
- **Cite o Fact Sheet** (ID + displayName) como fonte de cada afirmação.
- **Conclusivo × lacuna de dados:** sem o dado no catálogo, declare "lacuna de dados" — nunca invente
  ID, nome ou valor.
- **Sem MCP / sem VPN:** declare "não foi possível consultar o LeanIX" e trate como lacuna; não suponha.
- **Precedência:** conceito/plataforma → doc oficial; estado do componente → LeanIX via MCP (tenant real).

## Somente leitura
Quando a análise indicar mudança no catálogo, **recomende** (o que criar/ajustar/relacionar, com
justificativa e ID existente para reuso). A execução é **manual, pela equipe responsável**.
