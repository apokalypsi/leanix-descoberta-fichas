---
id: indice-templates-por-tipo
nome: Índice dos templates de ficha por tipo de Fact Sheet (tenant configurado)
fonte: 'Mapa de tipos a gerar via MCP — a extração real preenche a data e os TPL-NN'
pertence_a: skill leanix-descoberta-fichas
metamodelo_extraido_em: 'AAAA-MM-DD'
revalidar_a_cada_dias: 30
versao_template: '1.1'
---

# Templates de ficha por tipo de Fact Sheet (TODOS os campos)

> ⚠️ **Estado: BOOTSTRAP (pasta sem os `TPL-NN`).** Este template é genérico e **não** traz os templates
> extraídos de um tenant real. Os `TPL-NN-<Tipo>.md` ainda **não existem** — precisam ser **gerados via
> MCP** seguindo `_MODELO-DO-TEMPLATE.md` (o "template do template", que fixa a estrutura canônica).
> Veja o passo a passo em **SKILL.md §0 → "Bootstrap — pasta vazia"**. Ao gerar: preencha a **data real**
> da extração em `metamodelo_extraido_em` e no texto `fonte:`, **regere esta tabela com os links** para
> os arquivos criados, e grave tudo em **UTF-8 sem BOM**. Nunca deixe o placeholder `AAAA-MM-DD`.

Cada template, depois de gerado, lista **TODOS os campos** reais do tipo (nome técnico, **rótulo em PT**,
tipo de dado, obrigatoriedade, condição de aparição), **agrupados por tema** (Identificação, Ciclo de
vida, Criticidade/fit, IA e governança, Autenticação/SSO, Hospedagem, Racionalização TIME/6R, Custos
RESTRITO, Outros), com **opções de seleção em checkbox** `[ ]` para os campos SINGLE_SELECT/
MULTIPLE_SELECT, mais **todas as relações** e as assinaturas, além dos blocos `## 0. Descrição rica` e
`## Diagrama (Mermaid)`. Ao montar a ficha, preencha a coluna **Valor** (marque `[x]` nas opções) e
**Origem**; sem evidência = lacuna. Campos **RESTRITO** (TCO/custo) não têm valor exibido.

> **Lembretes do meta-modelo (confirmar no seu tenant via MCP):**
> **Autenticação:** `Application` (subtipo `aiAgent`) e `Interface` costumam ter `lxAuthentication`
> (MULTIPLE_SELECT: oAuth2/basicAuth/apiKey/openIdConnect/http/mutualTLS/other) + `lxOtherAuthentication`
> (texto livre para HMAC, IAM/SigV4, RSA/AES, SASL/SSL — quando `lxAuthentication=other`).
> **Camada do microsserviço:** `Application` subtipo `microservice` → `lxMicroserviceType` = **backend ·
> ui · data**. **System** pode ter só o subtipo `sap` conforme o tenant.

## Tipos esperados (numeração de referência — confirme a lista real com `get_fact_sheet_types`)

A ordem/numeração abaixo é a convenção de nomeação dos arquivos (`TPL-NN-<Tipo>.md`). A **lista real de
tipos** vem do MCP (`get_fact_sheet_types`): se o seu tenant tiver mais/menos tipos, ajuste a tabela e a
numeração ao gerar. Os links só existem **após** a geração.

| # | Tipo (técnico) | Rótulo | Template (após gerar) |
|---|---|---|---|
| 01 | `Application` | Aplicação | `TPL-01-Application.md` |
| 02 | `ITComponent` | Componente de TI | `TPL-02-ITComponent.md` |
| 03 | `Interface` | Interface | `TPL-03-Interface.md` |
| 04 | `DataObject` | Objeto de Dados | `TPL-04-DataObject.md` |
| 05 | `ApplicationServices` | Serviço de Aplicação | `TPL-05-ApplicationServices.md` |
| 06 | `BusinessCapability` | Capacidade de Negócio | `TPL-06-BusinessCapability.md` |
| 07 | `BusinessContext` | Contexto de Negócio | `TPL-07-BusinessContext.md` |
| 08 | `Organization` | Organização | `TPL-08-Organization.md` |
| 09 | `Provider` | Provedor | `TPL-09-Provider.md` |
| 10 | `Contract` | Contrato | `TPL-10-Contract.md` |
| 11 | `Platform` | Plataforma | `TPL-11-Platform.md` |
| 12 | `TechnicalStack` | Categoria de Tecnologia (Tech Category) | `TPL-12-TechnicalStack.md` |
| 13 | `System` | Sistema | `TPL-13-System.md` |
| 14 | `Objective` | Objetivo | `TPL-14-Objective.md` |
| 15 | `Initiative` | Iniciativa | `TPL-15-Initiative.md` |

> **Fonte dos campos (após gerar):** extração do `dataModel` do tenant via MCP (`get_workspace_context`
> com `include_meta_model`/`include_schema`). Em divergência, prevalece o workspace (reconfirmar via
> `get_workspace_context`). Cadastro é manual (LeanIX é leitura).
