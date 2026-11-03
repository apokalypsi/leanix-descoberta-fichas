#!/usr/bin/env node
/*
 * Hook SessionStart — bootstrap dos templates por tipo do LeanIX (cross-platform).
 *
 * Verifica se os templates TPL-NN-<Tipo>.md ja foram baixados do tenant em
 * .kiro/skills/leanix-descoberta-fichas/references/templates-por-tipo/.
 * Se a pasta existir mas NAO houver nenhum TPL-NN (estado de bootstrap), imprime
 * um lembrete em stdout para o agente sugerir conectar o MCP leanix e gerar a
 * estrutura do meta-modelo. Caso contrario, nao imprime nada (fica silencioso).
 *
 * Roda identico em Windows, macOS e Linux (so depende do Node, presente no Kiro).
 */
'use strict';

const fs = require('fs');
const path = require('path');

// O hook roda a partir da raiz do workspace (cwd). Monta o caminho de forma portavel.
const dir = path.join(
  process.cwd(),
  '.kiro',
  'skills',
  'leanix-descoberta-fichas',
  'references',
  'templates-por-tipo'
);

try {
  if (!fs.existsSync(dir)) {
    // Sem a pasta de referencia: nada a sugerir aqui.
    process.exit(0);
  }

  const temTpl = fs
    .readdirSync(dir)
    .some((nome) => /^TPL-.*\.md$/i.test(nome));

  if (temTpl) {
    // Templates ja gerados: hook silencioso.
    process.exit(0);
  }

  const msg = [
    'BOOTSTRAP PENDENTE DO LEANIX: a pasta',
    '.kiro/skills/leanix-descoberta-fichas/references/templates-por-tipo/ ainda esta em',
    'estado de bootstrap (so _MODELO-DO-TEMPLATE.md + _INDICE.md, sem os TPL-NN). Os',
    'templates do meta-modelo ainda NAO foram baixados do tenant.',
    '',
    'LOGO NO INICIO DESTA SESSAO, antes de montar qualquer ficha, sugira ao usuario (em',
    'PT-BR, de forma breve): conectar o MCP leanix (token + VPN) e gerar os templates por',
    'tipo do meta-modelo ao vivo (Passo 0 / Bootstrap da skill leanix-descoberta-fichas:',
    'ler _MODELO-DO-TEMPLATE.md, puxar get_fact_sheet_types + get_workspace_context e gerar',
    'um TPL-NN-<Tipo>.md por tipo, preenchendo a data real da extracao e regerando o',
    '_INDICE.md). Sem MCP/VPN, avise que nao da para gerar os templates (nao inventar',
    'campos) e que so a classificacao de tipo e a categorizacao TBM funcionam. Pergunte se',
    'pode gerar agora.',
  ].join(' ');

  process.stdout.write(msg + '\n');
  process.exit(0);
} catch (err) {
  // Falha silenciosa: nunca bloquear a sessao por causa deste check.
  process.exit(0);
}
