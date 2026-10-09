# Kör den äldsta uppgiften i uppgifter/ny/ med den lokala modellen (Aider + Ollama).
# Se CLAUDE.md punkt 3 och uppgifter/MALL.md.
#
# Start (manuellt):
#   powershell -ExecutionPolicy Bypass -File verktyg\kor-uppgift.ps1
# Valfritt:
#   -Modell ollama_chat/qwen3-coder:30b   (vilken Ollama-modell Aider använder)
#   -EditFormat diff                      (whole, diff, udiff; tomt = Aiders standard för modellen)
#   -IngenPush                            (kör lokalt men pusha inte och öppna ingen PR)
#
# Kräver: git, gh (inloggad), Aider i %USERPROFILE%\.local\bin, Ollama igång, JDK 17+ och Android SDK.
# Skriptet ändrar bara repot och grenen lokal/<uppgift>. Det rör aldrig main på GitHub.

param(
    [string]$Repo = (Split-Path -Parent $PSScriptRoot),
    [string]$Modell = "ollama_chat/qwen3-coder:30b",
    [string]$EditFormat = "",
    [switch]$IngenPush
)

$ErrorActionPreference = "Stop"
$loggMapp = Join-Path $env:LOCALAPPDATA "appfabriken\logg"
New-Item -ItemType Directory -Force -Path $loggMapp | Out-Null
$stampel = Get-Date -Format "yyyyMMdd-HHmmss"
Start-Transcript -Path (Join-Path $loggMapp "kor-$stampel.log") | Out-Null

function Avbryt([string]$text) {
    Write-Host "AVBRYTER: $text" -ForegroundColor Red
    Stop-Transcript | Out-Null
    exit 1
}

function Kor([string]$fil, [string[]]$argument) {
    & $fil @argument
    if ($LASTEXITCODE -ne 0) { Avbryt "$fil $($argument -join ' ') gav felkod $LASTEXITCODE" }
}

function Sektion([string[]]$rader, [string]$rubrik) {
    $ut = @(); $inne = $false
    foreach ($r in $rader) {
        if ($r -match '^##\s+') { $inne = ($r -match ('^##\s+' + [regex]::Escape($rubrik))); continue }
        if ($inne) { $ut += $r }
    }
    return $ut
}

function Filer([string[]]$rader) {
    $ut = @()
    foreach ($r in $rader) { if ($r -match '^\s*-\s+(\S+)') { $ut += $Matches[1] } }
    return $ut
}

# --- Förkontroller ---
$aider = Join-Path $env:USERPROFILE ".local\bin\aider.exe"
if (-not (Test-Path $aider)) { Avbryt "Aider saknas: $aider" }
if (-not (Get-Command git -ErrorAction SilentlyContinue)) { Avbryt "git saknas" }
if (-not $IngenPush -and -not (Get-Command gh -ErrorAction SilentlyContinue)) { Avbryt "gh saknas (winget install GitHub.cli, sedan gh auth login)" }

$env:OLLAMA_API_BASE = "http://127.0.0.1:11434"
try { Invoke-RestMethod "$env:OLLAMA_API_BASE/api/tags" -TimeoutSec 10 | Out-Null }
catch { Avbryt "Ollama svarar inte på $env:OLLAMA_API_BASE" }

if (-not $env:JAVA_HOME) {
    $jbr = "C:\Program Files\Android\Android Studio\jbr"
    if (Test-Path $jbr) { $env:JAVA_HOME = $jbr } else { Avbryt "JAVA_HOME saknas och Android Studios JDK hittades inte" }
}

Set-Location $Repo
$localProps = Join-Path $Repo "local.properties"
if (-not (Test-Path $localProps)) {
    $sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk"
    if (-not (Test-Path $sdk)) { Avbryt "Android SDK saknas: $sdk" }
    "sdk.dir=" + ($sdk -replace '\\', '\\') | Set-Content -Encoding ASCII $localProps
}

# --- Utgå från senaste main ---
# En körning som Windows stoppade mitt i (datorn användes igen) lämnar en opushad lokal/-gren
# med halvfärdiga ändringar. Den kastas, och uppgiften körs om från början nästa gång.
$aktuell = (git rev-parse --abbrev-ref HEAD).Trim()
if ($aktuell -like "lokal/*" -and -not (git ls-remote --heads origin $aktuell)) {
    Write-Host "Avbruten körning på $aktuell kastas." -ForegroundColor Yellow
    Kor git @("reset", "-q", "--hard")
    Kor git @("clean", "-q", "-fd")
    Kor git @("checkout", "-q", "main")
    Kor git @("branch", "-q", "-D", $aktuell)
}
if (git status --porcelain) { Avbryt "Repot har ändringar som inte är committade. Rensa först." }
Kor git @("checkout", "-q", "main")
Kor git @("pull", "-q", "--ff-only")

# Välj den första uppgiften som inte redan körts (gren finns) och vars beroenden är klara på main.
$uppgift = $null
foreach ($kandidat in (Get-ChildItem "uppgifter\ny\*.md" -ErrorAction SilentlyContinue | Sort-Object Name)) {
    $g = "lokal/$($kandidat.BaseName)"
    if (git ls-remote --heads origin $g) { Write-Host "Hoppar över $($kandidat.BaseName): grenen väntar på granskning."; continue }
    $beror = (Get-Content -Encoding UTF8 $kandidat.FullName | Where-Object { $_ -match '^beror-på:' } | Select-Object -First 1) -replace '^beror-på:\s*', ''
    $saknade = @()
    if ($beror -and $beror -notmatch '^ingen') {
        foreach ($b in ($beror -split ',')) {
            $b = $b.Trim(); if (-not $b) { continue }
            if (-not $b.EndsWith('.md')) { $b = "$b.md" }
            if (-not (Test-Path "uppgifter\klar\$b")) { $saknade += $b }
        }
    }
    if ($saknade) { Write-Host "Hoppar över $($kandidat.BaseName): väntar på $($saknade -join ', ')."; continue }
    $uppgift = $kandidat; break
}
if (-not $uppgift) { Write-Host "Ingen uppgift att köra just nu."; Stop-Transcript | Out-Null; exit 3 }

$namn = $uppgift.BaseName
$gren = "lokal/$namn"

Write-Host "Uppgift: $namn" -ForegroundColor Cyan
$rader = Get-Content -Encoding UTF8 $uppgift.FullName
$tillatna = Filer (Sektion $rader "Ändra bara dessa filer")
$forebild = Filer (Sektion $rader "Läs som förebild")
$testRad = (Sektion $rader "Test som ska gå igenom" | Where-Object { $_ -match '^\s*gradle\s' } | Select-Object -First 1)
if (-not $tillatna) { Avbryt "Uppgiften saknar filer under 'Ändra bara dessa filer'" }
if (-not $testRad) { Avbryt "Uppgiften saknar ett gradle-kommando under 'Test som ska gå igenom'" }
$testCmd = ($testRad.Trim() -replace '^gradle\s', 'call gradlew.bat ')
# Testkommandot läggs i en .cmd-fil, eftersom PowerShell 5.1 förstör citattecken i argument till andra program.
$testFil = Join-Path $loggMapp "test-$stampel.cmd"
"@echo off`r`ncd /d `"$Repo`"`r`n$testCmd`r`n" | Set-Content -Encoding ASCII $testFil

# --- Gren och flytt till pagar ---
Kor git @("checkout", "-q", "-b", $gren)
$iPagar = "uppgifter/pagar/$($uppgift.Name)"
Kor git @("mv", "uppgifter/ny/$($uppgift.Name)", $iPagar)
Kor git @("commit", "-q", "-m", "Uppgift startad: $namn")

# --- Aider ---
$aiderArg = @(
    "--model", $Modell,
    "--message-file", $iPagar,
    "--yes-always", "--no-pretty", "--no-check-update", "--no-show-release-notes",
    "--analytics-disable", "--no-gitignore", "--map-tokens", "0",
    "--auto-test", "--test-cmd", $testFil
)
if ($EditFormat) { $aiderArg += @("--edit-format", $EditFormat) }
foreach ($f in $forebild) { $aiderArg += @("--read", $f) }
$aiderArg += $tillatna

$start = Get-Date
& $aider @aiderArg
$aiderKod = $LASTEXITCODE
$minuter = [math]::Round(((Get-Date) - $start).TotalMinutes, 1)

# --- Egen kontroll efteråt ---
& $testFil
$testOk = ($LASTEXITCODE -eq 0)

# Aider committar normalt själv. Ligger ändringar i de tillåtna filerna kvar okommittade, committas de här.
$kvar = git status --porcelain -- $tillatna
if ($kvar) {
    Kor git (@("add", "--") + $tillatna)
    Kor git @("commit", "-q", "-m", "Lokal modell: $namn (okommittade ändringar efter Aider)")
}

$andrade = git diff --name-only "main...HEAD" | Where-Object { $_ -notlike "uppgifter/*" }
$saknas = $tillatna | Where-Object { $andrade -notcontains $_ }
if ($saknas) { $testOk = $false; Write-Host "Filer som uppgiften kräver men som inte ändrats: $($saknas -join ', ')" -ForegroundColor Yellow }
$utanfor = $andrade | Where-Object { $tillatna -notcontains $_ }

$resultat = if ($testOk) { "gröna" } else { "RÖDA" }
Write-Host "Tid: $minuter min. Tester: $resultat. Aider: $aiderKod." -ForegroundColor Cyan
if ($utanfor) { Write-Host "Filer utanför uppgiften: $($utanfor -join ', ')" -ForegroundColor Yellow }

if ($IngenPush) {
    Write-Host "Ingen push (-IngenPush). Grenen $gren finns bara lokalt."
    Kor git @("checkout", "-q", "main")
    Stop-Transcript | Out-Null
    exit 0
}

# --- Push och PR ---
Kor git @("push", "-q", "-u", "origin", $gren)
$utanforText = if ($utanfor) { $utanfor -join ", " } else { "inga" }
$saknasText = if ($saknas) { $saknas -join ", " } else { "inga" }
$format = if ($EditFormat) { $EditFormat } else { "standard" }
$kropp = @"
Lokal modell körde uppgiften ``$iPagar``.

- Modell: ``$Modell``, redigeringsformat: $format
- Tid: $minuter min
- Tester (``$testRad``): $resultat
- Filer utanför uppgiften: $utanforText
- Filer som saknas: $saknasText

Granska mot uppgiften och CLAUDE.md punkt 8.
"@
# Texten går via fil, eftersom PowerShell 5.1 tar bort citattecken i argument till andra program.
$kroppFil = Join-Path $loggMapp "pr-$stampel.md"
$kropp | Set-Content -Encoding UTF8 $kroppFil
$prArg = @("pr", "create", "--base", "main", "--head", $gren, "--title", "Lokal: $namn", "--body-file", $kroppFil)
if (-not $testOk -or $utanfor) { $prArg += "--draft" }
Kor gh $prArg

Kor git @("checkout", "-q", "main")
Write-Host "Klart." -ForegroundColor Green
Stop-Transcript | Out-Null
