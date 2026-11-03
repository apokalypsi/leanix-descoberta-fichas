---
name: leanix-descoberta-fichas
description: Mapeia o meta-modelo COMPLETO do SAP LeanIX do tenant configurado e VALIDA + CRIA fichas de qualquer um dos 15 tipos de Fact Sheet (Application, ITComponent, Interface, DataObject, BusinessCapability, Provider, Platform, Tech Category e outros), com todos os campos — tipo de dado, obrigatoriedade, valores de seleção, campos condicionais (camada backend/ui/data, autenticação lxAuthentication/lxOtherAuthentication, governança de IA), tags, hierarquia e grafo de relações. Tem um template por tipo em references/templates-por-tipo/. Faz dois modos: (1) FICHA/VALIDAÇÃO POR TIPO — gera ou valida a ficha campo a campo; (2) DESCOBERTA DE REPOSITÓRIO — varre um repo/pasta e identifica todas as fichas possíveis com tipo, subtipo, tags, autenticação e relações. Use quando o usuário pedir para "montar/validar a ficha de X", "quais campos/subtipos/tags/relações tem o tipo Y", "template de cadastro de Z" ou "ler este repositório e gerar todas as fichas". NÃO escreve no catálogo — entrega as fichas prontas para cadastro manual.
metadata:
  versao: "3.2"
  tipo: skill
  categoria: metamodelo-cadastro-leanix
  autor: "Arquitetura Corporativa"
  papel: "valida E cria/mapeia fichas de QUALQUER tipo do meta-modelo LeanIX do tenant configurado"
  origem: "meta-modelo do SAP LeanIX — dataModel completo (15 tipos, todos os campos, valores, obrigatoriedade, condicionais incl. autenticação e camada de microsserviço, relações) + allTagGroups via API pathfinder/graphql. Fonte de verdade: references/templates-por-tipo/. Reconfirme os valores contra o SEU tenant via MCP na execução."
  atualizacao_3_0: "reconsulta completa do meta-modelo (TODOS os campos de TODOS os tipos); campos de autenticação lxAuthentication + lxOtherAuthentication (Application/Interface); camada do microsserviço (backend/ui/data); 1 TEMPLATE por tipo em references/templates-por-tipo/ com todos os campos e checkbox de seleção"
  somente_leitura: true
  requer_mcp: false
  mcp_opcional: "confirmar valores/rótulos ao vivo e reusar Fact Sheets existentes quando houver VPN + MCP leanix"
  entrada: "modo FICHA/VALIDAÇÃO (tipo + o que se sabe do item) OU modo DESCOBERTA (repositório/pasta de documentos)"
  saida: "ficha(s) de cadastro campo a campo (TODOS os campos; valor OU lacuna + origem) + tags + autenticação + relações; no modo descoberta, o conjunto completo de fichas do sistema com o grafo"
---

# Meta-modelo LeanIX — validar e criar fichas de qualquer tipo (+ descoberta de repositório)

A skill carrega o **meta-modelo real do tenant configurado** (15 tipos, subtipos, campos com tipo/
obrigatoriedade/valores/condicionais, **tags** configuradas, hierarquia e **grafo de relações**,
extraídos do próprio workspace via API) e opera em **dois modos**:

- **Modo FICHA (por tipo):** recebe o tipo/subtipo e o que se sabe do item e **monta a ficha de
  cadastro** campo a campo. Use quando o usuário já sabe o que quer cadastrar.
- **Modo DESCOBERTA (por repositório):** varre um **repositório ou pasta de documentos**, **identifica
  todos os Fact Sheets** que o sistema geraria (Application + IT Components com versão + Interfaces +
  Data Objects + Provider + Tech Category + Capability/Organization…), com tipo, subtipo, tags e
  relações, e entrega **o conjunto de fichas** ligadas. Use quando o usuário pedir para "ler o repo e
  identificar tudo que viraria Fact Sheet" ou "gerar todas as fichas deste sistema".

A fonte é o **meta-modelo real do workspace**, não a documentação genérica — a ficha reflete o que **de
fato existe** para preencher, incluindo campos customizados do tenant, condicionais e os tag groups reais.

> **Somente leitura.** A skill **entrega a(s) ficha(s)**; o cadastro é **manual, pelo time de
> Arquitetura** (o LeanIX é leitura para o agente). A skill **não cria, não altera e não escreve** nada.

> **Regra de ouro — não inventar.** Todo valor vem de **evidência** (o que o usuário informou OU, no
> modo descoberta, `arquivo:trecho` do fonte) OU fica como **lacuna** (campo a preencher). Nunca
> suponha. **Nomes de campo, valores, tags, subtipos e relações** vêm das referências do meta-modelo
> real; não invente nada fora delas. Tecnologia sempre com **versão** quando o fonte declarar.

---

## 0. Frescor dos templates — revalidar a cada 30 dias via MCP (OBRIGATÓRIO)

Os templates `references/templates-por-tipo/TPL-NN-<Tipo>.md` são a **fonte de verdade dos campos**,
gerados do meta-modelo real do tenant via MCP (ver "Bootstrap" abaixo quando ainda não existirem). Uma
vez gerados, carregam a **data da extração** e uma **janela de revalidação** no front matter — porque o
meta-modelo **evolui** (SAP e a organização adicionam/removem campos, valores de seleção e relações) e o
template pode ficar defasado:

```yaml
metamodelo_extraido_em: 'AAAA-MM-DD'   # data da última extração do meta-modelo
revalidar_a_cada_dias: 30              # janela de frescor
versao_template: '1.0'                 # sobe a cada alteração de conteúdo do template
```

> **`versao_template`** rastreia **alterações do template**: começa em `1.0` e **sobe sempre que o
> conteúdo mudar** numa revalidação (meta-modelo alterado). Convenção: **minor** (`1.0`→`1.1`) para
> ajustes pontuais (um campo novo, novo valor de seleção, mudança de rótulo/obrigatoriedade); **major**
> (`1.x`→`2.0`) para mudança estrutural grande (campos removidos em massa, reorganização de seções). Só
> renovar a data **sem** mudar conteúdo **não** incrementa a versão.

**Regra (rode SEMPRE, logo ao ativar a skill, antes de montar qualquer ficha):**
0. **Verifique primeiro se os `TPL-NN` existem.** Se a pasta `templates-por-tipo/` tiver só
   `_MODELO-DO-TEMPLATE.md` + `_INDICE.md` (sem `TPL-NN`), é **bootstrap** → vá para "Bootstrap — pasta
   vazia" abaixo e **gere os templates via MCP** antes de qualquer coisa. Só depois aplique os passos de
   frescor.
1. **Leia `metamodelo_extraido_em`** do(s) template(s) que vai usar (ou do `_INDICE.md`, que reflete o
   lote) e calcule **dias decorridos = hoje − `metamodelo_extraido_em`**. (Se o valor ainda for o
   placeholder `AAAA-MM-DD`, trate como bootstrap/defasado: gere ou revalide via MCP.)
2. **Se dias decorridos ≤ 30:** siga normalmente — o template está dentro da janela de frescor.
3. **Se dias decorridos > 30:** **avise o usuário** com uma frase objetiva no início da resposta, ex.:
   > ⚠️ Os templates deste tipo foram extraídos em `<data>` (há `<N>` dias, acima da janela de 30).
   > Recomendo revalidar o meta-modelo no LeanIX via MCP antes de confiar nos campos. Quer que eu
   > revalide agora?
   Não bloqueie a tarefa — siga com o template atual se o usuário preferir, mas deixe o aviso visível.

### Bootstrap — pasta `templates-por-tipo/` VAZIA (gerar os templates via MCP)

Este template vem **sem** os `TPL-NN-<Tipo>.md` preenchidos de um tenant real (foram removidos para
manter o template genérico). Então, **antes de montar qualquer ficha, verifique a pasta**
`references/templates-por-tipo/`:

- **Se houver só `_MODELO-DO-TEMPLATE.md` (e nenhum/poucos `TPL-NN`), ou algum tipo sem template:** a
  pasta está em estado de **bootstrap**. Para gerar os templates é **obrigatório MCP + VPN** ao `leanix`
  (é a única fonte do meta-modelo real). Avise o usuário e, com o aceite:
  1. **Leia `_MODELO-DO-TEMPLATE.md`** — ele é o **"template do template"**: define a estrutura canônica
     (front matter, blocos `## 0. Descrição rica`, `## Campos` por tema, `## Relacoes`, `## Assinaturas`,
     `## Diagrama`), as 8 colunas da tabela de campos e de onde cada dado vem no MCP.
  2. **Puxe o meta-modelo ao vivo** (ver passos de Revalidação abaixo) para **cada tipo** de
     `get_fact_sheet_types` e **gere um `TPL-NN-<Tipo>.md` por tipo**, seguindo o modelo com o **máximo de
     detalhe**: TODOS os campos, TODAS as opções de seleção `[ ]`, TODAS as relações com seus atributos.
     Nunca resuma ("demais seguem o padrão") — liste tudo.
  3. **Regere `_INDICE.md`**, atualize `tags-grupos-leanix.md` (via `allTagGroups`) e
     `estrutura-subtipos-relacoes.md`, e **preencha a DATA REAL** da extração em `metamodelo_extraido_em`
     e no texto `fonte:` de todos os arquivos (nunca deixe `AAAA-MM-DD`). Grave em **UTF-8 sem BOM**.
  4. Só então prossiga para montar a(s) ficha(s). O cadastro no LeanIX segue **manual** (somente leitura).
- **Se não houver MCP/VPN no bootstrap:** declare que **não é possível gerar os templates** sem o
  meta-modelo ao vivo e **não invente** campos/valores. Ofereça seguir apenas com a classificação de
  tipo/subtipo (que independe dos TPL) e marque os campos como lacuna "requer extração do meta-modelo".
- **Se os `TPL-NN` já existirem** (tenant já extraído): não faça bootstrap — siga para a regra de
  frescor abaixo (revalidar se > 30 dias).

> O **formato** dos templates é fixo (vem de `_MODELO-DO-TEMPLATE.md`); o **conteúdo** vem do MCP. Essa
> separação garante que, em qualquer tenant, os templates gerados fiquem idênticos em estrutura — só
> mudam os campos/valores reais daquele workspace.

**Revalidação via MCP (quando o usuário aceitar, ou sempre que houver VPN + MCP `leanix`):**
1. Puxe o meta-modelo ao vivo: `get_workspace_context` (`include_meta_model: true`,
   `fact_sheet_types: ["<Tipo>"]`, `include_schema: true`) — e, para o conjunto, confira os **15 tipos**
   com `get_fact_sheet_types`.
2. **Compare campo a campo** o que voltou do MCP com o template: campos novos, campos removidos, mudança
   de obrigatoriedade/condicional, novos valores de seleção, novas relações.
3. **Meta-modelo NÃO mudou:** apenas **renove a data** — atualize `metamodelo_extraido_em` para hoje nos
   templates verificados (a janela de 30 dias reinicia). **`versao_template` NÃO muda** (nada de conteúdo
   mudou).
4. **Meta-modelo MUDOU:** **atualize o template refletindo a diferença, mantendo EXATAMENTE a mesma
   estrutura** — o mesmo front matter (mais a data nova), os mesmos cabeçalhos/tabelas e o mesmo
   agrupamento temático (Identificação, Ciclo de vida, …), as colunas `Campo · Rótulo (PT) · Tipo ·
   Obrig. · Condicional · Opções · Valor · Origem` e o padrão de checkbox `[ ]`. Só muda o **conteúdo**
   (linhas de campo/valores), nunca o **formato**. Registre a data nova em `metamodelo_extraido_em`,
   **incremente `versao_template`** (minor para ajuste pontual, major para mudança estrutural grande) e,
   se o número/identidade dos tipos mudar, atualize o `_INDICE.md` e a contagem de tipos (hoje **15**).
5. **Sem MCP/VPN:** não dá para revalidar — **mantenha o aviso** de template possivelmente defasado e
   siga declarando a limitação ("não foi possível revalidar o meta-modelo ao vivo"). Nunca invente a
   mudança.

> **Nunca altere a estrutura do template na revalidação.** A regra é: atualizar **dados e data**,
> preservar **layout**. Assim as fichas geradas continuam no mesmo formato e o diff fica legível.
> A data também fica embutida no texto do campo `fonte:` — na dúvida, `metamodelo_extraido_em` manda.

---

## 1. Escolha do modo e entrada

Primeiro, identifique o **modo**:
- O usuário apontou um **repositório/pasta** e quer "identificar/gerar todas as fichas"? → **modo
  DESCOBERTA** (seção 2B).
- O usuário quer a ficha de **um tipo específico** (sabe o que vai cadastrar)? → **modo FICHA** (seção 2A).

Entrada do **modo FICHA**:
- **Qual tipo** cadastrar. Se ele descrever o item sem saber o tipo, **classifique primeiro**
  (`leanix-classificar-metamodelo`) e confirme.
- **Qual subtipo** (`category`) — **muda os campos** (condicionais) e as tags aplicáveis. Sem saber,
  monte o subtipo mais provável e marque os condicionais dos outros como "aparecem se o subtipo for X".
- **O que se sabe do item** → vira valor; o que faltar vira **lacuna**.

Entrada do **modo DESCOBERTA**:
- **A pasta/repositório** do sistema (um sistema por conjunto de fichas; mono-repo → separe por sistema).

> **Alvo obrigatório — sempre aponte onde está o projeto.** O modo DESCOBERTA **não roda sem um alvo
> explícito**. O alvo pode ser **uma pasta local** (caminho no disco) **ou um repositório** (pasta já
> clonada, ou uma URL de repositório que o usuário forneça):
>
> - Se o alvo não estiver claro na mensagem, **pergunte o caminho da pasta ou o repositório antes de
>   varrer** — não assuma o workspace atual nem um diretório qualquer.
> - Se outra skill/orquestradora acionar o modo DESCOBERTA, use exatamente o caminho do repositório que
>   ela passar como alvo. Se ele não vier, peça-o antes de rodar.
>
> Registre no cabeçalho da entrega **qual alvo foi varrido** (caminho/repo + data), para o leitor saber
> a origem das fichas. Nunca invente o alvo nem misture dois sistemas numa única varredura.

---

## 2A. Modo FICHA — passo a passo (por tipo)

### Passo 0 — Confirmar o tipo (e o subtipo)
Confirme o **tipo** de Fact Sheet. Se houver dúvida de classificação, resolva primeiro (skill
`leanix-classificar-metamodelo`). Identifique o **subtipo** (`category`) — ele define quais campos
condicionais entram na ficha.

### Passo 1 — Carregar o mapa de campos do tipo
Abra `references/templates-por-tipo/TPL-NN-<Tipo>.md` (o template do tipo). Ele traz **TODOS os
campos** com: tipo de dado, `**SIM**` quando obrigatório, **opções de seleção em checkbox `[ ]`**,
**condição** de aparição (campo condicional), e todas as relações. Para a **camada, regra de nome,
hierarquia, workflow de aprovação e ontologia** do tipo, consulte `references/estrutura-subtipos-relacoes.md`.

### Passo 2 — Montar a ficha
Para cada campo do tipo (filtrando pelos condicionais que se aplicam ao subtipo escolhido), produza uma
linha com: **campo · tipo de dado · obrigatório? · valores permitidos (se seleção) · valor preenchido
OU "— lacuna" · origem da evidência**. Separe em seções:
- **Identificação** (name/base fields, `category`/subtipo, `alias`, `externalId`/`external_ID`).
- **Campos próprios do tipo** (os específicos, inclusive customizados e condicionais aplicáveis).
- **Ciclo de vida** (`lifecycle` e afins).
- **Relações mínimas** (as relações recomendadas para o tipo — ver a referência e a ontologia).
- **Assinaturas (pessoas)** — papéis recomendados (Responsible obrigatório quando o workflow exigir).

Marque cada campo como **Conclusivo** (tem evidência), **Lacuna** (obrigatório/recomendado sem valor)
ou **Requer validação** (inferência a confirmar). Respeite os campos **obrigatórios** e o **workflow de
aprovação** do tipo (ex.: Application e ITComponent exigem `description`; Application exige subscription
Responsible).

> **Antes de fechar a ficha, aplique as REGRAS DE ENRIQUECIMENTO OBRIGATÓRIAS da seção 2C**
> (descrição rica · responsáveis/assinaturas · capacidade de negócio · organização/equipe).

### Passo 3 — (OPCIONAL) Confirmar/enriquecer via MCP — só se houver VPN + MCP `leanix`
A skill **não exige** MCP; a referência já traz o meta-modelo real. Se o MCP estiver disponível, use-o
só para **confirmar ao vivo** (o workspace pode ter mudado) e para **reusar**:
1. `get_workspace_context` (`include_meta_model: true`, `fact_sheet_types: ["<Tipo>"]`) — confere campos,
   valores de seleção, obrigatoriedade e rótulos atuais do tenant. **Se divergir da referência,
   prevalece o MCP** (e vale atualizar a referência).
2. `filter_inventory` / busca por nome — procura se o item **já existe** (não duplicar) e os Fact Sheets
   das **relações** (para referenciar o ID existente). Sem MCP, marque "verificar reúso no cadastro".

### Passo 4 — Entregar
Entregue a ficha (tabela por seção) + **Resumo** (quantos campos preenchidos × em lacuna; relações a
criar; tags aplicadas) + **Próximo passo** (o que o time cadastra primeiro). Entregue em Markdown
(se pedido, em outro formato) e salve em `documentos-gerados/`.

> **Tags:** além dos campos, aplique as **tags** dos tag groups que valem para o tipo
> (`references/tags-grupos-leanix.md`). No modo FICHA, as de negócio ("a definir pelo time"); no modo
> descoberta, várias saem direto do fonte (linguagem, tipo de banco, ABB de aplicação).

---

## 2B. Modo DESCOBERTA — passo a passo (por repositório)

Varre o fonte e identifica **todos** os Fact Sheets que o sistema geraria. Guia completo em
`references/descoberta-repo-para-fichas.md` (sinais do repo → tipo/subtipo/tag/relação).

### Passo 0 — Escopo (quantas Applications há?)
Confirme a **pasta/repositório** e **quantas Applications** ele gera. **A unidade de agrupamento da
saída é a Application** (o nó central da ontologia; Interfaces/IT Components/Data Objects pertencem a
uma app):
- **1 Application** (caso comum) → um único conjunto na pasta do repo (`<saida>/<repo>/`).
- **Mais de uma Application** (mono-repo, ou repo que gera 2+ apps — ex.: um agente + um hub local) →
  **divida por aplicação**: uma subpasta por app (`<saida>/<repo>/<app>/`), cada uma com seu consolidado
  + arquivos por tipo. Os Fact Sheets compartilhados (Provider, IT Component de plataforma, Organization,
  Capability) aparecem em cada app que os usa, marcados como **reúso** (mesmo ID), não duplicados.
Liste os arquivos relevantes (manifestos, lockfiles, Dockerfile/IaC, `mcp.json`, OpenAPI, schemas,
CODEOWNERS). Repo grande: delegue a varredura a um **sub-agente de contexto**.

### Passo 1 — Varrer e coletar evidência
Leia o fonte e colete **com origem (arquivo:trecho)** cada sinal da tabela "sinal → Fact Sheet" de
`descoberta-repo-para-fichas.md` (§2). Levante a **stack COM VERSÃO** dos lockfiles/manifestos/
Dockerfile (versão efetiva do lock; `latest`/ausente = "— não declarada"): cada tecnologia relevante
vira um **ITComponent** rico (nome + versão + alias/pacote + ecossistema + subtipo + fornecedor +
dependência direta×transitiva + origem) para o match posterior.

### Passo 2 — Classificar cada item e aplicar tags
Para cada item levantado, decida **tipo + subtipo** (dúvida → `leanix-classificar-metamodelo`;
gatilhos de subtipo em `estrutura-subtipos-relacoes.md` §2) e **aplique as tags extraíveis**
(`tags-grupos-leanix.md` §"Tags extraíveis direto do repositório").

### Passo 3 — Montar uma ficha por Fact Sheet
Para cada Fact Sheet, monte a ficha (como no modo FICHA, usando o template do tipo em
`templates-por-tipo/`), com campos do subtipo, tags e as **relações**. Marque Conclusivo × Lacuna ×
Validar e a origem.

### Passo 4 — Conectar o grafo (ontologia)
Ligue as fichas pelo **grafo de relações** (`estrutura-subtipos-relacoes.md` §3) respeitando a
ontologia: **Provider → ITComponent → Application**; **Interface** com as duas pontas + **Data
Object**; **IT Component** com Provider + Tech Category + Application consumidora.

### Passo 5 — (OPCIONAL) MCP — reúso/confirmação
Se houver MCP, confirme campos/valores ao vivo e **reuse** Fact Sheets existentes (AI Model, Provider,
IT Components por nome+versão, Capability, Interfaces, Data Objects) referenciando o **ID** em vez de
duplicar. Sem MCP, marque "verificar reúso/match no cadastro" (não bloqueia a entrega).

### Passo 6 — Enriquecer e entregar o conjunto (agrupado por Application)
**Antes de entregar, aplique a cada ficha as REGRAS DE ENRIQUECIMENTO OBRIGATÓRIAS da seção 2C.**
**Agrupe a saída por Application** (ver Passo 0 e `descoberta-repo-para-fichas.md` §4): **1 app** → um
conjunto em `<saida>/<repo>/`; **2+ apps** → uma **subpasta por app** (`<saida>/<repo>/<app>/`) + um
`_INDICE.md` listando as apps. Em cada conjunto (de 1 app), entregue **um consolidado + um arquivo por
tipo de metamodelo**:
1. **`fichas-leanix.md`** (consolidado) — **evidência coletada numerada `E1…En`** (§2C.7.2) +
   **inventário de fichas** (tabela `Fact Sheet | Tipo/Subtipo | Nome | Tags | criar/reusar/validar |
   Reúso por ID (tenant) | Origem`, §2C.7.4) + ficha resumida por Fact Sheet + **`## Divergências
   registradas`** (§2C.7.3) + **grafo** (Mermaid, cores oficiais por tipo) + **resumo** (fichas por
   tipo; lacunas por severidade; **ordem de cadastro** Provider → AI Model/ITComponents → Application →
   Interfaces/Data Objects → ancoragem de negócio).
2. **Um arquivo por tipo** — nome `leanix-<tipo em minúsculas>.md`. **Não se limite aos 4 base.**
   - **Sempre** (crie os 4; tipo sem evidência = "nenhum \<tipo\> — lacuna"):
     `leanix-applications.md` · `leanix-itcomponents.md` · `leanix-interfaces.md` · `leanix-dataobjects.md`.
   - **Quando houver evidência**, gere também o arquivo do(s) tipo(s) identificado(s) — um por tipo,
     mesmo padrão `leanix-<tipo>.md`, usando o `templates-por-tipo/TPL-NN-<Tipo>.md` correspondente.
     Os mais comuns na descoberta de um repositório:
     `leanix-providers.md` (Provider — TPL-09, o dono/fornecedor por trás de IT Components e sistemas
     integrados) · `leanix-businesscapabilities.md` (BusinessCapability — TPL-06, a capacidade que a app
     apoia, §2C.3) · `leanix-businesscontexts.md` (BusinessContext — TPL-07, processo/jornada de negócio
     apoiado) · `leanix-objectives.md` (Objective — TPL-14, o objetivo/OKR que o sistema serve).
     Outros tipos do meta-modelo (Organization, Platform, TechnicalStack/Tech Category, Initiative…)
     seguem a mesma regra: identificou com evidência → gera `leanix-<tipo>.md`. **Nunca invente um tipo
     sem evidência** — tipo sem sinal no fonte não vira arquivo (fica como lacuna no consolidado).
   Cada ficha **campo a campo** no formato do `TPL-NN-<Tipo>.md` (regra dura §2C.7.1 — **nunca** só
   tabela consolidada), com diagrama por item: Interface mostra **provedor → consumidor** + Data Object;
   Data Object mostra **em qual Interface trafega** + Apps CRUD; Provider mostra **Provider → IT
   Components/Interfaces** que fornece; BusinessCapability/BusinessContext/Objective mostram o **vínculo
   com a Application**. A coluna Origem referencia os códigos `Ex` da tabela de evidência (§2C.7.2).
Se pedido, entregue também em outro formato. Destino: a pasta de saída do
projeto (neste workspace, `documentos-gerados/`).

---

## 2C. Regras de enriquecimento OBRIGATÓRIAS (toda ficha)

Valem para os **dois modos**. Cada ficha só é considerada completa depois de aplicá-las. Mantêm a
regra de ouro: valor com **origem**; sem evidência = **lacuna** (nunca inventar nome de pessoa, de
capacidade ou de equipe).

### 2C.1 Conformidade de template — descrição rica + diagrama (REGRA DURA, toda ficha)

**Toda ficha entregue — em QUALQUER tipo, nos dois modos (FICHA e DESCOBERTA) — DEVE conter os três
blocos do template, ou é considerada INCOMPLETA e não pode ser entregue:**

1. **`## 0. Descrição rica (OBRIGATÓRIA)`** — 2–5 frases **sintetizadas do fonte** (nunca cópia do
   `metadata.description`), respondendo o que é/faz, como se encaixa e com quem integra. Com
   **origem** (`arquivo:trecho`). Sem base no fonte = lacuna; nunca inventar. Texto genérico
   ("componente X") não conta. `description` é **obrigatória para aprovar** Application e ITComponent.

2. **Descrição por natureza do tipo — SEMPRE os DOIS blocos (negócio E técnica), em TODA ficha.**
   Toda ficha, de qualquer tipo, traz **`### Descrição de negócio`** e **`### Descrição técnica`**. O
   que muda por tipo é só **qual é obrigatório** e qual entra **"quando houver"** (ou **"não
   aplicável"**) — nenhum tipo fica com um bloco só. (Alinhado ao `templates-por-tipo/_MODELO-DO-TEMPLATE.md`
   bloco C, que define isso também para a geração dos TPL-NN via MCP.)
   - **Tipos de NEGÓCIO** (`Application`, `BusinessCapability`, `BusinessContext`, `Organization`,
     `Objective`, `Initiative`, `Contract`, `Provider`, `Platform`) → **negócio OBRIGATÓRIA** (rotule
     `### Responsabilidade de negócio`: função de negócio, que processo/capacidade/jornada apoia, valor
     que entrega — "o quê"); **técnica "quando houver"** (materialização técnica relevante) ou "não
     aplicável — item de negócio".
   - **Tipos de TECNOLOGIA** (`ITComponent`, `Interface`, `DataObject`, `TechnicalStack`, `System`,
     `GAP`, `TechnologyAuthority`, `principiosarquiteturais`) → **técnica OBRIGATÓRIA** (o que a
     tecnologia faz + como o sistema a usa, **com versão** quando houver; Interface: o que trafega,
     direção e as duas pontas; DataObject: que conceito representa + classificação PII/LGPD sem
     transcrever o dado); **negócio "quando houver"** (o que o item viabiliza no processo — ex.: a
     interface que fecha a venda) ou "não aplicável — item técnico/infra".
   - **Tipos mistos** (`ApplicationServices`, `Contract`) → **os dois obrigatórios**.
   > Nunca omita um subbloco: o que não se aplica recebe o texto **"não aplicável — …"**, não a
   > ausência. Nunca inventar valor de negócio/técnico; sem evidência no fonte = **lacuna**.

3. **`## Diagrama (Mermaid — cores oficiais por tipo)`** — 1 diagrama de ontologia ligando a ficha
   aos Fact Sheets vizinhos reais, com a **cor oficial de cada tipo** (`leanix-nucleo/references/
   cores-fact-sheet.md`), texto preto e legenda quando houver 3+ tipos. Nunca inventar cor.
   **Sintaxe Mermaid (evita parse error):** cada `classDef` e cada `class` em **sua própria linha** —
   nunca dois `classDef` na mesma linha nem após `; `; prefira o sufixo `:::classe` no nó. Ver a seção
   "Sintaxe Mermaid" em `cores-fact-sheet.md`. Um diagrama que não renderiza é ficha quebrada.

> Os templates `templates-por-tipo/TPL-NN-*.md` (versao_template ≥ 1.1) **já trazem esses três blocos
> prontos** para preencher. Ao montar a ficha, **copie a estrutura do template e preencha** — não
> entregue só a tabela de campos. Uma ficha sem descrição rica, sem a descrição de negócio/tecnologia
> do seu tipo, ou sem o diagrama, está **fora do padrão** e deve ser completada antes de entregar.
> Padrão de qualidade: cada ficha com **descrição rica + bloco de negócio/técnica + diagrama**, campo a
> campo, nada resumido (steering `fichas-leanix-completas`).

### 2C.2 Assinaturas / responsáveis (tentar sempre identificar)
Toda ficha tenta preencher as **Subscriptions** (pessoas + papel). Procure o responsável em:
`CODEOWNERS`, `catalog-info.yaml` (`spec.owner`, `metadata.annotations` de time/contato), `AUTHORS`/
`MAINTAINERS`, `author`/`maintainers` de manifestos (`package.json`, `pyproject.toml`), `docs/`
(contatos), e histórico (quando disponível).
- Achou **pessoa** → preencha **Responsible** (e Accountable quando houver dono claro), com a origem.
- Achou só **grupo/time** (ex.: `group:default/tribo-...`) → registre como **candidato** e marque que
  o **Responsible exige uma pessoa** → vira a **Organization/equipe** da regra 2C.4 e o nome da pessoa
  fica **lacuna** ("indicar pessoa do time"). Não invente nome nem e-mail.
- `Responsible` é **obrigatório** para aprovar Application (workflow) — se faltar pessoa, sinalize como
  bloqueio de aprovação.

### 2C.3 Capacidade de negócio (achar via MCP → senão sugerir cadastro)
Toda Application (e, quando fizer sentido, Interface/Data Object) deve apontar a **Business Capability**
que apoia (`relApplicationToBusinessCapability`). Procedimento:
1. **Infira** a capacidade pelo propósito do sistema (fonte), em linguagem de negócio ("o quê", não "como").
2. **Procure no catálogo via MCP** (se houver VPN + MCP): `search_fact_sheet_by_name` /
   `filter_inventory` com faceta `FactSheetTypes=["BusinessCapability"]`. **Achou** → **reusar** o Fact
   Sheet existente (cite **ID + nome**); nunca crie uma capacidade duplicada.
3. **Não achou** (ou sem MCP) → **sugira o cadastro**: proponha uma **ficha de BusinessCapability nova**
   (nome de negócio + justificativa + hierarquia sugerida) marcada **"a validar/cadastrar"**. Sem MCP,
   registre "reúso a confirmar via MCP".
> Siga a regra `validacao-leanix`: cruze conceito (doc oficial) + dado real (MCP). Sem conseguir o MCP,
> declare a lacuna — não afirme que existe/ não existe.

### 2C.4 Organização — subtipo equipe (team) (achar via MCP → senão sugerir cadastro)
O **dono/time** do sistema vira uma **Organization** (subtipo **`team`** para tribo/squad; `businessUnit`
para BU). Mesma lógica da capacidade:
1. Extraia o time do `catalog-info.yaml` (`spec.owner`, ex.: `group:default/squad-exemplo`)
   e labels (`bu`, `org`, `tribo`).
2. **Procure via MCP** (faceta `FactSheetTypes=["Organization"]`, subtipo `team`). **Achou** → **reusar**
   (ID + nome); liga via `relApplicationToOrganization`. **Não achou/sem MCP** → **sugira o cadastro** de
   uma **Organization/team nova** marcada "a validar/cadastrar".
3. A Organization/team também é a âncora do **Responsible** (2C.2): a pessoa responsável pertence a esse time.

### 2C.5 Governança de IA (quando encontrada, SEMPRE mostrar)
Se o fonte tiver **qualquer sinal de IA** (uso de LLM/modelo, agente de IA, SDK de IA, prompts,
`mcp.json`, config de modelo, biblioteca de ML), **preencha e EXIBA os campos de governança de IA** na
ficha — não os deixe de fora. Os campos existem em três tipos (meta-modelo real):

- **Application** (condicionais): `lxAiUsage` (unknown/noAiUsage/aiAvailable/aiEnabled) ·
  `lxAiType` (neuralNet/llm/machineLearning/Other) · `lxAiTaxonomyDescription` (se `lxAiType=Other`) ·
  `lxAiPotential` (low/medium/high/veryHigh) · `lxAiRisk` (minimal/limited/high/unacceptable, escala
  tipo AI Act) · e, no subtipo **aiAgent**: `lxAiAgentType` (autonomous/conversational) ·
  `lxAiAgentNature` (standard/custom/standardExtended/background) · `lxAiAgentServiceEndpoint` ·
  `lxAiVerified` (verified/unverified/rejected) · `lxSkills` · `lxA2ACapabilities` ·
  `lxAuthentication` (+ `lxMcpServerSupport`/`lxMcpServerEndpoint`/`lxMcpServerClassification`).
- **ITComponent / `aiModel` (ou `llm`)**: `lxAiTechnologyTaxonomy` (llm/modelOptimization/
  modelGovernanceSecurity/otherAi) · `lxAiTechnologyTaxonomyComment` (se `otherAi`) ·
  `lxAiVerified` (verified/unverified/rejected). É o LLM/modelo fundacional (liga a Provider).
- **Interface / `mcpServer`**: `lxAiVerified` · `lxAvailableTools` · `lxMcpServerType`
  (remote/local) · `lxMcpServerClassification` (read/write) · `lxMcpServerEndpoint`.

Regras: preencha o que o fonte permitir inferir **com origem** (ex.: `lxAiUsage=aiEnabled` e
`lxAiType=llm` quando há chamada a um LLM; `lxAiAgentType` conforme o agente é autônomo ou
conversacional). O que não der para inferir fica **lacuna** (ex.: `lxAiRisk` costuma ser **decisão de
governança** → "a definir pelo COE de IA / parecer da autoridade de arquitetura/governança"). **Nunca invente** o
nível de risco/verificação. Em uma ficha com IA, inclua **sempre** uma subseção **"Governança de IA"**
mostrando esses campos (valor ou lacuna) — mesmo que a maioria seja lacuna, a presença de IA tem de
ficar visível. Sinalize também o vínculo ao **AI Model** (ITComponent) e ao parecer do **COE de IA** da
autoridade de arquitetura/governança quando fizer sentido.

> **Resumo das regras de enriquecimento:** (1) descrição rica do fonte; (2) responsável/assinatura
> buscados no fonte (pessoa → Responsible; só grupo → equipe + pessoa em lacuna); (3) capacidade de
> negócio e (4) organização/equipe **procuradas via MCP e, se não existirem, sugeridas como cadastro
> novo**; (5) **governança de IA sempre exibida quando houver sinal de IA** no fonte; mais
> **§2C.1** (descrição rica + bloco negócio/técnica + diagrama em toda ficha) e **§2C.7** (formato de
> entrega do conjunto: ficha campo a campo por item, evidência numerada `E1…En`, divergências
> registradas, coluna reúso por ID, custo como RESTRITO). Sempre com reúso por **ID** quando existir,
> sem duplicar, e com a lacuna declarada quando faltar evidência ou MCP.

### 2C.7 Formato de entrega do conjunto (REGRA DURA, modo DESCOBERTA)

Qualidade-alvo: **completude total** — cada Fact Sheet detalhado campo a campo, nº de fichas = nº de
itens do inventário, nada resumido (steering `fichas-leanix-completas`). Para alcançá-la, o conjunto
entregue DEVE seguir os cinco pontos abaixo — não basta a tabela de campos.

1. **Ficha campo a campo por ITEM — nunca só tabela consolidada.** Em TODO tipo, inclusive os de
   volume (ITComponent, Interface, DataObject com muitos itens), cada item ganha a **ficha campo a
   campo** do `templates-por-tipo/TPL-NN-<Tipo>.md` (campos obrigatórios `lifecycle`,
   `lxTechnologyAssessment(+Category)`, `dataFlowDirection`, `dataClassification` etc. com rótulo PT,
   tipo, obrigatoriedade e opções marcáveis `[x]`). A **tabela consolidada é só o inventário de
   abertura** do arquivo; vem **seguida** das fichas detalhadas de **TODOS os itens**.
   **PROIBIDO resumir:** não detalhe só "fichas representativas" nem feche com "demais itens seguem o
   mesmo padrão", "…" ou equivalente. A ficha é a **base do cadastro manual no LeanIX** — cada Fact
   Sheet identificado tem a sua ficha completa (nº de fichas = nº de itens do inventário). Volume alto
   **não** justifica resumir. Regra transversal: steering **`fichas-leanix-completas`**.

2. **Tabela de evidência NUMERADA `E1…En`** no `fichas-leanix.md`, seção `## 0. Evidência coletada`:
   `# | Sinal | Valor | Origem (arquivo:trecho)`. Os arquivos por tipo **referenciam o código `Ex`**
   na coluna Origem (ex.: "E12") em vez de repetir o `arquivo:trecho` — garante rastreabilidade e
   evita divergência entre arquivos.

3. **Bloco `## Divergências registradas`** no consolidado (quando houver): rótulo declarado × realidade
   do código (ex.: `framework: strands` no catálogo × runtime real LangGraph), versão declarada ×
   versão do lock, nome/subtipo do catálogo × fonte. Cada divergência diz **o que prevalece** (regra:
   prevalece o fonte) e a recomendação. Sem divergência, declarar "nenhuma divergência identificada".

4. **Coluna `Reúso por ID (tenant)`** em toda relação/inventário de reúso, com **estados
   padronizados**: `ID real` (quando houver MCP e o FS existir) · `reúso a confirmar via MCP` (sem MCP) ·
   `não existe → sugerir cadastro`. Nunca inventar ID; sem MCP, o estado é sempre um dos dois últimos.

5. **Campos de custo visíveis como `(RESTRITO — não exibir)`**, não removidos: listar o campo do
   meta-modelo (`tco_*`, `lx*Cost*`, `costTotalAnnual`…) com o valor fixo `(RESTRITO — não exibir)`.
   Deixa explícito que o campo existe e foi omitido por política (melhor para auditoria) — ver
   `leanix-nucleo` → `references/seguranca-conteudo.md`.

> **Decisões de governança (nunca inventar):** `lxAiRisk`, `lxAiVerified` e `lxTechnologyAssessment`
> são **decisão** do COE de IA / autoridade de arquitetura/governança. Marque-os sempre como lacuna atribuída
> ("a definir pelo COE de IA / autoridade de arquitetura/governança"), nunca como valor inferido.

### 2C.6 Autenticação (Application e Interface — quando houver)
Os campos de autenticação existem em **dois tipos** (meta-modelo real):
- **Application** (condicional a `category=aiAgent`): `lxAuthentication` (MULTIPLE_SELECT) +
  `lxOtherAuthentication` (texto, cond. a `lxAuthentication=other`).
- **Interface** (condicional a `category=api/logicalInterface/mcpServer`): `lxAuthentication`
  (MULTIPLE_SELECT) + `lxOtherAuthentication`.

Valores de `lxAuthentication`: `oAuth2 · basicAuth · apiKey · openIdConnect · http · mutualTLS · other`.
Mapeie pela evidência do fonte (JWT/Bearer→`oAuth2` ou `http`; `X-*-Api-Key`/subscription→`apiKey`;
Entra ID/OIDC→`openIdConnect`; SID+token→`basicAuth`; TLS mútuo→`mutualTLS`). Mecanismos **sem enum
próprio** (HMAC de webhook, IAM/SigV4, RSA-OAEP+AES-GCM da Meta, SASL/SSL do Kafka) → marque
`lxAuthentication=other` e **descreva em `lxOtherAuthentication`**. Sem evidência = "a validar".

---

## 3. Como ler os campos (convenções da referência)

- **Tipo de dado:** `STRING` (texto) · `SINGLE_SELECT` (uma opção da lista) · `MULTIPLE_SELECT` (várias)
  · `INTEGER`/`DOUBLE` (número) · `LIFECYCLE` (plan/phaseIn/active/phaseOut/endOfLife) · `LOCATION`
  (local geográfico) · `PROJECT_STATUS` (farol verde/amarelo/vermelho) · **BASE_FIELD** (`name`,
  `displayName`, `description`, `tags`, `subscriptions`, `resources/documents` — existem em todo tipo).
- **`[OBRIGATÓRIO]`** — campo exigido pelo meta-modelo. Além disso, o **workflow de aprovação** de alguns
  tipos torna `description` e certas subscriptions obrigatórias para aprovar o Fact Sheet.
- **`category`** = o **subtipo** do Fact Sheet (os valores listados são os subtipos reais do tenant).
- **"ativado por"** = campo **condicional**: só aparece quando o `category` (ou outro campo) tiver um dos
  valores indicados. Na ficha, inclua só os condicionais do subtipo escolhido.
- **valores em `{ }`** — as opções exatas daquela seleção no tenant. **Use apenas esses valores**; não
  invente nem traduza a chave técnica como se fosse valor.
- **relações** — `relXToY -> TipoDestino (mult A:B)`; `[campos: ...]` são atributos **da relação**
  (ex.: `costTotalAnnual`, `impactType`). Multiplicidade `*` = muitos; `1` = um.
- **templates por tipo** — em `references/templates-por-tipo/` há **um template por tipo** com **TODOS**
  os campos e, nos campos de seleção, **checkbox `[ ]` por opção** (marque `[x]` a escolhida — UMA em
  SINGLE_SELECT, VÁRIAS em MULTIPLE_SELECT). Use o template do tipo ao montar/validar uma ficha.

- **tags** — classificação transversal (não é campo nem Fact Sheet). Cada **tag group** é SINGLE (uma
  tag) ou MULTIPLE (várias) e restrito a certos tipos. Aplique só as tags listadas em
  `tags-grupos-leanix.md` para o tipo; não invente tag. Tag vs. campo vs. Fact Sheet: classificação
  pequena/transversal = tag; dado estável da FS = campo; conceito com identidade/ciclo próprio = FS.

> **Rótulos em português:** as chaves técnicas (ex.: `businessCriticality`, `missionCritical`) são do
> sistema. O **rótulo pt-BR de cada campo** já está na coluna **"Rótulo (PT)"** dos
> `templates-por-tipo/TPL-NN-<Tipo>.md` (extraído das `translations` do tenant). Ao falar com o usuário,
> prefira o rótulo e mostre a chave técnica entre parênteses.
> Obs.: alguns rótulos vêm do tenant com ruído de cadastro (ex.: `Descricao`→"Descrição do TCO",
> `interfaceType`→"Tipo de {{Fact_Sheet}}"); são reproduzidos fielmente da fonte, não corrigidos aqui.

---

## 4. Ontologia e regras inegociáveis (ao sugerir relações)

- **`Provider → IT Component → Application`.** Nunca ligue Provider direto na Application.
  (ITComponent → Provider é `relITComponentToProvider`, mult 1:*.)
- **IT Component órfão não tem valor** — nasce ligado a **Provider** + **Tech Category**
  (`relITComponentToTechnologyStack`) + **Application** consumidora (`relITComponentToApplication`).
- **Interface precisa das duas pontas** — provedora (`relInterfaceToProviderApplication`, mult 1:*) e
  consumidora (`relInterfaceToConsumerApplication`) + o **Data Object** que trafega
  (`relInterfaceToDataObject`).
- **Separe "o quê" (BusinessCapability) de "como/onde" (BusinessContext)** e de "quem" (Organization).
- **AI Agent / Microservice / Extension / Modulo** são subtipos de **Application** (`category`);
  **MCP Server / API / Logical / Event / ETL** são subtipos de **Interface**; **AI Model / llm** é
  subtipo de **ITComponent**. O subtipo **Microservice** libera o campo condicional
  `lxMicroserviceType` com a camada: **`backend`** (Back-end) · **`ui`** (UI/Frontend) · **`data`** (Dados).
- **Versão de tecnologia:** o ITComponent tem o campo `release` (versão) — use-o; e o nome compõe a
  regra de nome com Provider + release.
- **TCO/custo é dado restrito** — os campos `tco_*`, `lx*Cost*`, `lx*TotalCostOfOwnership`,
  `budgetCapEx/OpEx`, `costTotalAnnual` **não entram** na ficha exibida nem em relatório
  (ver `leanix-nucleo` → `references/seguranca-conteudo.md`). Marque-os como "restrito — não exibir".

---

## 5. Índice de referências (`references/`)

**Para montar/validar uma ficha, use dois arquivos juntos:** o **formato** (`template-ficha-cadastro.md`)
+ os **campos do tipo** (`templates-por-tipo/TPL-NN-<Tipo>.md`).

- **`templates-por-tipo/`** — ⭐ **FONTE DE VERDADE DOS CAMPOS.** Um template por tipo
  (`TPL-NN-<Tipo>.md`) com **TODOS os campos** (tipo de dado, obrigatoriedade, condicionais,
  autenticação), **opções de seleção em checkbox `[ ]`**, todas as relações e assinaturas — gerados da
  API, materialização campo a campo do meta-modelo real. **Neste template os `TPL-NN` ainda não existem
  (estado de bootstrap):** a pasta traz só `_MODELO-DO-TEMPLATE.md` (o "template do template" — estrutura
  canônica) + `_INDICE.md` (mapa dos tipos). **Gere os `TPL-NN` via MCP** seguindo a §0 → "Bootstrap"
  antes de montar fichas. Depois de gerados, cada template traz no front matter `metamodelo_extraido_em`
  + `revalidar_a_cada_dias: 30` + `versao_template` — **aplique a regra de frescor da seção 0** (revalidar
  via MCP quando passar de 30 dias, mantendo a mesma estrutura; ao mudar o conteúdo, incremente
  `versao_template`).
- `template-ficha-cadastro.md` — ⭐ **FORMATO DO ENTREGÁVEL** (não os campos): seções A–H, as 5 regras
  de enriquecimento, descrição rica, ancoragem de negócio, governança de IA, resumo. Preencha-o com os
  campos do template do tipo.
- `estrutura-subtipos-relacoes.md` — ⭐ **referência ESTRUTURAL e CONCEITUAL**: por tipo, subtipos
  (`category`), **camada, hierarquia, workflow, regra de nome**, **gatilhos de subtipo** (como o subtipo
  sai do repo), o **grafo de relações** e as **notas de ontologia/antipadrões** (por que modelar assim).
  Use para **entender** o tipo; para a lista exaustiva de campos, vá aos templates por tipo.
- `tags-grupos-leanix.md` — os **20 tag groups** configurados (por tipo, SINGLE/MULTIPLE, valores) e
  quais **tags saem direto do repositório**. Fonte de verdade das **tags**.
- `tech-categories-leanix.md` — a **taxonomia TBM v5 padrão** (`tbmStandard`, 179 nós) para classificar
  cada IT Component numa Tech Category; aponta para a árvore canônica em `leanix-classificar-categoria-tbm`.
  Sem categorias customizadas de tenant nem IDs (confirme o catálogo real via MCP quando precisar).
- `descoberta-repo-para-fichas.md` — o **guia do modo DESCOBERTA**: o que varrer, a tabela
  **sinal no fonte → Fact Sheet**, e como montar e conectar o conjunto.

---

## 6. Diferença para skills vizinhas

- **Esta skill (2 modos):** (1) **FICHA** — dado o tipo, entrega a ficha campo a campo; (2)
  **DESCOBERTA** — varre um repositório e identifica **todas** as fichas (qualquer tipo) com subtipos,
  tags e relações. É o mapa completo do meta-modelo + a planta do(s) formulário(s).
- `leanix-classificar-metamodelo`: decide **qual tipo** é um item (vem **antes**; esta skill a invoca
  em dúvidas de classificação).
- **Agente de IA não é um caso à parte:** é apenas uma **Application subtipo `aiAgent`** — use o
  template do tipo Application (`templates-por-tipo/TPL-01-Application.md`, categoria `aiAgent`, uma vez
  gerado via bootstrap) + a governança de IA (§2C.5) + autenticação (§2C.6). No modo DESCOBERTA, o repo
  de um agente gera Application/aiAgent + IT Component/aiModel (o LLM) + Interfaces + Data Objects, como
  qualquer sistema.

> **Fonte de verdade dos campos e tipos:** `references/templates-por-tipo/` — no estado de bootstrap,
> `_MODELO-DO-TEMPLATE.md` (estrutura canônica) + `_INDICE.md` (mapa dos tipos); os `TPL-NN` são gerados
> do meta-modelo real do tenant via MCP (§0 → "Bootstrap"), um por tipo retornado por
> `get_fact_sheet_types`.
> Consulta ao catálogo real via MCP: `leanix-nucleo` → `references/grounding.md`.
