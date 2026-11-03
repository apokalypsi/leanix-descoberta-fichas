---
inclusion: always
---

# Segredos, credenciais e dados sensíveis

Regra permanente, válida para **todo código, arquivo, diagrama e resposta** que o agente gera ou
revisa — não só em auditorias de segurança.

## Nunca exponha segredos

- **Nunca** escreva senha, token, API key, secret ou string de conexão **no código** ou em arquivos
  versionados (`.properties`, `.env` commitado, YAML, etc.). Use **variável de ambiente** ou um
  **cofre de segredos**; no código fica só a referência (ex.: `${DB_PASSWORD}`).
- **Nunca** exiba o **valor** de um token/segredo numa resposta, log ou relatório. Refira-se a ele
  **pelo nome** (ex.: `LEANIX_API_TOKEN`).
- Ao encontrar um segredo exposto no código auditado, trate-o como **vazado**: reporte como 🔴 Crítico
  e recomende **rotacionar** a credencial, além de movê-la para o cofre.

## Nunca registre dado sensível / PII em log

- CPF, cartão, CVV, e-mail pessoal, dados de saúde e afins **não entram em log, console, telemetria
  nem em arquivo gerado**. Logue identificadores não sensíveis (ex.: `clienteId`), não o dado em si.
- Mascare/omita PII em exemplos e relatórios; não invente dados reais no lugar.

## Dados restritos da organização

- **TCO / custo / valores financeiros** de aplicações são **restritos**: não entram em texto, tabela,
  diagrama nem arquivo. **Omita** o campo (não falsear com valor fictício). Detalhe:
  `leanix-nucleo` → `references/seguranca-conteudo.md`.

## Ao escrever código novo

Use padrões seguros por padrão: consultas **parametrizadas** (nunca concatenar input em SQL),
validação de entrada na borda, TLS sempre validado, tratamento de erro que **não** engole falhas de
segurança. Baseline: OWASP Top 10; para features de IA, o OWASP LLM Top 10 — trate saída de modelo
como não confiável e não use o system prompt como fronteira de segurança.
