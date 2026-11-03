---
inclusion: auto
name: fichas-leanix-completas
description: Regra de completude das fichas de descoberta LeanIX — detalhar TODO Fact Sheet campo a campo, listar TODOS os itens de cada tipo e nunca resumir com "demais seguem o mesmo padrão". A ficha é a base do cadastro no LeanIX. Use ao rodar leanix-descoberta-fichas (modo DESCOBERTA ou FICHA).
---

# Fichas LeanIX: detalhar tudo, nada resumido

As fichas geradas pela descoberta LeanIX (`leanix-descoberta-fichas`) **são a base do cadastro
manual no SAP LeanIX pela equipe responsável**. Uma ficha incompleta vira cadastro incompleto. Por
isso a disciplina aqui é **completude total**: cada componente identificado no fonte **tem** de
aparecer detalhado.

## Regra dura

1. **Listar TODOS os itens de cada tipo.** Se a varredura identificou 16 Interfaces, 25 IT Components e
   10 Data Objects, os arquivos por tipo têm **16 / 25 / 10** fichas — não um subconjunto. Nenhum
   componente identificado pode ficar de fora.
2. **Detalhar CADA item campo a campo.** Toda ficha — inclusive nos tipos de volume (ITComponent,
   Interface, DataObject) — traz os três blocos obrigatórios (`## 0. Descrição rica`, descrição de
   negócio e/ou técnica, `## Diagrama`) **mais** a tabela de campos com rótulo PT, tipo, obrigatoriedade,
   opções marcáveis e valor/origem, **mais** as relações mínimas da ontologia. Ver a skill
   `leanix-descoberta-fichas` (§2C.1 e §2C.7) e os templates `templates-por-tipo/TPL-NN-*.md`.
3. **PROIBIDO resumir com reticências.** Não use "demais itens seguem o mesmo padrão", "…", "e assim por
   diante", nem detalhar só "fichas representativas" e encurtar o resto. **Cada FS tem a sua ficha
   completa.** A tabela de inventário no topo do arquivo é só a abertura — vem **seguida** das fichas
   detalhadas de todos os itens, não no lugar delas.
4. **Nada de ocultar/compactar por volume.** Volume alto não justifica resumir: a equipe do LeanIX
   precisa do dado de cada item para cadastrar. Se o arquivo ficar grande, ele fica grande — a
   completude vence a brevidade.
5. **Faltou evidência? Vira lacuna, não omissão.** Um campo sem evidência no fonte é **lacuna atribuída
   explícita** ("— lacuna: responsável a indicar", "a validar com o dono"), nunca um campo omitido nem um
   valor inventado (regra de ouro da skill). Ponta externa a este repo = `lacuna/externa` com reúso por
   ID a confirmar — nunca sumir com a relação.

## Conferência de contagem — PASSO OBRIGATÓRIO antes de entregar (gate)

Este é um **portão de entrega**, não uma sugestão: o conjunto de fichas **não pode ser apresentado
como pronto** sem passar por ele. Faça, para cada alvo, nesta ordem:

1. **Fixe o número esperado por tipo a partir do inventário.** No consolidado (`fichas-leanix.md`), o
   inventário de abertura lista N itens por tipo. Esse N é a **meta de contagem** de cada arquivo por
   tipo. Anote: Application = Na · ITComponent = Ni · Interface = Nf · DataObject = Nd · Provider = Np ·
   (demais tipos com evidência) = N….
2. **Conte as fichas efetivas em cada arquivo.** O número de blocos `## Ficha` em `leanix-<tipo>.md`
   **tem de ser igual** ao N daquele tipo. Conte de fato (não estime) — p.ex. buscando as ocorrências
   de `## Ficha` no arquivo.
3. **Compare e registre.** Para cada tipo: `fichas no arquivo == itens no inventário`? Se **bater em
   todos**, o gate passa. Se **faltar qualquer ficha**, o conjunto está **incompleto** — gere as fichas
   que faltam **antes** de entregar; nunca "fecha o resto" com resumo.
4. **Varra as frases proibidas.** Confirme que não há `seguem o mesmo padrão`, `demais itens/fichas`,
   `representativa(s)`, `e assim por diante` nem reticências de omissão (`…` / `...`) em nenhum
   `leanix-*.md` nem no `fichas-leanix.md`. Qualquer ocorrência = incompleto.
5. **Se o número de itens mudar na contagem, atualize o inventário E o resumo** do consolidado para o
   número real (ex.: cada biblioteca do lock é uma ficha de ITComponent própria — o inventário e o
   resumo têm de refletir o total detalhado, não um "~N" aproximado).
6. **FECHAMENTO DE RELAÇÕES — toda ponta citada numa relação tem de existir como ficha.** Completude
   não é só "contar fichas por tipo"; é **fechar o grafo**. Para cada Fact Sheet, toda ponta destino
   que aparece na tabela `## Relacoes` (coluna Alvo) **tem de ter a sua própria ficha** no conjunto —
   ou estar marcada explicitamente como `lacuna/externa` (ponta fora do escopo deste alvo, com reúso
   por ID a confirmar via MCP). **Ponta citada sem ficha e sem marca de externa = conjunto incompleto.**
   Em especial, pela ontologia (`estrutura-subtipos-relacoes.md` §4) estas relações são **obrigatórias**
   e geram a ficha da ponta:
   - **`relITComponentToProvider` (1:*) — TODO ITComponent tem um Provider.** Logo, **cada Provider
     distinto citado nas fichas de ITComponent precisa de uma ficha em `leanix-providers.md`.** Reúna
     os provedores citados (Spring, AWS, PostgreSQL GDG, open source nominais, etc.), deduplique e
     confira: `nº de Providers distintos citados == nº de fichas de Provider`. Provedor citado sem
     ficha é a falha mais comum — trate como bloqueio de entrega.
   - **Interface precisa das duas pontas** (Application/ITComponent provedor + consumidor) e do Data
     Object que trafega — cada um com ficha (ou externa).
   - **Application** cita Capability/Organization/Interfaces/DataObjects/ITComponents — todos com ficha.
   Procedimento: extraia a coluna Alvo de todas as tabelas `## Relacoes`, deduplique por (tipo, nome),
   e verifique que cada alvo "interno" tem ficha. Os que faltam: **gere as fichas antes de entregar**
   (um Provider genérico "open source"/"Spring" também é uma ficha de Provider, ainda que simples).

> **Só declare o conjunto pronto depois que os 6 passos fecharem.** Uma frase como "22 ITComponents"
> no resumo tem de corresponder a **22 blocos `## Ficha`** em `leanix-itcomponents.md` — contados, não
> presumidos. E **cada Provider citado nos 22 ITComponents tem de ter ficha** (passo 6). Divergência
> entre o resumo, a contagem real ou as pontas de relação é, por si, uma falha de entrega.

**Reforço automático (hook):** o hook `PreToolUse` `leanix-completude-fichas`
(`.kiro/hooks/leanix-completude-fichas.json` + `leanix-completude-check.js`) intercepta a gravação de
qualquer `leanix-*.md` / `fichas-leanix.md` sob `documentos-gerados/` e **pede confirmação** se detectar
uma frase de resumo proibida. Ele é a rede de segurança do passo 4 — **não substitui** a conferência de
contagem (passos 1–3), que continua sendo responsabilidade de quem gera as fichas.

## Checklist antes de entregar o conjunto de fichas

- [ ] **Gate de contagem (acima) fechou:** para CADA tipo, nº de `## Ficha` no arquivo = nº de itens do
  inventário. Divergência = incompleto.
- [ ] **Fechamento de relações (passo 6):** toda ponta citada na coluna Alvo das tabelas `## Relacoes`
  tem ficha própria ou marca `lacuna/externa`. Em especial: **cada Provider citado nos ITComponents
  (`relITComponentToProvider`, obrigatória) tem ficha em `leanix-providers.md`** — contar os provedores
  distintos e comparar com o nº de fichas de Provider.
- [ ] Nenhuma ocorrência de "seguem o mesmo padrão", "demais itens", "…" ou "representativa(s)" no lugar
  de ficha.
- [ ] Cada ficha com os três blocos (descrição rica · negócio/técnica · diagrama) + tabela de campos +
  relações da ontologia.
- [ ] Os 4 arquivos base existem **na pasta do alvo** (applications/itcomponents/interfaces/dataobjects)
  + os adicionais por tipo quando houver evidência (providers, businesscapabilities, …).
- [ ] O **resumo** do consolidado cita o número real por tipo (igual à contagem), sem "~" aproximado.
- [ ] Campos sem evidência = lacuna atribuída; custos/TCO = `(RESTRITO — não exibir)`; nada inventado.

> Esta regra reforça o §2C.7 (1) da `leanix-descoberta-fichas` ("ficha campo a campo por item —
> nunca só tabela consolidada") e o eleva a **critério de entrega**: um conjunto de fichas com qualquer
> item resumido está **incompleto** e deve ser completado antes de ir para a equipe do LeanIX.
