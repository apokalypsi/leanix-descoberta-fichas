---
id: ref-tech-categories-leanix
nome: Tech Categories (TechnicalStack) — taxonomia TBM v5 canônica
tipo: referencia_skill
versao: '2.0'
fonte: 'Taxonomia TBM v5.0.1 (c) TBM Council — padrão (tbmStandard). Sem IDs de tenant e sem categorias customizadas: este template usa apenas a taxonomia TBM padrão.'
revisao: '2026-11'
status: ativo
pertence_a: skill leanix-descoberta-fichas
---

# Tech Categories (TechnicalStack) — taxonomia TBM v5 (padrão)

A **Tech Category** (tipo `TechnicalStack` do meta-modelo, rótulo "Categoria de Tecnologia") classifica
cada **IT Component**. Neste template, a referência de categorias é a **taxonomia TBM v5.0.1 padrão**
(`category: tbmStandard`) — **6 domínios (N1) → 32 grupos (N2) → 141 categorias-folha (N3) = 179 nós**.

> **Importante (template genérico):** este template **não embute categorias customizadas de tenant**
> (qualquer valor de `category` diferente de `tbmStandard`) nem IDs de Fact Sheet. Ele trabalha só com a
> **taxonomia TBM padrão**. Se o seu tenant tiver categorias próprias além do TBM, elas **não** estão
> aqui — confirme-as via MCP no seu catálogo (`FactSheetTypes=TechnicalStack`, `category`) e registre o
> ID real na hora de montar a ficha.

## Fonte única da árvore TBM

A árvore completa (os 179 nós com nome + definição oficial em inglês, por domínio) é mantida **num só
lugar** para não duplicar:

> **`leanix-classificar-categoria-tbm` → `references/tbm-taxonomy-v5.md`**

Ao montar um IT Component, abra essa referência e escolha a **categoria-folha (N3)** mais específica cujo
nome/definição descreva a tecnologia (N1/N2 são guarda-chuva; o IT Component é ligado à folha). Se nenhuma
folha servir, **não invente** — marque "Tech Category a definir/confirmar via MCP" e descreva a sugestão.

Os **6 domínios N1** da taxonomia TBM v5:

| Domínio (N1) | Abrange (resumo) |
|---|---|
| **Artificial Intelligence** | IA: agêntica, generativa, interpretativa, preditiva, prescritiva |
| **Business** | Soluções de negócio: atendimento, manufatura/entrega, produto, vendas & marketing |
| **Delivery** | Desenvolvimento, plataformas, operações, segurança & conformidade, estratégia, suporte |
| **Infrastructure** | Compute, dados, data center, rede, armazenamento |
| **Shared & Corporate** | Funções internas: comunicação corporativa, finanças, RH, jurídico etc. |
| **Workplace** | Soluções voltadas ao usuário: dispositivos cliente, software, conectividade e produtividade |

## Heurística de classificação (sinal do fonte → domínio/grupo TBM)

Ponto de partida; a escolha final confirma pelo **nome/definição da categoria-folha** em
`tbm-taxonomy-v5.md` (prefira o nível mais específico que descreva a tecnologia).

| Tecnologia levantada no fonte | Caminho TBM sugerido (confirmar a folha em tbm-taxonomy-v5.md) |
|---|---|
| Linguagem/stack de desenvolvimento, framework de app, IDE | Delivery / Development · Delivery / Enabling Platforms (Development Platform) |
| SDK/biblioteca de LLM, framework de agente | Artificial Intelligence (domínio) + Delivery / Development |
| LLM / modelo generativo de texto | Artificial Intelligence / Generative (Text Generation) |
| Agente autônomo / orquestração de tarefas | Artificial Intelligence / Agentic |
| NLP, OCR, visão computacional, fala | Artificial Intelligence / Interpretive |
| Banco relacional / NoSQL / data warehouse / cache | Infrastructure / Data (Database, Data Warehouse, Distributed Cache) |
| Vector DB / busca semântica / RAG | Infrastructure / Data **ou** Artificial Intelligence / Interpretive |
| Contêiner/orquestração, serverless, compute | Infrastructure / Compute (Virtual Compute & Containers, Compute on Demand) |
| CI/CD, deploy, gestão de serviço de TI | Delivery / Development · Delivery / Operations |
| Broker/streaming, API management, integração | Delivery / Enabling Platforms (Message Bus & Integration) |
| Observabilidade / monitoramento | Delivery / Operations (Event Management) |
| IAM, criptografia, SIEM, vulnerabilidades | Delivery / Security & Compliance |

> Nome sem correspondência clara na taxonomia padrão = "a definir pelo time / confirmar via MCP". Nunca
> force um encaixe nem invente categoria.

## Reconfirmar o catálogo real via MCP (opcional)

A taxonomia TBM padrão acima é a referência de classificação. Para saber quais Tech Categories **de fato
existem** no seu tenant (e seus IDs, incluindo eventuais customizadas), consulte via MCP — **somente
leitura**, não altera o catálogo:

```graphql
query($after:String){ allFactSheets(first:100, after:$after, filter:{ facetFilters:[
  {facetKey:"FactSheetTypes", operator:OR, keys:["TechnicalStack"]} ]})
{ totalCount pageInfo{ hasNextPage endCursor } edges{ node{ id level displayName } } } }
```

Relação IT Component → Tech Category: `relITComponentToTechnologyStack` (`supportType: leading/supports`).
