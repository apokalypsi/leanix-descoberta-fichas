---
title: "Building Blocks — ABB e SBB (lente conceitual TOGAF → LeanIX)"
tipo: reference
pertence_a: skill leanix-classificar-metamodelo
origem: "The Open Group TOGAF (Building Blocks) — vocabulário de origem; traduzido para o meta-modelo SAP LeanIX v4"
somente_leitura: true
---

# Building Blocks — ABB e SBB (lente conceitual TOGAF → LeanIX)

> **Fontes (padrão de mercado):** The Open Group — TOGAF, "Building Blocks"
> (https://pubs.opengroup.org/architecture/togaf9-doc/m/chap33.html); TOGAF ADM Fase E —
> Opportunities & Solutions (https://www.visual-paradigm.com/guide/togaf-adm-phase-e-opportunities-solutions/);
> capstera — Architecture Building Block (https://capstera.com/knowledge-hub/glossary/architecture-building-block-abb);
> boldo — ABB vs SBB (https://www.boldo.io/en/wiki/architecture-building-blocks-abbs-vs-solution-building-blocks-sbbs).
> Conteúdo parafraseado para referência.
> **DIRECIONAL:** ABB/SBB é um **conceito do TOGAF usado como lente de raciocínio** — o padrão de
> modelagem adotado é o **meta-modelo do SAP LeanIX v4**. Todo ABB/SBB é **traduzido para Fact Sheets
> LeanIX** (ABB → Business Capability / Tech Category / Platform; SBB de negócio → Application; SBB de
> tecnologia → IT Component + Provider). **Em conflito, prevalece o LeanIX** (ver a árvore de decisão
> principal no SKILL.md e `complemento-tipos-e-ontologia-v4.md`).

> **Para que serve nesta skill:** ao classificar um item, distinguir se a pessoa está descrevendo uma
> **capacidade agnóstica** (ABB → tende a Business Capability / Tech Category / Platform) ou uma
> **implementação concreta** (SBB → tende a Application, ou IT Component + Provider). Resolve o
> antipadrão de nomear uma capacidade com nome de produto (ex.: "ABB Salesforce").

## 1. O que é um Building Block

Um **Building Block** é um pacote (potencialmente reutilizável) de capacidade de negócio, de TI
ou de arquitetura, que **pode ser combinado com outros** para entregar arquiteturas e soluções.
Pense em "peças de Lego" da organização: cada bloco provê uma funcionalidade específica e, montados
segundo um design claro, formam um sistema coeso e interoperável.

O TOGAF distingue dois tipos: **ABB** (o que a funcionalidade faz) e **SBB** (como/com o que ela é
implementada).

## 2. ABB — Architecture Building Block

**Definição:** descreve **o que** uma capacidade faz, de forma **independente de produto ou
fornecedor**. É o bloco **conceitual/lógico** — o tipo, o padrão, o requisito de capacidade.

**Características:**
- Define funcionalidade, requisitos, atributos de qualidade e **interações/interfaces** esperadas.
- É **tecnologia-agnóstico** (não cita vendor nem produto).
- É **estável** no tempo (muda menos que a implementação).
- Nasce nas fases de arquitetura do TOGAF ADM (B — Negócio, C — SI/Dados e Aplicações, D — Tecnologia).

**Exemplos (genéricos):**
- "Serviço de Autenticação de Identidade" (capacidade de autenticar usuários).
- "Barramento de Integração" (capacidade de mediar mensagens entre sistemas).
- "Repositório de Dados Mestres" (capacidade de manter dados mestres consistentes).
- "Gestão de Relacionamento com Cliente (CRM)" como **capacidade**, não como produto.

> **No LeanIX:** um ABB vira tipicamente **Business Capability** (o "o quê" do negócio), **Tech
> Category** (quando é uma classe de tecnologia, ex.: "Message Broker") ou **Platform** (quando é um
> agrupamento estratégico). Nunca um produto nomeado.

## 3. SBB — Solution Building Block

**Definição:** representa a **implementação concreta** que **realiza** um ABB — um produto de
fornecedor nomeado, um módulo de aplicação específico, uma peça de infraestrutura.

**Características:**
- **Específico de produto/fornecedor** (nomeia a tecnologia).
- Define **como** a capacidade é entregue (configuração, versão, componentes reais).
- Muda mais rápido (acompanha mercado, contratos, obsolescência).
- É **mapeado na Fase E do ADM (Opportunities & Solutions)**, quando a arquitetura abstrata vira
  plano de implementação concreto.

**Exemplos (genéricos):**
- Para o ABB "Serviço de Autenticação de Identidade" → SBB **Keycloak** ou **Microsoft Entra ID**.
- Para o ABB "Barramento de Integração" → SBB **Apache Kafka** ou **MuleSoft**.
- Para o ABB "Repositório de Dados Mestres" → SBB **Informatica MDM**.
- Para o ABB "CRM" → SBB **Salesforce** ou **Microsoft Dynamics 365**.

> **No LeanIX:** um SBB vira **Application** (quando é um software que entrega função de negócio — ex.:
> Salesforce) ou **IT Component + Provider** (quando é fundação técnica — ex.: Kafka da Confluent).
> Produto SaaS que entrega capacidade de negócio e rastreia custo/hosting → **exceção SaaS**
> (Application funcional + IT Component subtipo SaaS). Ver a árvore de decisão principal no SKILL.md.

## 4. ABB × SBB — comparação

| Dimensão | ABB (Architecture Building Block) | SBB (Solution Building Block) |
|---|---|---|
| Responde | **O quê** / qual capacidade | **Como** / com o quê (implementação) |
| Fornecedor | Agnóstico (sem vendor) | Específico (produto nomeado) |
| Natureza | Conceitual / lógico | Concreto / físico |
| Estabilidade | Alta (muda pouco) | Menor (muda com o mercado) |
| Fase do ADM | B, C, D (arquitetura) | **E** (Opportunities & Solutions) |
| Reuso | Padrão reutilizável de capacidade | Implementação que pode ser trocada |
| **Fact Sheet LeanIX** | Business Capability / Tech Category / Platform | Application, ou IT Component + Provider |

> **Regra:** padrões de arquitetura são definidos **primeiro como ABBs** e só então **mapeados a
> SBBs**. Um ABB pode ser realizado por **vários** SBBs candidatos (opções de produto); a escolha
> do SBB é uma decisão de solução (custo, fit, risco, roadmap).

## 5. Relação ABB → SBB

```
ABB (o quê, agnóstico)        realizado por        SBB (como, produto concreto)
"CRM" (capacidade)           ───────────▶         Salesforce | Dynamics 365
"Barramento de Integração"   ───────────▶         Kafka | MuleSoft | RabbitMQ
"Autenticação de Identidade" ───────────▶         Keycloak | Entra ID | Okta
"Camada de Serviços"         ───────────▶         microsserviços concretos do domínio
```

- O **ABB é o alvo arquitetural**; o **SBB é a entrega**. Trocar o SBB (ex.: migrar de um produto
  para outro) **não deve mudar o ABB** — essa é a vantagem de separar os dois.
- **No LeanIX:** a relação ABB → SBB se materializa como Business Capability → Application (uma app
  realiza a capacidade) e Application → IT Component → Provider (a tecnologia que a sustenta).

## 6. Como usar na prática

1. **Ao desenhar a arquitetura (fases B/C/D):** identifique os **ABBs** necessários — as capacidades,
   independentes de produto. Especifique funcionalidade, interfaces e atributos de qualidade.
2. **Ao planejar a solução (fase E):** mapeie cada ABB a um ou mais **SBBs** candidatos; avalie
   opções (build vs. buy, fit, custo, risco, obsolescência) e selecione.
3. **Reuso:** antes de criar um ABB novo, verifique se um ABB existente já cobre a capacidade; antes
   de adotar um SBB novo, verifique se já há um SBB corporativo para aquele ABB.
4. **Rastreabilidade:** mantenha o vínculo ABB → SBB → aplicação/implantação para análise de
   dependência, obsolescência e racionalização.

## 7. Boas práticas e antipadrões

**Boas práticas**
- Nomeie ABBs por **capacidade** ("Gestão de Identidade"), não por produto.
- Mantenha o ABB **estável**; evolua o SBB conforme o mercado.
- Um ABB pode ter **múltiplos SBBs**; registre a opção escolhida e as alternativas.
- Hierarquias de blocos rasas (2–3 níveis) e critérios claros.

**Antipadrões**
- Nomear um ABB com nome de produto (ex.: ABB "Salesforce") — confunde o quê com o como.
- Pular o ABB e ir direto ao SBB — perde a visão de capacidade e dificulta troca de produto.
- Tratar cada versão/patch do produto como SBB novo — controle versão só no nível útil (obsolescência/risco).
- Acoplar a arquitetura (ABB) a um fornecedor específico sem necessidade.

> **Nota de conformidade:** citações do TOGAF/The Open Group e da SAP foram parafraseadas; o texto
> original está nos links. Este material é **lente de raciocínio** — a classificação final segue sempre
> a definição SAP LeanIX v4 (a organização adotou o meta-modelo SAP LeanIX como fonte da verdade).
