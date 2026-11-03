#!/usr/bin/env node
/*
 * Hook PreToolUse — guarda da regra de completude das fichas LeanIX (cross-platform).
 *
 * Steering de referencia: `fichas-leanix-completas` (regra dura): PROIBIDO resumir um
 * conjunto de fichas com "demais seguem o mesmo padrao", "representativa(s)", reticencias
 * de omissao etc. Cada Fact Sheet identificado tem de ter a SUA ficha completa, campo a
 * campo. Uma ficha resumida = cadastro incompleto no LeanIX.
 *
 * Como funciona: roda ANTES de fs_write / str_replace / fs_append. Le o payload da tool
 * no stdin (JSON com os parametros da chamada). So age quando o alvo e um entregavel de
 * ficha LeanIX (arquivo leanix-*.md ou fichas-leanix.md em documentos-gerados/). Se o
 * texto que esta sendo gravado contem uma das expressoes de resumo proibidas, devolve uma
 * decisao `ask` (pede confirmacao do usuario antes de gravar). Caso contrario, fica
 * silencioso e deixa a gravacao seguir.
 *
 * NUNCA bloqueia por erro: qualquer excecao termina em exit 0 sem decisao.
 * Roda identico em Windows, macOS e Linux (so depende do Node, presente no Kiro).
 */
'use strict';

// Expressoes que caracterizam RESUMO proibido num conjunto de fichas (regra dura).
// Case-insensitive; com e sem acento para robustez de encoding.
const PADROES_PROIBIDOS = [
  /demais\s+(itens|fichas|seguem)/i,
  /seguem\s+o\s+mesmo\s+padr[aã]o/i,
  /segue\s+o\s+mesmo\s+padr[aã]o/i,
  /fichas?\s+representativ[ao]s?/i,
  /\brepresentativ[ao]s?\b/i,
  /e\s+assim\s+por\s+diante/i,
  /\betc\.\.\.|\.\.\.\s*$/im, // reticencias de omissao em fim de linha
];

// So vale para os entregaveis de ficha LeanIX na pasta de saida.
function ehEntregavelDeFicha(p) {
  if (!p) return false;
  const norm = String(p).replace(/\\/g, '/').toLowerCase();
  if (!norm.includes('documentos-gerados/')) return false;
  const base = norm.split('/').pop() || '';
  return /^leanix-.*\.md$/.test(base) || base === 'fichas-leanix.md';
}

// Extrai (caminho, texto) do payload da tool, cobrindo fs_write/str_replace/fs_append.
function extrair(payload) {
  const tool =
    payload.toolName || payload.tool || payload.name || payload.tool_name || '';
  const args =
    payload.arguments || payload.args || payload.input || payload.parameters || payload;

  const caminho = args.path || args.targetFile || args.file || '';
  // fs_write usa `text`; str_replace usa `newStr`; fs_append usa `text`.
  const texto = [args.text, args.newStr, args.content, args.new_str]
    .filter((v) => typeof v === 'string')
    .join('\n');

  return { tool, caminho, texto };
}

function lerStdin() {
  try {
    return require('fs').readFileSync(0, 'utf8');
  } catch (_) {
    return '';
  }
}

try {
  const raw = lerStdin();
  if (!raw.trim()) process.exit(0);

  let payload;
  try {
    payload = JSON.parse(raw);
  } catch (_) {
    process.exit(0); // payload nao-JSON: nao arrisca bloquear
  }

  const { caminho, texto } = extrair(payload);

  if (!ehEntregavelDeFicha(caminho) || !texto) {
    process.exit(0); // nao e ficha LeanIX, ou nada a inspecionar
  }

  const achados = [];
  for (const re of PADROES_PROIBIDOS) {
    const m = texto.match(re);
    if (m) achados.push(m[0].trim());
  }

  if (achados.length === 0) {
    process.exit(0); // completo: segue a gravacao
  }

  const amostra = Array.from(new Set(achados)).slice(0, 3).join(' | ');
  const razao = [
    'Regra de completude das fichas LeanIX (steering fichas-leanix-completas): este',
    'entregavel parece RESUMIR fichas em vez de detalhar cada uma campo a campo.',
    `Trecho(s) suspeito(s): ${amostra}.`,
    'Cada Fact Sheet identificado no inventario tem de ter a SUA ficha completa',
    '(descricao rica + campos + relacoes + diagrama); campo sem evidencia vira lacuna',
    'explicita, nunca omissao. Confirme so se este NAO for um conjunto de fichas',
    '(ex.: nota metodologica) — caso contrario, cancele e detalhe todas as fichas.',
  ].join(' ');

  const decisao = {
    hookSpecificOutput: {
      permissionDecision: 'ask',
      permissionDecisionReason: razao,
    },
  };

  process.stdout.write(JSON.stringify(decisao) + '\n');
  process.exit(0);
} catch (_) {
  // Falha silenciosa: nunca bloquear por erro do proprio hook.
  process.exit(0);
}
