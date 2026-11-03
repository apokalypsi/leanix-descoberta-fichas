---
inclusion: always
---

# Comunicação e saída de entregáveis

Padrão de como o agente responde e onde salva o que gera.

## Comunicação

- **Idioma: PT-BR** sempre.
- Tom **profissional, consultivo, pragmático e objetivo**. Combate ativo ao over-engineering (KISS, YAGNI).
- Respostas a pedidos **complexos** encerram com **Resumo Executivo + Próximo Passo Acionável**.
- **Cite a fonte** ao recomendar: Fact Sheet (ID + nome) para dado do LeanIX via MCP, `arquivo:trecho`
  no modo descoberta, ou a referência da skill para o resto.

## Entregáveis gerados

- Artefatos que o usuário vai guardar/revisar (as fichas LeanIX e seus diagramas) são salvos em
  **`documentos-gerados/`** na raiz do workspace.
- Formato padrão: **Markdown** (com Mermaid para diagramas). Gere em **UTF-8 sem BOM**
  (ver steering `encoding-utf8`). Se o usuário pedir outro formato, entregue nele também.
- Diagramas de tipos do LeanIX usam as cores oficiais (`leanix-nucleo` →
  `references/cores-fact-sheet.md`; reforço na steering `cores-metamodelo-leanix`).

## Organização por alvo (REGRA)

- **Tudo que for de um alvo (repositório/sistema) vai na pasta daquele alvo**, nunca solto na raiz.
  Um alvo = **uma subpasta** com o nome dele: `documentos-gerados/<alvo>/`. Exemplo: as fichas do
  `agente-vendas` ficam em `documentos-gerados/agente-vendas/` (consolidado `fichas-leanix.md` +
  os arquivos por tipo `leanix-<tipo>.md`; se o repo gerar 2+ apps, uma subpasta por app).
- **A raiz `documentos-gerados/` guarda só o genérico/transversal** — o que não pertence a um alvo
  específico (ex.: um índice geral). Na dúvida, se o artefato cita um alvo, ele vai na pasta do alvo.
- Links entre os artefatos de um mesmo alvo são **relativos** (não quebram ao mover a pasta inteira).
- Ao gerar um novo artefato de um alvo existente, **coloque-o na pasta desse alvo** e ajuste os
  links relativos — não recrie uma cópia na raiz.
