---
name: leanix-auditar-metamodelo
description: Audita COMPLETUDE E ASSOCIAÇÕES OBRIGATÓRIAS no SAP LeanIX em dois escopos. MODO ECOSSISTEMA — varre o catálogo via MCP e mede não-conformidades em escala: cadastros sem Nome/Assinatura/Descrição, Provedores órfãos ou duplicados, IT Components sem provedor, Interfaces sem produtor/consumidor/objeto de dados, Aplicações sem IT Component. MODO CONFORMIDADE DE 1 APP — valida se uma aplicação/projeto está corretamente representada (app, interfaces fornecidas/consumidas, IT Components, owner/capability/lifecycle). Use para varrer o catálogo e medir completude/lacunas, ou para checar se um projeto/app específico está cadastrado certo. NÃO use para estratégia TIME/6R (→ leanix-auditar-portfolio). Consulta via MCP (somente leitura); não escreve no catálogo.
metadata:
  versao: "1.0"
  tipo: skill
  categoria: leanix-leitura
  autor: "Arquitetura Corporativa"
  origem: regras validadas contra o tenant configurado via MCP
  somente_leitura: true
  relacoes_confirmadas: relProviderToITComponent, relProviderToInitiative, relLxProviderToContract, relITComponentToProvider, relInterfaceToProviderApplication, relInterfaceToConsumerApplication, relInterfaceToDataObject, relApplicationToITComponent
  facetas_confirmadas: __missing__ (combinável em AND entre facetas); DataQuality (_noResponsible_/_noDescription_/_qualitySealBroken_)
---

# Auditoria de Qualidade de Dados — Meta-Model LeanIX

## Papel
Atue como **Arquiteto Corporativo Especialista em LeanIX e Auditor de Qualidade de Dados**. Analise e
valide a **integridade dos Fact Sheets** do LeanIX, garantindo que o **Meta-Model** seja respeitado e
que os dados sigam as regras de governança da organização.

## Dois escopos (escolha pelo pedido)
- **MODO ECOSSISTEMA** — varrer o catálogo **inteiro** (ou um tipo/domínio) e medir completude/lacunas em
  escala. É o corpo principal desta skill (Passo 0 → seções 0–5). Use quando o pedido for "auditar o
  catálogo", "quantos cadastros estão incompletos", "providers órfãos", etc.
- **MODO CONFORMIDADE DE 1 APP** — checar se **uma aplicação/projeto** está cadastrada corretamente
  (app + interfaces fornecidas/consumidas + IT Components + ancoragem). Escopo = 1 Fact Sheet. Ver a
  seção **"MODO CONFORMIDADE — 1 aplicação"** ao final. Use quando o pedido for "validar o projeto X no
  LeanIX", "a app Y está cadastrada certo?", "verificar interfaces/componentes de um sistema".

> As duas respondem a mesma pergunta — "o que está cadastrado respeita o meta-modelo?" — em escopos
> diferentes (todo o catálogo × uma app). Para **classificação/padronização** (tipo errado, nomes) use
> `leanix-classificar-metamodelo`; para **estratégia** (TIME/6R) use `leanix-auditar-portfolio`.

## Boas práticas de cadastro (fonte oficial SAP LeanIX) → regras auditáveis
As regras desta skill são ancoradas nas boas práticas oficiais da SAP LeanIX para um "bom Fact Sheet":
completude, conexão (relações), propriedade (ownership), frescor (freshness) e governança (quality
seal). O **catálogo completo das 12 regras auditáveis (R1–R12)** com a boa prática e o como-auditar via
MCP está em `references/boas-praticas-e-evolucao.md` — leia esse arquivo ao fundamentar um achado numa
regra específica. A **v1 implementa R1, R6, R8** e as regras específicas de Provider/IT
Component/Interface/Application detalhadas abaixo; as demais (R2–R5, R7, R9–R12) entram no roteiro de
evolução.

## Nota de versão (v1)
Esta é a **primeira versão (v1)** da skill de auditoria de qualidade de dados — a fundação inicial da
governança de dados no LeanIX. O escopo será **iterado no futuro** (v2+) para validações mais
complexas, novos relacionamentos e outros tipos de Fact Sheet.

## Escopo (v1)
Auditoria automatizada e detalhada focada em **Provider (Provedor)** e **IT Component (Componente de
TI)**. Fonte: SAP LeanIX via MCP (`leanix`), **somente leitura** — a skill **recomenda**; correção e
merge são manuais, pelo time. Requer **VPN** + MCP ativo (grounding em `leanix-nucleo` →
`references/grounding.md`). Dado restrito (TCO/custo) não entra no relatório (`leanix-nucleo` →
`references/seguranca-conteudo.md`).

> Dimensões do tenant (confirme sempre via MCP na execução): um catálogo maduro costuma ter **dezenas de
> milhares de Fact Sheets** distribuídos em ~15 tipos, com **centenas de Providers e IT Components**.
> Catálogo grande → use `first:0` + `totalCount` para contagem; só traga linhas (amostra/paginação) ao
> listar. Nunca despeje o catálogo inteiro.

---

## Passo 0 (OBRIGATÓRIO) — levantar TODO o meta-modelo do ecossistema via MCP

**Antes de qualquer regra, bata no MCP e levante o meta-modelo completo** — todos os tipos de Fact
Sheet cadastrados no workspace e sua dimensão. A auditoria parte do que **realmente existe** no tenant,
não de uma lista fixa. Dois movimentos:

**0.1 Tipos e campos do meta-modelo** — ferramenta `get_fact_sheet_types` (lista todos os tipos + os
rótulos/translations) e, quando precisar dos campos/relações de um tipo, `get_workspace_context`
(`include_meta_model: true`, `fact_sheet_types: [<tipo>]`). Isso dá o **retrato do meta-modelo**.

**0.2 Dimensão + qualidade por tipo numa só consulta** — `filter_inventory` com facetas. Esta query
traz **a contagem de cada tipo** (faceta `FactSheetTypes`) e os **indicadores de qualidade** nativos
(faceta `DataQuality`: `_noResponsible_`, `_noDescription_`, `_qualitySealBroken_`) e de responsável
(faceta `Subscriptions` → `__missing__`):
```graphql
query {
  allFactSheets(first:0) {
    totalCount
    filterOptions { facets { facetKey results { key name count } } }
  }
}
```
> Saída de referência (formato ilustrativo — os números reais são os do **seu** tenant): a faceta
> `FactSheetTypes` devolve a contagem por tipo, tipicamente dominada por Initiative e BusinessContext,
> seguidos de Interface, ITComponent, Objective, Application, Provider e os demais (Contract,
> Organization, BusinessCapability, DataObject, Platform,
> System). A faceta `DataQuality` revela quantos estão **sem responsável** e com **Quality Seal
> quebrado** — em catálogos grandes esses costumam ser a maioria. Leia os valores da própria consulta;
> não assuma números fixos.

**Use este Passo 0 para:** (a) montar a tabela "tipos cadastrados × contagem" no relatório; (b)
dimensionar a auditoria; (c) decidir o foco. Nesta **v1** as regras detalhadas rodam sobre **Provider**
e **IT Component**, mas o levantamento cobre **o ecossistema inteiro** — e a seção "Sugestões de
evolução (v2)" usa as contagens/qualidade dos demais tipos para recomendar as próximas regras.

> **Completude por faceta:** a faceta **`DataQuality`** (valores `_noResponsible_`, `_noDescription_`,
> `_qualitySealBroken_`) permite **contar** por tipo quem está sem responsável/descrição sem paginar —
> combine-a com `FactSheetTypes`. Para **listar** quais Fact Sheets (ID+nome) violam, aí sim pagine
> trazendo os campos `description` e `subscriptions` (seção 0 abaixo).

---

## 0. Regra transversal — completude mínima de cadastro (TODO Fact Sheet)

**Todo cadastro do meta-modelo DEVE conter, no mínimo: Nome, Assinatura e Descrição.** Vale para
qualquer tipo (Provider, IT Component e os demais); nesta v1 aplica-se a Provider e IT Component.

| Campo obrigatório | No LeanIX | Como verificar | Violação |
|---|---|---|---|
| **Nome** | `displayName` / `name` (BASE_FIELD) | sempre presente (obrigatório na criação) | nome vazio/placeholder ("Novo", "sem nome", só código) |
| **Assinatura** | **Subscription** (pessoa responsável) | `subscriptions { edges { node { user { email } roles { name } } } }` | `subscriptions.edges` vazio = **sem responsável** |
| **Descrição** | `description` | campo `description` do nó | `description: null`/vazia |

> **Importante (como consultar):**
> - **Para CONTAR** quem está sem responsável/descrição → use a faceta **`DataQuality`**
>   (`_noResponsible_`, `_noDescription_`, `_qualitySealBroken_`) combinada com `FactSheetTypes`.
>   É o jeito eficiente, sem paginar (ver Passo 0).
> - **Para LISTAR** quais Fact Sheets (ID+nome) violam → `description` e `subscriptions` são **campos
>   do nó** (não têm faceta `__missing__`): pagine trazendo-os e avalie quais vêm `null`/vazios.
> No tenant, a grande maioria dos Fact Sheets vem sem descrição e sem subscription — confirme a
> amplitude por `DataQuality` na execução.

**Contar** sem responsável / sem descrição por tipo (eficiente, via faceta `DataQuality`):
```graphql
query { allFactSheets(first:0, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["Provider"]},
  {facetKey:"DataQuality", operator:OR, keys:["_noResponsible_"]} ]})
{ totalCount } }
```
Troque `_noResponsible_` por `_noDescription_` (sem descrição) ou `_qualitySealBroken_` (selo quebrado),
e a key de `FactSheetTypes` por `ITComponent`/outros tipos.

**Listar** quais violam (ID+nome) — pagine `after` até cobrir o `totalCount`; conte/relacione os vazios:
```graphql
query($after:String){ allFactSheets(first:100, after:$after, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["Provider"]} ]})
{ totalCount pageInfo{ hasNextPage endCursor }
  edges{ node{
    id displayName
    description
    subscriptions { edges { node { user { email } roles { name } } } }
  } } } }
```
Para cada Fact Sheet, marque quais dos 3 obrigatórios faltam. Severidade: **Alto** (sem Assinatura —
governança sem dono) · **Médio** (sem Descrição). Natureza: `Conclusivo`.

## 1. Regras de auditoria — PROVIDER (Provedor)

### 1.1 Padronização de nomenclatura
O nome deve seguir o **padrão oficial do fornecedor**: capitalização correta e **sem sufixos
societários/ruído** ("LTDA", "S.A.", "Inc.", "Corp", "ME", "EIRELI"), sem marcadores de teste
("TESTE", "NÃO USAR", "(antigo)"), sem espaços duplos. Reporte os que fogem do padrão com o **nome
sugerido** já normalizado. Natureza: `Requer validação` (padronização é decisão do time).

Base para análise (traga nome + alias de todos, paginando):
```graphql
query($after:String){ allFactSheets(first:100, after:$after, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["Provider"]} ]})
{ totalCount pageInfo{ hasNextPage endCursor }
  edges{ node{ id displayName ... on Provider { alias } } } } }
```

### 1.2 Análise de duplicidade e coerência (similaridade fonética e de escrita)
Identifique **o mesmo fornecedor cadastrado mais de uma vez** ou em variações (ex.: "AWS", "Amazon Web
Services", "Amazon"; "Microsoft", "Microsoft Corp", "MSFT"). Método sobre o conjunto trazido em 1.1:
- **Normalize** cada nome: minúsculas, remover pontuação e sufixos societários, colapsar espaços.
- **Similaridade de escrita:** compare por distância de edição (Levenshtein) / radical comum /
  substring — nomes próximos entram no mesmo grupo candidato.
- **Similaridade fonética:** agrupe nomes que soam igual (ex.: Soundex/Metaphone, ou heurística de
  fonemas PT/EN) para pegar grafias diferentes do mesmo som ("Oracle"/"Oracol", "Totvs"/"Totus").
- **Alias:** considere o campo `alias` — marca × razão social do mesmo fornecedor devem ser 1 Fact Sheet.
Para cada grupo suspeito, **sugira o nome "Master"** (o mais canônico/oficial) para um possível merge,
listando os IDs das variações. Natureza: `Requer validação` (merge é decisão do time).

### 1.3 Provedores órfãos (sem nenhuma associação)
Provedor **sem nenhum relacionamento ativo** com outros Fact Sheets do inventário — não fornece IT
Component, não está em Initiative nem em Contrato. Cadastro morto ou associação faltante. Severidade
`Crítico` para revisão/exclusão. Conte com as facetas combinadas (AND):
```graphql
query { allFactSheets(first:0, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["Provider"]},
  {facetKey:"relProviderToITComponent", operator:OR, keys:["__missing__"]},
  {facetKey:"relProviderToInitiative", operator:OR, keys:["__missing__"]},
  {facetKey:"relLxProviderToContract", operator:OR, keys:["__missing__"]} ]})
{ totalCount } }
```
Para listar: `first:50` + `edges{ node{ id displayName } }`. (No tenant, a definição ampla de órfão
reduz o número vs. olhar só IT Component — ~97 órfãos reais contra ~260 "sem IT Component".)

> Se o workspace tiver outras relações de Provider relevantes, inclua a faceta correspondente no AND
> (confirme via `get_workspace_context` type Provider). Relações de saída conhecidas:
> `relProviderToITComponent`, `relProviderToInitiative`, `relLxProviderToContract`,
> `relContratoProviderToBusinessContext`.

---

## 2. Regras de auditoria — IT COMPONENT (Componente de TI)

### 2.1 Obrigatoriedade de fornecedor
**TODO** IT Component deve ter um relacionamento ativo com um Fact Sheet `Provider`
(`relITComponentToProvider` — "provided by"). Liste **todos** que violam (sem provedor). Severidade
`Crítico` (quebra a ontologia Provider → IT Component → Application e impede rastrear origem/obsolescência).
```graphql
query { allFactSheets(first:0, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["ITComponent"]},
  {facetKey:"relITComponentToProvider", operator:OR, keys:["__missing__"]} ]})
{ totalCount } }
```
Listar faltantes: `first:50` + `edges{ node{ id displayName } }`. Para cada um, recomende associar ao
provedor correto — **reusando um Provider existente** (cruze com 1.2 para não criar duplicata).

---

## 3. Regras de auditoria — INTERFACE (produtor, consumidor e objeto de dados)
Uma Interface só é útil quando mostra **as duas pontas do fluxo e o dado que trafega**. Toda Interface
deve ter: **Produtor** (quem disponibiliza), **Consumidor** (quem consome) e **Objeto de dados** (o que
trafega). (Boa prática SAP: Fact Sheet isolado tem pouco valor — regra R6 Conexão.)

### 3.1 Interface sem produtor — `Crítico`
Sem provider a interface é órfã na origem (`relInterfaceToProviderApplication`).
```graphql
query { allFactSheets(first:0, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["Interface"]},
  {facetKey:"relInterfaceToProviderApplication", operator:OR, keys:["__missing__"]} ]})
{ totalCount } }
```

### 3.2 Interface sem consumidor — `Alto`
Sem consumer, ninguém usa o que a interface expõe (`relInterfaceToConsumerApplication`). Mesma query,
trocando a faceta para `relInterfaceToConsumerApplication`.

### 3.3 Interface sem objeto de dados — `Médio`
A interface não declara o **dado que trafega** (`relInterfaceToDataObject`). Mesma query, faceta
`relInterfaceToDataObject`.

> **Relatório da Interface:** para cada interface, mostre as três dimensões — **Produtor → [Interface]
> → Consumidor** e o(s) **Objeto(s) de dados**. Liste com:
> ```graphql
> query($after:String){ allFactSheets(first:100, after:$after, filter:{ facetFilters:[
>   {facetKey:"FactSheetTypes", operator:OR, keys:["Interface"]} ]})
> { totalCount pageInfo{ hasNextPage endCursor } edges{ node{ id displayName
>   ... on Interface {
>     relInterfaceToProviderApplication { edges { node { factSheet { id displayName } } } }
>     relInterfaceToConsumerApplication { edges { node { factSheet { id displayName } } } }
>     relInterfaceToDataObject { edges { node { factSheet { id displayName } } } }
>   } } } } }
> ```
> Órfã nas duas pontas (sem provider E sem consumer) = `Crítico`. Natureza: `Conclusivo`.

---

## 4. Regras de auditoria — APLICAÇÃO (infra/recursos via IT Component)
**Cada aplicação DEVE ter ao menos um IT Component associado** (`relApplicationToITComponent`) — é o que
identifica a **infraestrutura e os recursos** que a app usa (hospedagem, runtime, banco, serviços).
App sem IT Component não permite rastrear infra, custo de obsolescência nem impacto técnico. Respeita a
ontologia **Provider → IT Component → Application**.

### 4.1 Aplicação sem IT Component — `Alto`
```graphql
query { allFactSheets(first:0, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["Application"]},
  {facetKey:"relApplicationToITComponent", operator:OR, keys:["__missing__"]} ]})
{ totalCount } }
```
Listar faltantes: `first:50` + `edges{ node{ id displayName } }`. Recomende associar os IT Components
que compõem a infra/recursos da app (reusando IT Components existentes). Cruza com a regra 2.1 (IT
Component sem provedor) para fechar a cadeia Provider → IT Component → Application.

---

## 5. Formato de saída — "Relatório de Auditoria de Qualidade LeanIX (v1)"

```
# Relatório de Auditoria de Qualidade LeanIX (v1)
Data: <dd/mm/aaaa> · Fonte: SAP LeanIX via MCP (somente leitura) · Escopo v1: Provider · IT Component · Interface · Application

## 0. Meta-modelo do ecossistema (levantado via MCP — Passo 0)
Total de Fact Sheets: <N> · Tipos cadastrados: <T>
| Tipo de Fact Sheet | Qtde | Sem responsável | Sem descrição | Selo quebrado |
|---|---|---|---|---|
| Application | … | … | … | … |
| Provider | … | … | … | … |
| ITComponent | … | … | … | … |
| … (todos os tipos retornados) | … | … | … | … |

## 1. Resumo executivo
- Providers analisados: <n> · IT Components analisados: <n>
- Não-conformidades (v1): <total>
  - Completude mínima (sem Nome/Assinatura/Descrição): <n>
  - Provider fora do padrão: <n> · Suspeitas de duplicidade: <g> grupos · Órfãos: <n>
  - IT Component sem provedor: <n>
  - Interface sem produtor: <n> · sem consumidor: <n> · sem objeto de dados: <n>
  - Aplicação sem IT Component (infra/recursos): <n>
- Saúde geral do meta-modelo: <Boa | Com ressalvas | Crítica>

## 2. Completude mínima (Nome · Assinatura · Descrição)
| Fact Sheet (ID) | Tipo | Nome | Assinatura | Descrição | Faltando |
|---|---|---|---|---|---|
| … | Provider/ITComponent | ✅ | ❌ | ❌ | Assinatura, Descrição |

## 2b. Provedores fora do padrão / suspeita de duplicidade
| Nome atual (ID) | Problema | Nome "Master" sugerido | IDs do grupo (merge) |
|---|---|---|---|
| "Amazon Web Services LTDA" (…) | sufixo + duplicata | Amazon Web Services | AWS(…), Amazon(…) |

## 3. Provedores órfãos (revisão/exclusão)
| Provider (ID) | Lifecycle | Observação |
|---|---|---|
| … | … | sem IT Component/Initiative/Contrato |

## 4. IT Components sem provedor (enriquecimento de dados)
| IT Component (ID) | Lifecycle | Provedor sugerido (se inferível) |
|---|---|---|
| … | … | … |

## 5. Interfaces incompletas (produtor · consumidor · objeto de dados)
| Interface (ID) | Produtor | Consumidor | Objeto de dados | Faltando |
|---|---|---|---|---|
| … | App X | — | — | Consumidor, Objeto de dados |

## 6. Aplicações sem IT Component (infra/recursos não identificados)
| Application (ID) | Lifecycle | Observação |
|---|---|---|
| … | … | sem infra/recursos modelados |

## 7. Sugestões de evolução (v2)
<recomendações da IA para a próxima versão — ver seção 4 desta skill>
```
Encerre com **Próximo Passo Acionável** (o que corrigir primeiro: tipicamente associar IT Components
órfãos a providers existentes → consolidar providers duplicados → revisar/arquivar providers órfãos).

---

## 4. Sugestões de evolução (sementes para a v2)
Ao final do relatório, proponha regras para a próxima versão, priorizando as **regras de boas práticas
SAP LeanIX ainda não implementadas** (R2–R5, R7, R9–R12) e as regras específicas por tipo
(Provider / IT Component / Ecossistema). A lista completa de sementes da v2 está em
`references/boas-praticas-e-evolucao.md` (seção "Sugestões de evolução") — leia esse arquivo ao montar
a seção 7 do relatório. Confirme faceta/relação via `get_workspace_context` antes de implementar cada
nova regra.

## Regras de execução
- **Somente leitura:** o relatório recomenda; correção/merge é manual, pelo time de Arquitetura.
- **Duplicidade e nomenclatura = `Requer validação`:** apresente candidatos com o "Master" sugerido; o
  time confirma antes de qualquer merge. Órfão e "sem provedor" são `Conclusivo` (regra objetiva).
- **Escala:** `first:0` para contagem; amostra/paginação só ao listar. Cite sempre ID + nome.
- Sem dado = lacuna; nunca invente. Sem MCP/VPN = declare a limitação, não audite por memória.
- Nomes de relações/facetas podem variar por tenant e versão; confirme-os na execução e, se um campo
  falhar, reconfirme via `get_workspace_context` (Provider / ITComponent).

---

# MODO CONFORMIDADE — 1 aplicação (completude de uma app específica)

Checa se **uma aplicação/projeto** está **corretamente representada** no catálogo (escopo = 1 Fact
Sheet, em vez do ecossistema inteiro). Mesmo rigor de evidência; entrega um **laudo de conformidade da app**.

## Checklist de conformidade
1. **Aplicação cadastrada** — existe um Fact Sheet `Application` com o nome do projeto?
2. **Interfaces FORNECIDAS** — a app provê interfaces e elas estão cadastradas? (`relProviderApplicationToInterface`).
3. **Interfaces CONSUMIDAS** — a app consome interfaces e elas estão cadastradas? (`relConsumerApplicationToInterface`).
4. **IT Components associados** — a app tem IT Components ligados? (`relApplicationToITComponent`), respeitando `Provider → IT Component → Application`.
5. (Complementar) **Owner** (`relApplicationToOrganization`), **Business Capability**
   (`relApplicationToBusinessCapability`), **lifecycle** e **Data Objects** das interfaces
   (`relInterfaceToDataObject`) — reportar como lacuna de governança quando vazios.

## Procedimento
1. **Identificar a aplicação.** Confirme o nome; busque com `search_fact_sheet_by_name` (type
   `Application`) ou `filter_inventory` + `fullTextSearch`. Não achou → achado **crítico** "app não
   cadastrada" (ou **nome divergente** — liste candidatos próximos antes de concluir).
2. **Carregar o grafo da app** (consulta abaixo): interfaces fornecidas, consumidas e IT Components, com ID + nome.
3. **Avaliar o checklist**: natureza `Conclusivo`/`Lacuna`/`Requer validação`; severidade `Crítico`
   (app ausente; interface real sem cadastro) · `Alto` (IT Component ausente; interface sem ponta) ·
   `Médio` (owner/capability/lifecycle vazios) · `Baixo`.
4. **Checar as duas pontas das interfaces** (provider + consumer) + Data Object — interface órfã = lacuna.
5. **IT Component correto** — existe e tem Provider; app sem nenhum IT Component = lacuna.
6. **Emitir o laudo** — toda afirmação cita o Fact Sheet (ID + nome); sem dado = lacuna; recomendações
   são handoff ao time (LeanIX é leitura).

```graphql
query($nome:String!){
  allFactSheets(first:5, filter:{ fullTextSearch:$nome,
    facetFilters:[{facetKey:"FactSheetTypes", operator:OR, keys:["Application"]}] }){
    edges{ node{ id displayName type
      ... on Application {
        lifecycle { asString }
        relApplicationToITComponent { edges { node { factSheet { id displayName type } } } }
        relProviderApplicationToInterface { edges { node { factSheet { id displayName } } } }
        relConsumerApplicationToInterface { edges { node { factSheet { id displayName } } } }
        relApplicationToOrganization { edges { node { factSheet { id displayName } } } }
        relApplicationToBusinessCapability { edges { node { factSheet { id displayName } } } }
      } } }
  }
}
```
`variables`: `{ "nome": "<nome da aplicação/projeto>" }`. Para auditar as pontas de uma Interface
específica, use `factSheet(id:$id)` com `relInterfaceToProviderApplication`/`...ToConsumerApplication`/
`relInterfaceToDataObject`.

## Laudo de conformidade (saída)
```
Laudo de conformidade LeanIX — <Aplicação> (<ID ou "NÃO CADASTRADA">)
| Item | Status | Evidência (ID + nome) | Severidade | Recomendação |
|---|---|---|---|---|
| Aplicação cadastrada | ✅/🔴 | ... | ... | ... |
| Interfaces fornecidas / consumidas | ✅/🟡/🔴 | <n>: ... | ... | ... |
| IT Components associados | ✅/🟡/🔴 | <n>: ... | ... | ... |
| Interfaces com as 2 pontas | ✅/🟡 | órfãs: ... | ... | ... |
| Owner / Capability / lifecycle | ✅/🟡 | ... | ... | ... |
Conformidade geral: <Conforme | Conforme com ressalvas | Não conforme>
```
Encerre com **Resumo Executivo** + **Próximo Passo** (o que cadastrar/ajustar, com IDs para reuso).

---

> Boas práticas (R1–R12) e roteiro v2: `references/boas-praticas-e-evolucao.md` (desta skill).
> Núcleo compartilhado (cores, grounding, ontologia, segurança): skill `leanix-nucleo`
> (`references/ontologia-metamodelo.md`, `references/grounding.md`).
> Auditoria de portfólio (saúde funcional, complementar): skill `leanix-auditar-portfolio`.

