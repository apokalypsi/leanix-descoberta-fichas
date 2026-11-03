---
inclusion: always
---

# Encoding: sempre UTF-8

Regra permanente do projeto: **todo arquivo é lido e escrito em UTF-8** (sem BOM), e todo
texto acentuado em português (ç, ã, õ, á, é, í, ó, ú, â, ê) deve sair íntegro — nunca
corrompido como `Ã§`, `Ã£`, `Ã©` etc.

## Ao criar ou editar arquivos
- Gere o conteúdo em UTF-8. Isso vale para `.md`, `.html`, `.dsl`, `.json`, `.py`, `.java`,
  `.yaml`, `.properties` e qualquer ficha/relatório gerado pelas skills (ex.: as fichas de
  descoberta LeanIX e seus diagramas Mermaid).
- Não insira BOM. Use fim de linha `lf` para arquivos de texto comuns (o `.editorconfig`
  na raiz já fixa isso; scripts `.ps1`/`.bat`/`.cmd` ficam em `crlf`).

## PowerShell no Windows (causa comum de acento quebrado)
O PowerShell 5.1 **não grava UTF-8 sem BOM por padrão** — `Set-Content`, `Out-File` e
`Add-Content` gravam ANSI/UTF-16 (corrompem acentos) e, com `-Encoding utf8`, **adicionam BOM**.
Como a regra do projeto é **UTF-8 sem BOM**, não confie nesses cmdlets para gravar.

**Regra única — grave sempre com .NET, que controla o BOM:**

```powershell
# GRAVAR (UTF-8 SEM BOM — use SEMPRE isto para .md/.json/.html/.dsl/.yaml/.py/.java):
[System.IO.File]::WriteAllText($arquivo, $conteudo, [System.Text.UTF8Encoding]::new($false))

# LER:
$conteudo = [System.IO.File]::ReadAllText($arquivo, [System.Text.Encoding]::UTF8)
# (ou, com o cmdlet: Get-Content -Path $arquivo -Encoding utf8 -Raw)
```

> **Não use** `Set-Content -Encoding utf8` / `Out-File -Encoding utf8` nestes arquivos: no
> PS 5.1 eles inserem BOM, violando a regra. Se o conteúdo tem acento e você precisa de um
> cmdlet, confirme depois que não há BOM (ver "Verificação rápida").
> **Melhor ainda:** prefira as ferramentas de edição de arquivo do agente (elas preservam
> UTF-8 sem BOM) a reescrever arquivos de texto via shell.

## Python
- Abra arquivos de texto com `encoding="utf-8"` explícito em `open(...)` tanto na leitura
  quanto na escrita.

## Verificação rápida
Depois de gerar um arquivo com acentos, confirme (a) que não há BOM e (b) que não há mojibake.

```powershell
# (a) BOM? — os 3 primeiros bytes não podem ser EF BB BF
$b = [System.IO.File]::ReadAllBytes($arquivo)
$temBom = ($b.Length -ge 3 -and $b[0] -eq 0xEF -and $b[1] -eq 0xBB -and $b[2] -eq 0xBF)
if ($temBom) { "ERRO: arquivo tem BOM — regrave sem BOM" }

# (b) Mojibake? — procure as SEQUÊNCIAS típicas de UTF-8 relido como cp1252,
#     não acentos isolados (senão dá falso positivo em texto PT correto).
Select-String -Path $arquivo -Pattern 'Ã§|Ã£|Ã©|Ã¡|Ã³|Ãª|Ã­|Ãµ|Ã‡|â€“|â€”|â€™|â†’|â””|â”€' |
  Select-Object -First 5
```

Se aparecer BOM ou qualquer sequência acima (ex.: `Ã§`, `Ã£`, `â””`), o arquivo foi gravado no
encoding errado — regrave em UTF-8 sem BOM com o `WriteAllText` acima.

> **Atenção ao falso positivo:** não procure `Ã.` genérico nem `Â.` — letras acentuadas
> corretas em português (`ç`, `ã`, `é`) **não** são mojibake. Mojibake é a sequência de DOIS
> caracteres (`Ã` + outro) que aparece quando bytes UTF-8 são lidos como cp1252/Latin-1.
