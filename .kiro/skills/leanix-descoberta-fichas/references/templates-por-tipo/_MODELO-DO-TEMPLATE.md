---
id: modelo-do-template
nome: Modelo do template (o "template do template") — estrutura canônica de um TPL-NN-<Tipo>.md
fonte: 'Padrão estrutural dos TPL-NN gerados a partir do dataModel do tenant via MCP LeanIX'
pertence_a: skill leanix-descoberta-fichas
versao_modelo: '1.1'
---

# Modelo do template (o "template do template")

Este arquivo define a **estrutura canônica** que todo `TPL-NN-<Tipo>.md` deve seguir. Use-o quando a
pasta `templates-por-tipo/` estiver **vazia** (ou faltar o template de um tipo) e for preciso **gerar os
templates a partir do meta-modelo real via MCP** (ver SKILL.md §0 → "Bootstrap: pasta vazia").

**Regra de ouro:** a extração via MCP preenche o **conteúdo** (campos, valores, relações do tipo); o
**formato** é exatamente o deste modelo — mesmos cabeçalhos, mesmas colunas, mesma ordem, mesmo padrão de
checkbox. Máximo de detalhe: **TODOS** os campos, **TODAS** as opções de seleção, **TODAS** as relações
com seus atributos. Nunca resuma com "demais seguem o padrão"; liste tudo.

---

## De onde vêm os dados (MCP LeanIX — somente leitura)

Para cada tipo de Fact Sheet:
1. `get_fact_sheet_types` — lista os tipos do workspace + rótulos/translations (define quais TPL gerar e a numeração do `_INDICE.md`).
2. `get_workspace_context` com `include_meta_model: true`, `include_schema: true`, `fact_sheet_types: ["<Tipo>"]` — devolve, para o tipo:
   - **campos** (nome técnico, tipo de dado, obrigatoriedade, condicional/activation, valores de seleção);
   - **translations pt** → a coluna **Rótulo (PT)**;
   - **relações** (nome, tipo destino, multiplicidade, atributos da relação);
   - **subtipos** (`category`), **hierarquia** (níveis), **ACL**, **workflow** (obrigatórios p/ aprovar).
3. Tags: `allTagGroups` (GraphQL) alimenta `references/tags-grupos-leanix.md` (lote inteiro, não por tipo).

Cite a origem. Sem MCP/VPN → **não** gere por memória: declare a limitação.

---

## Estrutura canônica de um `TPL-NN-<Tipo>.md`

### A. Front matter (YAML) — obrigatório, nesta ordem

```yaml
---
id: tpl-ficha-<tipoMinusculo>
tipo_fact_sheet: <Tipo técnico>
rotulo: "<Rótulo PT>"
fonte: 'dataModel + translations pt do tenant configurado (extracao AAAA-MM-DD) - TODOS os campos, com rotulo PT e agrupamento tematico'
pertence_a: skill leanix-descoberta-fichas
metamodelo_extraido_em: 'AAAA-MM-DD'   # a DATA REAL da extração via MCP (não deixar o placeholder)
revalidar_a_cada_dias: 30
versao_template: '1.0'
---
```

### B. Título + cartão de regras do tipo

```markdown
# Template de ficha - `<Tipo>` (<Rótulo PT>)

> **Hierarquia:** <até N níveis> - **ACL:** <GLOBAL/...> - **Workflow:** <aprovação MANUAL/automática; obrigatorios p/ aprovar: subs=[...] base=[...]>
> **BASE_FIELDS (todo tipo):** `name` (Nome, obrigatorio), `displayName` (gerado), `description` (Descricao), `tags` (Tags), `subscriptions` (Assinaturas: pessoa+papel), `resources`/`documents` (Recursos: links, arquivos, diagramas).
> **Como preencher:** marque a opcao escolhida com [x] (UMA em SINGLE, VARIAS em MULTIPLE); coluna Valor p/ texto/numero; sem evidencia = lacuna. **Campos (RESTRITO)** (custo/TCO) nao exibir valor.
```

### C. Bloco `## 0. Descrição rica (OBRIGATÓRIA)`

Sempre presente (regra dura, SKILL.md §2C.1). **TODO `TPL-NN`, de QUALQUER tipo, traz os DOIS
subblocos — negócio e técnica** — nesta ordem. O que muda por natureza do tipo é apenas **qual deles é
obrigatório** e qual é **"quando houver"**; nenhum tipo fica com um só. Esta regra vale também na
**geração dos templates via MCP** (Passo 0 / Bootstrap): ao criar cada `TPL-NN-<Tipo>.md`, inclua
sempre os dois subblocos abaixo — nunca gere um template só com negócio ou só com técnica.

1. **`### Descrição de negócio`** — subcampos **Função de negócio** (o que o item viabiliza no
   processo/jornada — "o quê", em linguagem de negócio), **Valor para o negócio** (por que importa; o
   que para/quebra se ele cair), **Origem**.
   - **Obrigatório** nos **tipos de negócio** (`Application`, `BusinessCapability`, `BusinessContext`,
     `Organization`, `Objective`, `Initiative`, `Contract`, `Provider`, `Platform`) — rotule o
     subbloco `### Responsabilidade de negócio (OBRIGATÓRIO para tipo de negócio)` e acrescente
     **Responsabilidade no processo**.
   - **Recomendado / "quando houver"** nos **tipos técnicos** (`ITComponent`, `Interface`, `DataObject`,
     `ApplicationServices`, `TechnicalStack`, `System`, `GAP`, `TechnologyAuthority`,
     `principiosarquiteturais`): preencha sempre que o item tiver significado de negócio (ex.: a
     interface que fecha a venda, o banco que guarda a inscrição). Se for puramente técnico/infra,
     **não omita o subbloco** — marque **"não aplicável — item técnico/infra"** (com o valor atribuído à
     Application que o usa). Nunca inventar valor de negócio; sem evidência = lacuna.
2. **`### Descrição técnica`** — o que é / o que faz / como integra (provedor → consumidor, dados
   trafegados, protocolo quando aplicável), com **Origem**.
   - **Obrigatório** nos **tipos técnicos** (lista acima).
   - **Recomendado / "quando houver"** nos **tipos de negócio**: inclua quando o item tiver
     materialização técnica relevante (ex.: `Application`, `Platform`, `Contract`). Puramente de
     negócio (ex.: `BusinessCapability`, `Objective`) → marque **"não aplicável — item de negócio"**.

> **Regra dura:** os dois subblocos existem em TODA ficha e em TODO `TPL-NN` gerado. O que varia é o
> rótulo de obrigatoriedade (OBRIGATÓRIO vs "quando houver — recomendada") e, no caso que não se aplica,
> o preenchimento **"não aplicável"** — jamais a ausência do subbloco. Tipos **mistos**
> (`ApplicationServices`, `Contract`) têm os dois como relevantes.

Texto-guia em blockquote (`>`) explicando que são 2-5 frases sintetizadas do fonte, com origem
`arquivo:trecho`, nunca copiar `metadata.description`, nunca inventar; e que a técnica é obrigatória para
tipo técnico e a de negócio para tipo de negócio, a outra entrando "quando houver" (ou "não aplicável").

### D. Bloco `## Campos (TODOS os campos do tipo, agrupados por tema)`

Subseções numeradas **por tema**, SEMPRE as 9 nesta ordem fixa:

1. Identificação
2. Ciclo de vida
3. Criticidade / fit / maturidade
4. IA e governança de IA
5. Autenticação / SSO
6. Hospedagem / camada
7. Racionalização (TIME/6R) / obsolescência / prontidão
8. Outros campos
9. Custos (RESTRITO - nao exibir)

> **Tema sem campos → NÃO omita: declare "Não se aplica a este tipo".** Para a estrutura ficar
> **idêntica entre todos os tipos** (e o diff entre tenants ser legível), mantenha o subtítulo numerado
> do tema mesmo quando o tipo não tem nenhum campo dele, com a frase `Não se aplica a este tipo.` no
> lugar da tabela (uma linha explicativa quando ajudar, ex.: "o fit é avaliado como atributo da relação
> X"). Assim o leitor sabe que o tema foi considerado, não esquecido.

> **Subtítulos `####` dentro de um tema (blocos nomeados do meta-modelo).** Quando o meta-modelo
> agrupa campos sob um bloco próprio (ex.: "Detalhes SAP" em `System`, "Renovação do contrato" /
> "Governança e Regulamentações" em `Contract`, os vários "Parecer de …" em `TechnologyAuthority`,
> "Detalhes do servidor MCP" em `Interface`), preserve esse agrupamento como um subtítulo `####` dentro
> do tema correspondente (em geral "8. Outros campos"), cada um com sua própria tabela de 8 colunas. Não
> achate tudo numa tabela só: espelhe os blocos do tenant.

> **Campo que é atributo de RELAÇÃO (não do Fact Sheet).** Alguns valores vêm como atributo de uma
> relação, não como campo do tipo (ex.: TCO em `relApplicationToBusinessCapability`, `supportType`/
> `resourceClassification` em `relITComponentToTechnologyStack`). Esses **não** entram nas tabelas de
> campos; entram no bloco **E. Relações** (coluna "Atributos da relacao"). No tema afetado (ex.: Custos),
> deixe uma nota curta apontando que o valor aparece como atributo de relação, para não parecer omissão.

Cada subseção **com campos** é **uma tabela** com EXATAMENTE estas 8 colunas, nesta ordem:

```markdown
| Campo | Rotulo (PT) | Tipo | Obrig. | Condicional | Opcoes (marque) | Valor | Origem |
|---|---|---|---|---|---|---|---|
| `nomeTecnico` | Rótulo PT | STRING/SINGLE_SELECT/MULTIPLE_SELECT/INTEGER/DOUBLE/LIFECYCLE/... | **SIM** (se obrigatório) | <ativação, ex.: category=aiAgent; campoX=valor> | [ ] opcao1 - [ ] opcao2 - ... | | |
```

Regras de preenchimento das colunas:
- **Campo:** nome técnico em crase. Sufixe ` (RESTRITO)` em campos de custo/TCO.
- **Obrig.:** `**SIM**` só quando o meta-modelo marca obrigatório; senão vazio.
- **Condicional:** a condição de ativação do campo (subtipos/valores que o fazem aparecer); vazio se sempre visível.
- **Opcoes (marque):** para SINGLE/MULTIPLE_SELECT, TODAS as opções como `[ ] valor` separadas por ` - `. LIFECYCLE lista as fases. Demais tipos: vazio.
- **Valor / Origem:** sempre vazios no template (preenchidos ao montar a ficha).

### E. Bloco `## Relacoes (TODAS)`

Uma tabela com TODAS as relações do tipo:

```markdown
| Relacao | Destino | Mult. | Atributos da relacao | Alvo (ID/nome ou a criar) | Reuso/Novo |
|---|---|---|---|---|---|
| `relXToY` | <TipoDestino> | *:* (ou 1:*, *:1, 1:1) | - (ou "attrs: campo:TIPO, ...") | | |
```

- Liste os atributos da relação como `attrs: nome:TIPO, nome:TIPO` quando houver; `-` quando não houver.
- As colunas **Alvo** e **Reuso/Novo** ficam vazias no template.

### F. Bloco `## Assinaturas (pessoas)`

```markdown
| Papel | Pessoa | Obrig.? | Origem |
|---|---|---|---|
| Responsible |  | <workflow/recomendado/opcional conforme o tipo> |  |
| Accountable |  | recomendado |  |
| Observer |  | opcional |  |
```

### G. Bloco `## Diagrama (Mermaid - cores oficiais por tipo)`

Texto-guia em blockquote + um diagrama `flowchart LR` de ontologia com o nó-alvo e um vizinho
placeholder. A cor do nó-alvo é a **cor oficial do tipo** (`references/cores-fact-sheet.md`); siga a
sintaxe Mermaid segura (um `classDef` por linha; `<br/>`; `&lt; &gt;` para `< >`). Estrutura:

- blockquote de instrução (ontologia, cor oficial, texto preto, legenda quando 3+ tipos);
- um bloco ` ```mermaid ` com `flowchart LR`, o nó `ALVO["&lt;nome&gt;<br/>[<Tipo>]"]:::alvo`, um nó
  `VIZ["&lt;vizinho&gt;<br/>[tipo vizinho]"]:::viz`, a aresta `VIZ --> ALVO`, e os `classDef`:
  `classDef alvo fill:<HEX-DO-TIPO>,stroke:#333,color:#000;` e
  `classDef viz fill:#EDEDED,stroke:#333,color:#000;` (um `classDef` por linha).

> Para o HEX de cada tipo (ex.: `#387BDE` para Application), consulte `references/cores-fact-sheet.md`,
> que também traz a tabela completa de cores e um exemplo Mermaid pronto.

---

## Depois de gerar os templates (fechamento obrigatório)

1. **Gere um TPL por tipo** retornado por `get_fact_sheet_types`, nomeando `TPL-NN-<Tipo>.md` na ordem do índice.
1b. **Confira a conformidade estrutural de CADA `TPL-NN` gerado** (garante que a regeneração via MCP
   volta **idêntica** em estrutura — a estrutura não regride). Em cada template:
   - **Os 7 blocos A–G existem** (front matter · título/cartão · `## 0. Descrição rica` · `## Campos` ·
     `## Relacoes` · `## Assinaturas` · `## Diagrama`).
   - **Os DOIS subblocos do bloco C** estão presentes no `## 0`: `### Descrição de negócio` **e**
     `### Descrição técnica` (um OBRIGATÓRIO, o outro "quando houver" / "não aplicável") — nunca só um.
   - **Os 9 temas do bloco D aparecem na ordem fixa**, cada um com tabela OU a frase "Não se aplica a
     este tipo." (nenhum tema omitido).
   - **Completude do conteúdo (regra de ouro):** TODOS os campos do tipo, TODAS as opções `[ ]` dos
     SINGLE/MULTIPLE_SELECT e TODAS as relações com atributos vieram do MCP para o template — nada
     resumido com "demais seguem o padrão". Campos de custo/TCO marcados `(RESTRITO)`.
   Um TPL que falhe em qualquer item acima está **fora do modelo** e deve ser corrigido antes de seguir.
2. **Regere o `_INDICE.md`:** tabela `# · Tipo técnico · Rótulo · link`, com a contagem real de tipos, o front matter (mesma `metamodelo_extraido_em` real) e as notas de autenticação/subtipo observadas.
3. **Atualize `tags-grupos-leanix.md`** a partir de `allTagGroups` e, se aplicável, `estrutura-subtipos-relacoes.md`.
4. **Preencha a DATA REAL** em `metamodelo_extraido_em` e no texto `fonte:` de todos os arquivos gerados (nunca deixar `AAAA-MM-DD`).
5. **UTF-8 sem BOM** em todos os arquivos (regra do projeto); confira acentos e ausência de mojibake.
6. Cadastro no LeanIX continua **manual** — a skill só gera os templates/fichas (somente leitura).
