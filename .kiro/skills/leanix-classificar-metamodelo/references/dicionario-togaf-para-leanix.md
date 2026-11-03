---
title: "Dicionário de tradução — TOGAF → SAP LeanIX v4 (ABB/SBB e outros termos)"
tipo: reference
pertence_a: skill leanix-classificar-metamodelo
origem: "SAP LeanIX v4 modeling guidelines + The Open Group TOGAF (vocabulário de origem)"
somente_leitura: true
---

# Dicionário de tradução — TOGAF → SAP LeanIX v4

Material **didático** para quem já pensa em **TOGAF** (ABB, SBB, as 4 camadas do ADM) e está
aprendendo o meta-modelo **SAP LeanIX v4**. Ele traduz o vocabulário que você já conhece para o Fact
Sheet correspondente no LeanIX — como uma "ponte de aprendizado".

> ⚠️ **Regra de ouro.** Isto é um **dicionário de tradução**, **não** um critério de classificação. A
> **fonte da verdade é a definição oficial de cada tipo na SAP LeanIX v4**. Use este de-para para
> *entender/ensinar*; na hora de **classificar**, decida pela definição SAP (SKILL.md + reference
> `complemento-tipos-e-ontologia-v4.md`). Em qualquer conflito, **prevalece a SAP v4**.
>
> **Por que traduzir e não equivaler?** O TOGAF é teórico, acadêmico e exaustivo; o LeanIX é
> pragmático, opinativo e focado em execução. Não há "igual a" 1:1 — há **correspondência prática**.
> Curiosamente, a própria SAP chama **Business Capability** de *"fundamental building block of an
> organization"* ([LeanIX · Business Capability](https://www.leanix.net/en/wiki/ea/business-capability)),
> o que mostra que "building block" existe no LeanIX, só que **materializado em tipos concretos**.

---

## 1. O conceito central: ABB × SBB → onde cai no LeanIX

No TOGAF, **ABB** é o "**o quê**" agnóstico de produto e **SBB** é o "**como/com o quê**" concreto. No
LeanIX isso **não vira um único tipo** — vira tipos diferentes conforme a **camada** e a **natureza**:

| Você pensa (TOGAF) | …e quer dizer | → No LeanIX v4 é | Exemplo |
|---|---|---|---|
| **ABB** de **negócio** | capacidade agnóstica (o "o quê") | **Business Capability** | "Gestão de Identidade", "Faturamento" |
| **ABB** de **agrupamento estratégico** | conjunto estratégico de capacidades/apps/tech | **Platform** | "Plataforma de Pagamentos", "SAP BTP" |
| **ABB** de **tecnologia** | categoria de tecnologia (classifica IT Components) | **Tech Category** | "Database", "Operating System", "Server Software" |
| **SBB** = aplicação concreta | o software que **realiza** a capacidade | **Application** | "Salesforce", "SAP S/4HANA" |
| **SBB** = tecnologia concreta | o produto/serviço técnico que sustenta a app | **IT Component** (+ Provider + Tech Category) | "Oracle DB 19c", "AWS EC2", "Salesforce [SaaS]" |

**Leitura rápida:** um **ABB** no TOGAF, no LeanIX, se parte em **três destinos possíveis** (Business
Capability / Platform / Tech Category) dependendo de ser negócio, estratégico ou técnico. Um **SBB**
vira **Application** (software que realiza) e/ou **IT Component** (tecnologia que sustenta), na
ontologia **Provider → IT Component → Application**.

> **Armadilha nº 1 (o antipadrão `ABB_Aplicacoes` do tenant):** "ABB de aplicações" **não** é um
> balde chamado Platform. Se agrupa apps por **função de negócio** → **Business Capability**; se é uma
> **oferta estratégica coesa** → **Platform**; se classifica **tecnologia** → **Tech Category**.

---

## 2. As 4 camadas do TOGAF ADM → camadas do LeanIX v4

| Domínio TOGAF (ADM) | Camada LeanIX v4 | Tipos LeanIX típicos |
|---|---|---|
| **Business Architecture** (Fase B) | Arquitetura de negócio | Business Capability, Business Context, Organization |
| **Data Architecture** (Fase C) | Aplicações e dados | Data Object, Data Product (opc.) |
| **Application Architecture** (Fase C) | Aplicações e dados | Application, Interface |
| **Technology Architecture** (Fase D) | Arquitetura técnica | IT Component, Tech Category, Provider, System (opc.) |
| **Vision / Strategy** (Fase A, requisitos/motivação) | Estratégia e transformação | Objective, Platform, Initiative |

---

## 3. Dicionário termo a termo (TOGAF/EA comum → LeanIX v4)

| Termo que você usa | → Fact Sheet LeanIX v4 | Observação (definição SAP manda) |
|---|---|---|
| Capability / Business Capability / "o quê" | **Business Capability** | o que a organização faz, agnóstico a processo/tech |
| Capability de TI "CRM/ERP/LMS" | **Business Capability** (ou Application Category) | **não** é Tech Category |
| Value Stream / Process / Customer Journey / Business Product | **Business Context** (subtipo correspondente) | o "como/onde": produto, processo, jornada, value stream |
| Organizational Unit / Actor / Role (dono/uso) | **Organization** (Business Unit/Region/Legal Entity/Team/Customer) | quem é dono/usa a Application |
| Application Component / Logical Application | **Application** (subtipo **Business Application**) | software que entrega função de negócio |
| Application Service / "serviço exposto" | **Interface** (ou Application Service, se o tenant tiver) | o que a app expõe/troca |
| Information / Data Entity (conceito) | **Data Object** | conceito de informação (Cliente, Fatura) |
| Data asset governado e consumível | **Data Product** (opcional) | ativo de dados com owner/consumidores |
| Technology Component / Platform Service / Node | **IT Component** (Hardware/IaaS/PaaS/SaaS/Service/Software/AI Model) | tecnologia/serviço base consumido por apps |
| Technology Category / "tipo de tecnologia" | **Tech Category** | classifica IT Components (Database, OS, Server Software) |
| Vendor / Supplier | **Provider** | empresa fornecedora — liga em **IT Component**, nunca direto na App |
| Requirement / Driver / Goal (mensurável) | **Objective** | resultado mensurável |
| Work Package / projeto de mudança | **Initiative** (Idea/Program/Project/Epic) | ação que muda a arquitetura |
| Ambiente/instância (DEV/QA/PROD) | **System** (opcional) | instância técnica de uma Application |
| Integration / Interface / API | **Interface** (Logical/API/MCP Server) | ligação/troca entre aplicações |
| Agente de IA / LLM / endpoint de agente | **Application/AI Agent** · **IT Component/AI Model** · **Interface/MCP Server** | ver "Casos especiais" na reference de complemento |

---

## 4. Como usar este dicionário para aprender (passo a passo)

1. **Parta do termo que você já conhece** (ex.: "isto é um ABB de tecnologia").
2. **Traduza** pela tabela (§1 ou §3) para achar o **candidato** no LeanIX (ex.: Tech Category).
3. **Confirme pela definição oficial SAP v4** do tipo candidato — é aqui que a decisão se fecha. Se a
   definição SAP não bater, o termo TOGAF estava te levando ao tipo errado: siga a SAP.
4. **Aplique a ontologia** Provider → IT Component → Application e as regras da SKILL.md.

> **O TOGAF te leva à vizinhança certa; a SAP v4 crava o endereço.** Use o de-para para reduzir o
> espaço de busca, nunca para pular a definição oficial.

---

## 5. Três confusões que o vocabulário TOGAF costuma causar (e a correção SAP)

1. **"ABB = Tech Category sempre."** ❌ Não. ABB de **negócio** = Business Capability; de
   **agrupamento estratégico** = Platform; só o ABB **técnico** = Tech Category.
2. **"Produto de fornecedor (SBB) = IT Component."** ❌ Não por ser de terceiro. Salesforce, S/4HANA,
   Workday, Zoom e o próprio SAP LeanIX são **Application** pela SAP. Decide-se por **função**, não por
   fornecedor (ver heurísticas na SKILL.md; SaaS → exceção SaaS = Application + IT Component).
3. **"CRM/ERP são categorias técnicas."** ❌ São **Business Capability / Application Category**. Tech
   Category é só **infraestrutura** que agrupa IT Components (Database, OS, Server Software).

---

## Fontes

- **SAP LeanIX v4 (fonte da verdade):**
  [Meta Model](https://docs-eam.leanix.net/docs/meta-model) ·
  [Business Capability](https://docs-eam.leanix.net/docs/business-capability-modeling-guidelines) ·
  [Business Context](https://docs-eam.leanix.net/docs/business-context-modeling-guidelines) ·
  [Application](https://docs-eam.leanix.net/docs/application-modeling-guidelines) ·
  [IT Component](https://docs-eam.leanix.net/docs/it-component-modeling-guidelines) ·
  [Tech Category](https://docs-eam.leanix.net/docs/tech-category-modeling-guidelines) ·
  [Platform](https://docs-eam.leanix.net/docs/platform-modeling-guidelines) ·
  [System](https://docs-eam.leanix.net/docs/system-modeling-guidelines).
- **TOGAF (vocabulário de origem, apoio):** The Open Group — ADM e Building Blocks; detalhe de ABB/SBB
  (o "o quê" × o "como") no reference `building-blocks-abb-sbb.md` desta mesma skill.

> **Nota de conformidade:** citações da SAP e do TOGAF traduzidas/parafraseadas; o texto original está
> nos links. Este de-para é apoio de aprendizado — a classificação segue sempre a definição SAP v4.
