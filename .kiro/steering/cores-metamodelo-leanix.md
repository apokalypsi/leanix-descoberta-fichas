---
inclusion: auto
name: cores-metamodelo-leanix
description: Cores oficiais dos tipos de Fact Sheet do SAP LeanIX para diagramas e ilustrações. Use ao criar qualquer diagrama (Mermaid, SVG, HTML) ou tabela com legenda que represente tipos do meta-modelo LeanIX.
---

# Cores oficiais do meta-modelo LeanIX em diagramas

**REGRA FIXA.** Todo diagrama/desenho (Mermaid, SVG, HTML, tabela com legenda) cujos **nós
representem tipos de Fact Sheet do LeanIX** DEVE pintar cada nó com a **cor oficial do seu tipo**.
Nunca use a paleta institucional da organização (roxo/amarelo) para os nós de tipo, nem invente cor.

- Texto do nó: **preto `#000000`**. Setas: cinza neutro `#9AA1AB`.
- Diagrama com 3+ tipos → inclua **legenda** cor → tipo.
- Nós que **não** são tipos de Fact Sheet (etapas/fases de processo) usam o tema do documento (paleta institucional da organização).

**Sintaxe Mermaid (evita parse error) — cada `classDef` e cada `class` em SUA PRÓPRIA LINHA.** Nunca
dois `classDef` na mesma linha nem após `; ` de outro comando (gera `Parse error ... UNICODE_TEXT`).
Prefira o sufixo `:::classe` no nó. Regras completas e exemplo em `leanix-nucleo` →
`references/cores-fact-sheet.md` (seção "Sintaxe Mermaid").

> **Fonte da verdade (tabela completa de cores por tipo + exemplo Mermaid):**
> `leanix-nucleo` → `references/cores-fact-sheet.md`. Mantenha a tabela num só lugar —
> não duplique aqui. Se o LeanIX mudar uma cor, atualize **somente** a reference da skill.
