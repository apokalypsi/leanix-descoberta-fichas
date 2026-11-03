---
title: "Complemento — tipos v4 completos, ontologia e subtipos (100% SAP)"
tipo: reference
pertence_a: skill leanix-classificar-metamodelo
origem: "doc oficial SAP LeanIX v4: meta-model, application/interface/it-component/platform/tech-category modeling guidelines"
somente_leitura: true
---

# Complemento — meta-modelo v4 completo, ontologia e subtipos

A árvore principal da SKILL.md foca no desempate mais frequente (Interface → Data Object → P1 → P2 →
P3 → Platform/Provider/Tech Category). Este complemento cobre **os tipos que a árvore principal não
detalha** (Objective, Initiative, Organization, Data Product, System) e fecha **3 regras de ontologia
e precisão** que a doc oficial exige. Tudo agnóstico de tenant e ancorado na SAP.

> Fonte canônica: [SAP LeanIX Meta Model v4](https://help.sap.com/docs/leanix/ea/meta-model?locale=pt-BR)
> e os *modeling guidelines* por tipo em `docs-eam.leanix.net`. Citações traduzidas/parafraseadas para
> conformidade de licença — ver os links para o texto original.

---

## 1. Os 14 tipos do v4 por camada (visão completa)

A árvore principal decide **Application / IT Component / Interface / Data Object / Platform / Provider
/ Tech Category**. Faltavam estes, que entram **antes** da pré-triagem quando o item é de estratégia,
negócio-pessoa ou ambiente técnico:

| Camada | Tipo | Quando usar | Não confundir com |
|---|---|---|---|
| Estratégia | **Objective** | resultado **mensurável** que a organização quer atingir | Initiative (resultado × ação) |
| Estratégia | **Platform** | agrupamento **estratégico** de apps/tecnologias de grande valor | Tech Category (taxonomia técnica) |
| Estratégia | **Initiative** | **ação planejada** que muda a arquitetura (Idea/Program/Project/Epic) | Objective (ação × resultado) |
| Negócio | **Business Capability** | o **"o quê"** o negócio faz (agnóstico) | Organization (depto) / Business Context (processo) |
| Negócio | **Business Context** | o **"como/onde"**: processo, jornada, produto, value stream | Business Capability (o quê) |
| Negócio | **Organization** | **quem**: unidade, região, entidade, equipe, cliente | Business Capability (não é depto) |
| Aplic./Dados | **Application** | software que entrega função de negócio a usuário/processo | IT Component (tecnologia de base) |
| Aplic./Dados | **Interface** | ligação/troca entre aplicações | Application (ponta, não o fluxo) |
| Aplic./Dados | **Data Object** | **conceito** de informação (Cliente, Fatura) | Data Product (ativo consumível) |
| Aplic./Dados | **Data Product** *(opc.)* | ativo de dados **governado e consumível** (ex.: Customer 360) | Data Object (o conceito) |
| Técnica | **IT Component** | system software / serviço / hardware base consumido por apps | Application (função de negócio) |
| Técnica | **Tech Category** | classificação funcional de IT Components (taxonomia técnica) | Platform (agrupamento estratégico) |
| Técnica | **Provider** | empresa/entidade **fornecedora** de tecnologia/serviço | ligar direto na App (ver §2) |
| Técnica | **System** *(opc.)* | **instância/ambiente** técnico de uma App (DEV/QA/PROD) | Application (contexto lógico) |

**Ordem recomendada de triagem (completa):**
1. É **meta mensurável**? → Objective. É **ação que muda a arquitetura**? → Initiative.
2. É **negócio**? → o quê = Business Capability · como/onde = Business Context · quem = Organization.
3. É **instância/ambiente** (DEV/QA/PROD) de uma app? → System.
4. Senão, entre na **árvore principal da SKILL.md** (Interface → Data Object → P1 → P2 → P3).

---

## 2. Regra de ontologia inegociável — Provider → IT Component → Application

- **Nunca ligue Provider direto na Application.** A cadeia correta é
  **Provider → IT Component → Application**: o Provider fornece o IT Component; o IT Component é
  consumido pela Application. Uma app depende de **vários** fornecedores/hospedagens — ligar Provider
  direto quebra a análise de risco de terceiro e de obsolescência.
- **Tríade de Ouro (reforço):** todo IT Component nasce ligado a **Provider** (quem fornece) +
  **Tech Category** (classificação técnica) + **Application** que consome (impacto). IT Component
  órfão não tem valor analítico.

---

## 3. Platform × Tech Category × Business Capability (definição oficial SAP v4)

> **Fonte da verdade = SAP LeanIX v4.** A organização adotou o meta-modelo SAP LeanIX; ele é pragmático e
> opinativo, e é o que seguimos. Classifique sempre pela **definição oficial de cada tipo na SAP v4**.
> Vocabulário de outros frameworks (ex.: "ABB"/"SBB" do TOGAF) **não** é critério de classificação
> aqui — serve, no máximo, como analogia de fundo, e nunca sobrepõe a SAP.

**Platform (SAP v4):** "agrupamentos de **capacidades, aplicações ou tecnologias estrategicamente
relevantes** que impulsionam simplificação e padronização e ajudam a TI a focar nos benefícios de
negócio" ([Platform Modeling Guidelines](https://docs-eam.leanix.net/docs/platform-modeling-guidelines)).
→ O critério é **relevância estratégica / oferta coesa**, não "ser uma camada técnica".

**Tech Category (SAP v4):** "usada para **agrupar IT Components em categorias de tecnologia** — ex.:
Database, Operating System, Server Software; modelo **estável e independente de indústria**",
opcionalmente alinhada à taxonomia **TBM**
([Tech Category Modeling Guidelines](https://docs-eam.leanix.net/docs/tech-category-modeling-guidelines)).
→ O critério é **classificar tecnologia de infraestrutura**, não agrupar aplicações.

| Se o agrupamento é… (critério SAP) | → tipo SAP v4 | Exemplo (fonte SAP) |
|---|---|---|
| **Capacidade de negócio** (o "o quê") | **Business Capability** | Gestão de Identidade, Faturamento |
| **Estratégico** de capacidades/apps/tecnologias | **Platform** | Plataforma de Pagamentos, SAP BTP |
| **Categoria de tecnologia** (classifica IT Components) | **Tech Category** | Database, Operating System, Server Software |

> **Por que `ABB_Aplicacoes` é antipadrão (pela SAP v4):** usar Platform como "camada de aplicações
> agrupadas por característica técnica" não atende a definição SAP de Platform (agrupamento
> **estratégico**) nem a de Tech Category (classifica **IT Components**, não aplicações). Um
> agrupamento de aplicações por **função de negócio** é **Business Capability**; por **oferta
> estratégica** é **Platform**; uma **categoria de tecnologia** que agrupa IT Components é **Tech
> Category**.

> _Nota de fundo (não é critério):_ o LeanIX é um meta-modelo **pragmático e opinativo**; frameworks
> teóricos como o TOGAF usam outro vocabulário (ex.: ABB/SBB). A organização segue o **SAP LeanIX v4** —
> então classifique pela definição SAP, não por equivalências de outros frameworks.

---

## 4. Subtipos v4 — redação oficial (precisão)

- **Application** — subtipos: **Business Application**, **AI Agent**, **Microservice**, **Extension**.
  A SAP destaca: *o subtipo Business Application distingue a aplicação **lógica** de deployments e
  microsserviços* — use Business Application na camada de negócio e conecte Microservice/Deployment a
  ela ([Application Modeling Guidelines](https://docs-eam.leanix.net/docs/application-modeling-guidelines)).
  *Nota:* **Microservice** é subtipo **opcional**, não faz parte do meta-modelo padrão.
- **Interface** — subtipos: **Logical Interface**, **API**, **MCP Server** (o MCP Server conecta
  agentes/ferramentas de IA à EA — [Interface Modeling Guidelines](https://docs-eam.leanix.net/docs/interface-modeling-guidelines)).
- **IT Component** — subtipos: **Hardware, IaaS, PaaS, SaaS, Service, Software, AI Model**.
  O subtipo **SaaS** é o *placeholder* técnico da exceção SaaS (captura hosting, localização, custo)
  enquanto a solução de negócio fica como **Application**
  ([IT Component Modeling Guidelines](https://docs-eam.leanix.net/docs/it-component-modeling-guidelines)).
- **Initiative** — Idea, Program, Project, Epic. **Business Context** — Customer Journey, Process,
  Business Product, Value Stream. **Organization** — Business Unit, Region, Legal Entity, Team, Customer.

> **Atenção à nomenclatura v4:** "AI Agent" (não "Agente de IA") e "AI Model" como subtipos; o nome
> técnico de alguns tipos/subtipos **varia por tenant** (ex.: Tech Category pode aparecer como
> `TechnicalStack` em workspaces customizados). Ao montar consulta MCP, confirme a chave técnica real.

---

## 5. Casos especiais recorrentes (resumo acionável)

- **IA:** agente → **Application / AI Agent** · LLM/modelo fundacional → **IT Component / AI Model** ·
  endpoint do agente → **Interface / MCP Server** · dados governados → **Data Product** · conceitos →
  **Data Object**.
- **SaaS de negócio:** a solução usada pelo negócio é **Application**; a hospedagem SaaS é
  **IT Component / SaaS** (exceção SaaS — ver SKILL.md).
- **Middleware/ESB/API:** plataforma de integração → **IT Component**; troca lógica →
  **Interface / Logical**; endpoint → **Interface / API**; dados → **Data Objects**; apps nas pontas =
  provedora/consumidora.
- **Ambiente técnico (DEV/QA/PROD):** → **System** ligado à Application — **nunca** uma Application por
  ambiente; System **não** liga a Capability/Context/Organization.

---

## 6. Antipadrões a sinalizar (além dos da SKILL.md)

- App ligada **direto a Provider** → usar Provider → IT Component → Application.
- **Uma Application por deployment/ambiente** → System (ou descoberta por CI/CD).
- **Data Product como catálogo de colunas/tabelas** → o detalhe físico fica no catálogo técnico.
- **Capability modelada como departamento** → separar de Organization.
- **Platform como categoria/camada técnica** (`ABB_Aplicacoes` no tenant) → Business Capability
  (agrupa por função de negócio) ou Tech Category (classifica IT Components), conforme §3.

---

## Fontes (doc oficial SAP LeanIX)

- [Meta Model v4](https://docs-eam.leanix.net/docs/meta-model) · [Application](https://docs-eam.leanix.net/docs/application-modeling-guidelines)
  · [Interface](https://docs-eam.leanix.net/docs/interface-modeling-guidelines)
  · [IT Component](https://docs-eam.leanix.net/docs/it-component-modeling-guidelines)
  · [Application vs. IT Component](https://docs-eam.leanix.net/docs/application-vs-it-component)
  · [Platform](https://docs-eam.leanix.net/docs/platform-modeling-guidelines)
  · [Tech Category](https://docs-eam.leanix.net/docs/tech-category-modeling-guidelines)
  · [System](https://docs-eam.leanix.net/docs/system-modeling-guidelines).

> **Fonte da verdade:** meta-modelo **SAP LeanIX v4** (links acima). Outros frameworks (ex.: TOGAF)
> são apenas apoio conceitual e não sobrepõem a SAP. **Conformidade:** citações da SAP
> traduzidas/parafraseadas; o texto original está nos links.
