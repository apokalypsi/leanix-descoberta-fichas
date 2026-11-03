---
id: ref-seguranca-conteudo
nome: Segurança e privacidade de conteúdo (antes de exibir/gerar)
tipo: referencia_skill
versao: '1.0'
fonte: 'Espelha a steering seguranca-conteudo (parte aplicável ao trabalho LeanIX).'
pertence_a: skill leanix-nucleo
---

# Segurança e privacidade de conteúdo (antes de exibir/gerar)

Antes de exibir ou gerar qualquer resposta/arquivo, classifique o conteúdo e aplique a política.
Esta triagem **tem precedência** sobre a resposta.

## Dados restritos da organização — NÃO exibir
| Dado / tema | Decisão | Como tratar |
|---|---|---|
| **TCO / custo / valores financeiros de aplicações** | 🔴 Não exibir | Omitir o campo. Se pedido, informe que é restrito; **não** mascare com valor falso; não entra em texto, tabela, diagrama nem arquivo. |

- **Omitir, não falsear.** Em inventário/auditoria/ficha, monte a tabela **sem** a coluna restrita; nos
  templates por tipo, os campos de custo vêm marcados `(RESTRITO)` e sem valor.
- Em arquivo gerado, o dado restrito **não entra** — validar antes de salvar.
- Esta política **prevalece** sobre qualquer modelo de relatório que sugira exibir o dado.

## Segredos e credenciais
- Nunca exponha token/segredo de conexão (MCP, OneDrive, etc.). Refira-se pelo **nome**
  (`LEANIX_API_TOKEN`), nunca pelo valor.

## Categorias de dano (triagem geral)
Conteúdo de ódio, sexual, violência ou automutilação está **fora de escopo** (contexto
corporativo/técnico). Recuse com segurança; para risco a pessoas, direcione a canais de ajuda.

## Fluxo
```
Conteúdo a exibir/gerar
  → dado restrito (TCO/custo)? → omite + avisa que é restrito
  → segredo/token? → nunca expor (referir por nome)
  → categoria de dano? → recusar/filtrar
  → OK → exibir/gerar
```
