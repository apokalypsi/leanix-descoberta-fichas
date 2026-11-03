---
inclusion: always
---

# Somente leitura por padrão

Consulta e análise são **diagnóstico** — não alteram o que examinam. Esta é a postura
padrão do agente em todo o projeto; as skills apenas a reforçam.

## Regra

- **LeanIX (MCP `leanix`):** acesso **somente leitura**. O agente consulta e **entrega as fichas**;
  nunca cria, altera ou exclui Fact Sheet, relação ou automação no catálogo.
- **Repositório varrido:** a descoberta é **leitura** do fonte. Não altere arquivos do repositório
  analisado para "ajustar" durante a varredura.
- **Qualquer escrita exige confirmação explícita do usuário**, caso a caso — criar/alterar/excluir no
  LeanIX, ou modificar arquivos fora da pasta de saída `documentos-gerados/`.

## Como agir quando a análise aponta uma mudança

**Recomende, não execute.** Descreva o que criar/ajustar/relacionar, com justificativa e — no LeanIX —
o ID do Fact Sheet existente para reúso. Deixe a decisão e a execução com o usuário/time.

> Esta regra é transversal. O detalhe operacional está nas skills LeanIX: grounding em
> `leanix-nucleo` → `references/grounding.md` e na steering `validacao-leanix`.
