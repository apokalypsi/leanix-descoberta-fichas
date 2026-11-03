#!/usr/bin/env node
/*
 * Hook PostFileSave — guarda do FECHAMENTO DE RELACOES das fichas LeanIX (cross-platform).
 *
 * Steering de referencia: `fichas-leanix-completas` (gate, passo 6): toda ponta citada numa
 * relacao tem de existir como ficha. O caso mais comum de falha e o Provider: a relacao
 * `relITComponentToProvider` e OBRIGATORIA (1:*), entao TODO Provider citado nas fichas de
 * ITComponent precisa de uma ficha em leanix-providers.md.
 *
 * Como funciona: roda DEPOIS de salvar um arquivo. Recebe no stdin o JSON do evento com o
 * caminho do arquivo salvo. So age quando o arquivo e um leanix-*.md ou fichas-leanix.md sob
 * documentos-gerados/. Entao, na PASTA do alvo (ja gravada em disco), cruza:
 *   - provedores citados em leanix-itcomponents.md (coluna Alvo de `relITComponentToProvider`)
 *   - nomes das fichas em leanix-providers.md (`## Ficha - <Nome> (Provider)`)
 * Se houver provedor citado SEM ficha correspondente, imprime um aviso em stdout (nao bloqueia;
 * PostFileSave e informativo). Normaliza nomes para casar "AWS / PostgreSQL" com "Amazon Web
 * Services" via apelidos conhecidos; o que nao casar e reportado para revisao humana.
 *
 * NUNCA falha a sessao: qualquer excecao termina em exit 0.
 * Roda identico em Windows, macOS e Linux (so depende do Node, presente no Kiro).
 */
'use strict';

const fs = require('fs');
const path = require('path');

function lerStdin() {
  try {
    return fs.readFileSync(0, 'utf8');
  } catch (_) {
    return '';
  }
}

function caminhoDoEvento(payload) {
  const a = payload.arguments || payload.args || payload.input || payload || {};
  return (
    payload.path || payload.filePath || payload.file ||
    a.path || a.targetFile || a.file || ''
  );
}

function ehFichaLeanix(p) {
  if (!p) return false;
  const norm = String(p).replace(/\\/g, '/').toLowerCase();
  if (!norm.includes('documentos-gerados/')) return false;
  const base = norm.split('/').pop() || '';
  return /^leanix-.*\.md$/.test(base) || base === 'fichas-leanix.md';
}

// Normaliza um nome de provedor para comparar citacao x ficha.
// Minusculas, sem acento, so o "nucleo" relevante.
function normalizar(nome) {
  let s = String(nome).toLowerCase().trim();
  s = s.normalize('NFD').replace(/[\u0300-\u036f]/g, ''); // tira acentos
  s = s.replace(/\(.*?\)/g, ' '); // remove parenteses "(open source)" etc.
  s = s.replace(/\b(a confirmar|open source|issuer|oauth2)\b/g, ' ');
  s = s.replace(/[^a-z0-9/ ]+/g, ' ').replace(/\s+/g, ' ').trim();
  return s;
}

// Apelidos conhecidos: citacao -> nome canonico de ficha.
const APELIDOS = [
  { re: /\baws\b|amazon web services/, canonico: 'amazon web services' },
  { re: /postgresql global dev group|postgresql gdg/, canonico: 'postgresql' },
];

function canonizar(nome) {
  const n = normalizar(nome);
  for (const a of APELIDOS) {
    if (a.re.test(n)) return a.canonico;
  }
  return n;
}

// Extrai os provedores citados na coluna Alvo de `relITComponentToProvider`.
// Uma citacao "AWS (a confirmar) / PostgreSQL" vira dois provedores: aws, postgresql.
function provedoresCitados(textoItComponents) {
  const set = new Set();
  const re = /relITComponentToProvider`?\s*\|\s*Provider\s*\|([^|]+)\|/g;
  let m;
  while ((m = re.exec(textoItComponents)) !== null) {
    const alvo = m[1];
    for (const parte of alvo.split('/')) {
      const canon = canonizar(parte);
      if (canon && canon.length > 1) set.add(canon);
    }
  }
  return set;
}

// Extrai os nomes das fichas de Provider (cabecalhos "## Ficha ... <Nome> (Provider)").
function fichasDeProvider(textoProviders) {
  const set = new Set();
  const re = /^##\s*Ficha.*?[-—]\s*(.+?)\s*\(Provider\)/gim;
  let m;
  while ((m = re.exec(textoProviders)) !== null) {
    set.add(canonizar(m[1]));
  }
  return set;
}

// Verifica se um provedor citado casa com alguma ficha (match por inclusao de nucleo).
function temFicha(citado, fichas) {
  for (const f of fichas) {
    if (f === citado || f.includes(citado) || citado.includes(f)) return true;
  }
  return false;
}

try {
  const raw = lerStdin();
  let payload = {};
  if (raw.trim()) {
    try { payload = JSON.parse(raw); } catch (_) { payload = {}; }
  }

  const arquivoSalvo = caminhoDoEvento(payload);
  if (!ehFichaLeanix(arquivoSalvo)) process.exit(0);

  const pasta = path.dirname(String(arquivoSalvo).replace(/\\/g, '/'));
  const fItc = path.join(pasta, 'leanix-itcomponents.md');
  const fPrv = path.join(pasta, 'leanix-providers.md');

  // So da para cruzar quando os dois arquivos ja existem na pasta.
  if (!fs.existsSync(fItc) || !fs.existsSync(fPrv)) process.exit(0);

  const textoItc = fs.readFileSync(fItc, 'utf8');
  const textoPrv = fs.readFileSync(fPrv, 'utf8');

  const citados = provedoresCitados(textoItc);
  const fichas = fichasDeProvider(textoPrv);

  const orfaos = [];
  for (const c of citados) {
    if (!temFicha(c, fichas)) orfaos.push(c);
  }

  if (orfaos.length === 0) process.exit(0); // grafo de Provider fechado

  const msg = [
    'AVISO — FECHAMENTO DE RELACOES (steering fichas-leanix-completas, passo 6):',
    `em ${pasta}, ha Provider(es) citado(s) em leanix-itcomponents.md SEM ficha em`,
    'leanix-providers.md (relITComponentToProvider e obrigatoria, 1:*). Provedores sem ficha:',
    orfaos.join(', ') + '.',
    'Gere uma ficha de Provider para cada um (ou marque como lacuna/externa com reuso por ID',
    'a confirmar via MCP) antes de considerar o conjunto pronto.',
  ].join(' ');

  process.stdout.write(msg + '\n');
  process.exit(0);
} catch (_) {
  process.exit(0);
}
