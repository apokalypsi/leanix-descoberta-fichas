---
name: leanix-nucleo
description: Núcleo compartilhado das skills leanix-* — os dados e regras PADRÃO comuns a todo trabalho de SAP LeanIX no projeto, num só lugar e autossuficiente (sem depender de steering externa). Fornece as CORES oficiais por tipo de Fact Sheet (para diagramas Mermaid/SVG), as regras de GROUNDING (rigor de evidência: só dado real via MCP, nunca inventar, somente leitura), a ONTOLOGIA do meta-modelo (tipos v4 + customizados do tenant, subtipos, Provider→IT Component→Application), e a política de SEGURANÇA DE CONTEÚDO (TCO/custo restrito, segredos por nome). As demais skills leanix-* referenciam este núcleo em vez de steerings soltas, para poderem ser importadas juntas em outro projeto. Use como base de consulta ao trabalhar com LeanIX; é biblioteca de apoio, não executa ação própria.
metadata:
  versao: "1.0"
  tipo: skill
  categoria: leanix-nucleo
  autor: "Arquitetura Corporativa"
  papel: "biblioteca de dados/regras padrão compartilhados pelas skills leanix-*"
  origem: "consolida num núcleo portável as regras de cores, grounding/validação, ontologia e segurança de conteúdo do LeanIX (reforçadas pelas steerings cores-metamodelo-leanix, validacao-leanix e seguranca-segredos)"
  somente_leitura: true
  portatil: true
---

# Núcleo compartilhado leanix

Biblioteca de **dados e regras padrão** do domínio SAP LeanIX, reunidos aqui para que as skills
`leanix-*` sejam **autossuficientes e importáveis em grupo** para outro projeto — sem depender de
steerings soltas em `.kiro/steering/`. Esta skill **não executa ação**; ela é **referenciada** pelas
demais skills LeanIX do projeto (todas as `leanix-*`: descoberta, classificação de tipo, TBM e as duas
auditorias).

> **Como usar (nas outras skills):** onde antes se lia *"ver steering `cores-metamodelo-leanix`"* /
> *"grounding/validação"* / *"ontologia do meta-modelo"* / *"segurança de conteúdo"*, leia *"ver
> `leanix-nucleo` → references/…"*. Se as steerings existirem no projeto (`cores-metamodelo-leanix`,
> `validacao-leanix`, `seguranca-segredos`), reforçam; se não (projeto importado), o núcleo basta.

## O que este núcleo fornece (references)

| Reference | O que traz | Usado por |
|---|---|---|
| `references/cores-fact-sheet.md` | **Cores oficiais** por tipo de Fact Sheet (Mermaid/SVG) + regras de diagrama | toda skill que desenha grafo/diagrama |
| `references/grounding.md` | **Rigor de evidência**: só dado real (MCP), nunca inventar, conclusivo × lacuna, somente leitura | toda skill que consulta/afirma algo do catálogo |
| `references/ontologia-metamodelo.md` | **Tipos v4 + customizados do tenant**, subtipos, relações, `Provider → IT Component → Application` | classificar, cadastrar, auditar, rastrear |
| `references/seguranca-conteudo.md` | **TCO/custo restrito** (não exibir), segredos por nome, triagem de dano | toda skill que gera saída/arquivo |

## Regras-resumo (as 4 em uma frase cada)

1. **Cores:** nó que representa tipo de Fact Sheet usa a **cor oficial do tipo** (texto preto, setas
   `#9AA1AB`); nunca a paleta institucional da organização nos nós de tipo. Tabela em `references/cores-fact-sheet.md`.
2. **Grounding:** todo dado do ecossistema real vem do **LeanIX via MCP** (somente leitura); cite o
   Fact Sheet (ID + nome); sem evidência = **lacuna** (nunca invente). Detalhe em `references/grounding.md`.
3. **Ontologia:** modele no meta-modelo LeanIX (não TOGAF); `Provider → IT Component → Application`;
   Interface com as duas pontas + Data Object. Detalhe em `references/ontologia-metamodelo.md`.
4. **Segurança:** **TCO/custo/valores financeiros não entram** em texto, tabela, diagrama nem arquivo;
   segredo/token referido por **nome**, nunca pelo valor. Detalhe em `references/seguranca-conteudo.md`.

## Portabilidade

Para levar as skills `leanix-*` a outro projeto, copie **todas juntas** (incluindo esta
`leanix-nucleo`). O conjunto fica autossuficiente: o núcleo substitui as steerings do projeto de
origem. A fonte de verdade do **dado real** continua sendo o tenant via MCP (configure o servidor
`leanix` no destino); a fonte dos **conceitos/cores/ontologia** passa a ser este núcleo.

> Observação: este núcleo é a **fonte única (canônica)** das regras de cores, grounding, ontologia e
> segurança do LeanIX. As steerings do projeto (`cores-metamodelo-leanix`, `validacao-leanix`) são
> **stubs curtos que apontam para cá** — ao mudar o LeanIX ou a política, atualize **só as references
> desta skill**; as steerings não repetem o conteúdo.
