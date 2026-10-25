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

# Max tid för Aider (inklusive --test-cmd). Vid tidsgräns avslutas processen och skriptet kör egna tester.
$AiderTimeoutMin = 60

$ErrorActionPreference = "Stop"
$loggMapp = Join-Path $env:LOCALAPPDATA "appfabriken\logg"
New-Item -ItemType Directory -Force -Path $loggMapp | Out-Null
$senasteFelFil = Join-Path (Split-Path -Parent $loggMapp) "senaste-fel.json"
$stampel = Get-Date -Format "yyyyMMdd-HHmmss"
Start-Transcript -Path (Join-Path $loggMapp "kor-$stampel.log") | Out-Null
try { if (Test-Path $senasteFelFil) { Remove-Item -Force $senasteFelFil } } catch { }

function Skriv-Felsignal([int]$kod, [string]$kalla, [string]$text) {
    try {
        @{ kod = $kod; kalla = $kalla; text = $text; tid = (Get-Date).ToString("s") } |
            ConvertTo-Json | Set-Content -Encoding UTF8 $script:senasteFelFil
    } catch { }
}

function Avbryt([string]$text, [string]$kalla = "okänd") {
    Write-Host "AVBRYTER [$kalla]: $text" -ForegroundColor Red
    Skriv-Felsignal 1 $kalla $text
    Status "fel" "$kalla`: $text"
    RensaAktiv
    Stop-Transcript | Out-Null
    exit 1
}

# Läget skrivs till status.json, som panelen (verktyg/panel.py) visar.
$statusFil = Join-Path (Split-Path -Parent $loggMapp) "status.json"
$aktivFil = Join-Path (Split-Path -Parent $loggMapp) "aktiv-uppgift.json"
$beroendeSparFil = Join-Path (Split-Path -Parent $loggMapp) "cursor-beroende-tid.json"
$aiderLogg = Join-Path $loggMapp "aider-senaste.log"
function RensaAktiv() {
    try { if (Test-Path $script:aktivFil) { Remove-Item -Force $script:aktivFil } } catch { }
}
function Status([string]$fas, [string]$text = "", [hashtable]$extra = @{}) {
    try {
        $obj = @{ fas = $fas; text = $text; uppgift = $script:namn; modell = $Modell; tid = (Get-Date).ToString("s") }
        foreach ($k in $extra.Keys) { $obj[$k] = $extra[$k] }
        $obj | ConvertTo-Json | Set-Content -Encoding UTF8 $statusFil
    } catch { }
}

# cursor:<namn> är klart när PR från cursor/<namn> är mergad, eller en mergad cursor/-PR nämner uppdraget.
function Test-CursorBeroende([string]$cursorNamn) {
    $ut = @{ Klar = $false; Forklaring = ""; OppenPr = $null }
    $exaktGren = "cursor/$cursorNamn"
    $uppdragFil = "uppdrag/$cursorNamn.md"
    $exakt = @()
    try { $exakt = @(gh pr list --head $exaktGren --state merged --json number,title,headRefName 2>$null | ConvertFrom-Json) } catch { }
    if ($exakt.Count -gt 0) {
        $ut.Klar = $true
        $ut.Forklaring = "Mergad PR #$($exakt[0].number) från gren $exaktGren."
        return $ut
    }
    $sok = @()
    try { $sok = @(gh pr list --state merged --search $cursorNamn --json number,headRefName,title,body --limit 50 2>$null | ConvertFrom-Json) } catch { }
    foreach ($pr in ($sok | Where-Object { $_.headRefName -like "cursor/*" })) {
        $blob = "$($pr.title)`n$($pr.body)"
        if ($blob -match [regex]::Escape($uppdragFil) -or $blob -match [regex]::Escape($cursorNamn)) {
            $ut.Klar = $true
            $ut.Forklaring = "Mergad PR #$($pr.number) från $($pr.headRefName) nämner $uppdragFil eller $cursorNamn."
            return $ut
        }
    }
    $oppna = @()
    try {
        $oppna += @(gh pr list --head $exaktGren --state open --json number,title,headRefName 2>$null | ConvertFrom-Json)
        $oppna += @(gh pr list --state open --search $cursorNamn --json number,title,headRefName --limit 20 2>$null |
            Where-Object { $_.headRefName -like "cursor/*" })
    } catch { }
    $oppna = $oppna | Sort-Object number -Unique
    if ($oppna.Count -gt 0) { $ut.OppenPr = $oppna[0] }
    $ut.Forklaring = "Ingen mergad PR från $exaktGren och ingen mergad cursor/-PR som nämner $uppdragFil eller $cursorNamn i titel eller beskrivning."
    if ($ut.OppenPr) {
        $p = $ut.OppenPr
        $ut.Forklaring += " Närmaste öppna PR: #$($p.number) ($($p.headRefName))."
    }
    return $ut
}

function Las-BeroendeSpar {
    if (-not (Test-Path $beroendeSparFil)) { return @{} }
    try {
        $r = Get-Content -Raw -Encoding UTF8 $beroendeSparFil | ConvertFrom-Json
        if ($r -is [hashtable]) { return $r }
        $h = @{}
        $r.PSObject.Properties | ForEach-Object { $h[$_.Name] = $_.Value }
        return $h
    } catch { return @{} }
}

trap {
    $rad = $_.InvocationInfo.ScriptLineNumber
    $msg = $_.Exception.Message
    if ($rad) { $msg = "$msg (rad $rad)" }
    Write-Host "OVÄNTAT FEL [skript]: $msg" -ForegroundColor Red
    Skriv-Felsignal 1 "skript" $msg
    try { Status "fel" "skript: $msg" } catch { }
    RensaAktiv
    Stop-Transcript | Out-Null
    exit 1
}

function Kor([string]$fil, [string[]]$argument, [string]$kalla = "test") {
    & $fil @argument
    if ($LASTEXITCODE -ne 0) { Avbryt "$fil $($argument -join ' ') gav felkod $LASTEXITCODE" $kalla }
}

function KorGit([string[]]$argument) {
    # PowerShell 5.1 gör varje rad som git skriver på stderr (till exempel "Already on 'main'")
    # till ett avslutande fel när $ErrorActionPreference är Stop. Här räknas bara felkoden.
    $ErrorActionPreference = "Continue"
    $ut = & git @argument 2>&1
    $kod = $LASTEXITCODE
    if ($ut) { $ut | ForEach-Object { Write-Host $_ } }
    if ($kod -ne 0) { Avbryt "git $($argument -join ' ') gav felkod $kod" "git" }
}

function SkapaUppgiftsgren([string]$gren, [string]$uppgiftsNamn) {
    $ErrorActionPreference = "Continue"   # se KorGit: git skriver vanliga meddelanden på stderr
    $script:namn = $uppgiftsNamn
    git show-ref --verify --quiet "refs/heads/$gren" 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        $ejPaOrigin = @(git rev-list $gren --not --remotes=origin 2>$null)
        if ($ejPaOrigin.Count -gt 0) {
            $rescue = "rescue/$uppgiftsNamn-$stampel"
            Write-Host "Sparar kvarliggande gren $gren som $rescue ($($ejPaOrigin.Count) commit(s) som inte finns på origin)." -ForegroundColor Yellow
            $ut = & git branch $rescue $gren 2>&1
            $kod = $LASTEXITCODE
            if ($ut) { $ut | ForEach-Object { Write-Host $_ } }
            if ($kod -ne 0) {
                $msg = "Kunde inte spara $gren som $rescue."
                Skriv-Felsignal 2 "git" $msg
                Status "fastnad" $msg
                Stop-Transcript | Out-Null
                exit 2
            }
        }
    }
    $ut = & git checkout -B $gren origin/main 2>&1
    $kod = $LASTEXITCODE
    if ($ut) { $ut | ForEach-Object { Write-Host $_ } }
    if ($kod -ne 0) {
        $msg = "Kunde inte skapa gren $gren från origin/main (se loggen)."
        Skriv-Felsignal 2 "git" $msg
        Status "fastnad" $msg
        Stop-Transcript | Out-Null
        exit 2
    }
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

function Add-AiderLogLine([string]$rad) {
    foreach ($f in 1..3) {
        try {
            Add-Content -Encoding UTF8 -Path $script:aiderLogg -Value $rad -ErrorAction Stop
            return
        } catch {
            Start-Sleep -Milliseconds 100
        }
    }
}

function VarmaGradle {
    $gradlew = Join-Path $Repo "gradlew.bat"
    if (-not (Test-Path $gradlew)) { Avbryt "gradlew.bat saknas i $Repo" "Gradle" }
    Write-Host "Värmer upp Gradle (startar daemon om ingen lever)..." -ForegroundColor DarkGray
    $ErrorActionPreference = "Continue"
    & $gradlew help *> $null
    $kod = $LASTEXITCODE
    $status = (& $gradlew --status 2>&1 | Out-String)
    $ErrorActionPreference = "Stop"
    if ($kod -ne 0) { Avbryt "gradlew help gav felkod $kod" "Gradle" }
    if ($status -notmatch '(?m)^\s*PID\s' -and $status -notmatch 'IDLE|BUSY') {
        Avbryt "Ingen Gradle-daemon efter uppvärmning. gradlew --status:`n$status" "Gradle"
    }
}

function KorAider([string]$aiderExe, [string[]]$argument, [int]$timeoutMin) {
    $streamLog = Join-Path $loggMapp "aider-stream-$stampel.log"
    $errLog = Join-Path $loggMapp "aider-err-$stampel.log"
    "" | Set-Content -Encoding UTF8 $streamLog
    if (Test-Path $errLog) { Remove-Item -Force $errLog }

    $proc = Start-Process -FilePath $aiderExe -ArgumentList $argument -PassThru -NoNewWindow `
        -RedirectStandardOutput $streamLog -RedirectStandardError $errLog
    $deadline = (Get-Date).AddMinutes($timeoutMin)
    $timedOut = $false
    $visadeRader = 0

    function Visa-NyaAiderRader {
        param([string]$fil, [ref]$antal)
        if (-not (Test-Path $fil)) { return }
        $rader = @(Get-Content -LiteralPath $fil -Encoding UTF8 -ErrorAction SilentlyContinue)
        for ($i = $antal.Value; $i -lt $rader.Count; $i++) {
            Write-Host $rader[$i]
            Add-AiderLogLine $rader[$i]
        }
        $antal.Value = $rader.Count
    }

    while (-not $proc.HasExited) {
        if ((Get-Date) -gt $deadline) {
            $timedOut = $true
            $msg = "Aider avbröts efter tidsgränsen ($timeoutMin min)."
            Write-Host $msg -ForegroundColor Yellow
            Add-AiderLogLine $msg
            $ErrorActionPreference = "Continue"
            & taskkill /T /F /PID $proc.Id 2>&1 | Out-Null
            $ErrorActionPreference = "Stop"
            break
        }
        Visa-NyaAiderRader $streamLog ([ref]$visadeRader)
        Start-Sleep -Milliseconds 400
    }

    Visa-NyaAiderRader $streamLog ([ref]$visadeRader)
    if ((Test-Path $errLog) -and (Get-Item $errLog).Length -gt 0) {
        Get-Content -Encoding UTF8 $errLog | ForEach-Object {
            Write-Host $_ -ForegroundColor DarkYellow
            Add-AiderLogLine $_
        }
    }

    if (-not $timedOut) {
        $proc.WaitForExit() | Out-Null
        return $proc.ExitCode
    }
    return 124
}

# --- Förkontroller ---
$aider = Join-Path $env:USERPROFILE ".local\bin\aider.exe"
if (-not (Test-Path $aider)) { Avbryt "Aider saknas: $aider" "Aider" }
if (-not (Get-Command git -ErrorAction SilentlyContinue)) { Avbryt "git saknas" "git" }
if (-not $IngenPush -and -not (Get-Command gh -ErrorAction SilentlyContinue)) { Avbryt "gh saknas (winget install GitHub.cli, sedan gh auth login)" "push" }

$env:OLLAMA_API_BASE = "http://127.0.0.1:11434"
try { Invoke-RestMethod "$env:OLLAMA_API_BASE/api/tags" -TimeoutSec 10 | Out-Null }
catch { Avbryt "Ollama svarar inte på $env:OLLAMA_API_BASE" "Ollama" }

if (-not $env:JAVA_HOME) {
    $jbr = "C:\Program Files\Android\Android Studio\jbr"
    if (Test-Path $jbr) { $env:JAVA_HOME = $jbr } else { Avbryt "JAVA_HOME saknas och Android Studios JDK hittades inte" "Gradle" }
}

Set-Location $Repo
$localProps = Join-Path $Repo "local.properties"
if (-not (Test-Path $localProps)) {
    $sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk"
    if (-not (Test-Path $sdk)) { Avbryt "Android SDK saknas: $sdk" "Gradle" }
    "sdk.dir=" + ($sdk -replace '\\', '\\') | Set-Content -Encoding ASCII $localProps
}

# --- Utgå från senaste main ---
# En avbruten körning kan lämna en opushad lokal/-gren (t.ex. Aider hann committa). Grenen behålls;
# vid nästa start av samma uppgift sparas den som rescue/ innan en ny lokal/-gren skapas.
$aktuell = (git rev-parse --abbrev-ref HEAD).Trim()
if ($aktuell -like "lokal/*" -and -not (git ls-remote --heads origin $aktuell)) {
    Write-Host "Avbruten körning på $aktuell; grenen behålls lokalt (rescue vid nästa försök)." -ForegroundColor Yellow
    KorGit @("checkout", "main")
}
# Övriga ändringar (till exempel byggfiler som inte är ignorerade, eller en uppdaterad fil i verktyg/)
# läggs undan i git stash i stället för att körningen stannar. De går att hämta med "git stash list".
if (git status --porcelain) {
    Write-Host "Lägger undan ändringar som inte är committade (git stash)." -ForegroundColor Yellow
    KorGit @("stash", "push", "-u", "-m", "appfabriken $stampel")
}
KorGit @("checkout", "main")
KorGit @("fetch", "--prune", "origin")
# Lokal main ska alltid vara lika med origin/main. Har den egna commits (main på GitHub squash-mergas,
# så historiken går isär) sparas de på en gren och main sätts till origin/main.
$egna = (git rev-list --count origin/main..main).Trim()
if ($egna -ne "0") {
    $sparad = "sparad/lokal-main-$stampel"
    Write-Host "Lokal main hade $egna egna commits. Sparas på $sparad, main sätts till origin/main." -ForegroundColor Yellow
    KorGit @("branch", $sparad, "main")
}
KorGit @("reset", "--hard", "origin/main")

# Välj den första uppgiften som inte redan körts (gren finns) och vars beroenden är klara på main.
$uppgift = $null
$vaxtarGranskning = $false
$vaxtarBeroende = $false
$cursorVarning = $null
$blockDetaljer = @()
$beroendeSpar = Las-BeroendeSpar
$sparAndrad = $false
foreach ($kandidat in (Get-ChildItem "uppgifter\ny\*.md" -ErrorAction SilentlyContinue | Sort-Object Name)) {
    $g = "lokal/$($kandidat.BaseName)"
    if (git ls-remote --heads origin $g) {
        $vaxtarGranskning = $true
        Write-Host "Hoppar över $($kandidat.BaseName): grenen väntar på granskning."
        continue
    }
    $beror = (Get-Content -Encoding UTF8 $kandidat.FullName | Where-Object { $_ -match '^beror-på:' } | Select-Object -First 1) -replace '^beror-på:\s*', ''
    $saknade = @()
    $saknadeDetalj = @()
    if ($beror -and $beror -notmatch '^ingen') {
        foreach ($b in ($beror -split ',')) {
            $b = $b.Trim(); if (-not $b) { continue }
            if ($b -like "cursor:*") {
                $cursorNamn = $b.Substring(7).Trim()
                $st = Test-CursorBeroende $cursorNamn
                $nyckel = "$($kandidat.BaseName)|$b"
                if ($st.Klar) {
                    if ($beroendeSpar.ContainsKey($nyckel)) { $beroendeSpar.Remove($nyckel) | Out-Null; $sparAndrad = $true }
                    continue
                }
                $saknade += $b
                $vaxtarBeroende = $true
                $rad = "$b`: $($st.Forklaring)"
                $saknadeDetalj += $rad
                $nu = Get-Date
                if (-not $beroendeSpar.ContainsKey($nyckel)) { $beroendeSpar[$nyckel] = $nu.ToString("s"); $sparAndrad = $true }
                $start = [datetime]::Parse($beroendeSpar[$nyckel])
                if (($nu - $start).TotalMinutes -gt 30 -and -not $cursorVarning) {
                    $cursorVarning = "VARNING: $($kandidat.BaseName) har väntat på $b i över 30 minuter. $($st.Forklaring)"
                }
                continue
            }
            $fil = if ($b.EndsWith('.md')) { $b } else { "$b.md" }
            if (-not (Test-Path "uppgifter\klar\$fil")) {
                $saknade += $b
                $saknadeDetalj += "$b`: filen uppgifter/klar/$fil saknas."
            }
        }
    }
    if ($saknade) {
        Write-Host "Hoppar över $($kandidat.BaseName): väntar på $($saknade -join ', ')." -ForegroundColor Yellow
        foreach ($d in $saknadeDetalj) { Write-Host "  $d" -ForegroundColor DarkYellow; $blockDetaljer += "$($kandidat.BaseName): $d" }
        continue
    }
    $uppgift = $kandidat; break
}
if ($sparAndrad) {
    try { $beroendeSpar | ConvertTo-Json | Set-Content -Encoding UTF8 $beroendeSparFil } catch { }
}
if (-not $uppgift) {
    $antal = @(Get-ChildItem "uppgifter\ny\*.md" -ErrorAction SilentlyContinue).Count
    $extra = @{}
    if ($blockDetaljer) { $extra["beroende"] = ($blockDetaljer -join " | ") }
    if ($cursorVarning) {
        $extra["varning"] = $cursorVarning
        Write-Host $cursorVarning -ForegroundColor Red
    }
    if ($vaxtarBeroende) {
        $orsak = if ($antal -eq 0) { "Kön är tom men cursor-beroenden väntar." } else { "$antal uppgift(er) väntar på beroenden (cursor-kod eller andra uppgifter)." }
        Status "vilar" $orsak $extra
        RensaAktiv
        Write-Host $orsak
        if ($blockDetaljer) { Write-Host ("Beroenden: " + ($blockDetaljer -join " | ")) -ForegroundColor DarkYellow }
        Stop-Transcript | Out-Null
        exit 4
    }
    $orsak = if ($antal -eq 0) { "Kön är tom. Väntar på att Cursor delar upp nästa uppdrag." } else { "$antal uppgifter väntar på granskning eller på andra uppgifter." }
    Status "vilar" $orsak $extra
    RensaAktiv
    Write-Host $orsak
    Stop-Transcript | Out-Null
    exit 3
}

$namn = $uppgift.BaseName
$script:namn = $namn
$gren = "lokal/$namn"

Write-Host "Uppgift: $namn" -ForegroundColor Cyan
$rader = Get-Content -Encoding UTF8 $uppgift.FullName
$tillatna = Filer (Sektion $rader "Ändra bara dessa filer")
$forebild = Filer (Sektion $rader "Läs som förebild")
# Kommandot får stå i backticks (`gradle ...`), som i MALL.md. Backticks tas bort före matchningen.
$testRad = (Sektion $rader "Test som ska gå igenom" | ForEach-Object { ($_ -replace '`', '').Trim() } | Where-Object { $_ -match '^gradle\s' } | Select-Object -First 1)
if (-not $tillatna) { Avbryt "Uppgiften saknar filer under 'Ändra bara dessa filer'" "uppgift" }
if (-not $testRad) { Avbryt "Uppgiften saknar ett gradle-kommando under 'Test som ska gå igenom'" "uppgift" }
$testCmd = ($testRad.Trim() -replace '^gradle\s', 'call gradlew.bat ')
# Testkommandot läggs i en .cmd-fil, eftersom PowerShell 5.1 förstör citattecken i argument till andra program.
$testFil = Join-Path $loggMapp "test-$stampel.cmd"
"@echo off`r`ncd /d `"$Repo`"`r`n$testCmd`r`n" | Set-Content -Encoding ASCII $testFil

VarmaGradle

# --- Gren och flytt till pagar ---
SkapaUppgiftsgren $gren $namn
$iPagar = "uppgifter/pagar/$($uppgift.Name)"
KorGit @("mv", "uppgifter/ny/$($uppgift.Name)", $iPagar)
KorGit @("commit", "-m", "Uppgift startad: $namn")

try {
    @{
        namn = $namn
        gren = $gren
        pagar = $iPagar
        modell = $Modell
        testFil = $testFil
        start = (Get-Date).ToString("s")
    } | ConvertTo-Json | Set-Content -Encoding UTF8 $aktivFil
} catch { }

# --- Aider ---
# Meddelandet är uppgiften plus en regel för frågor (ARBETSFLODE.md).
$meddelande = Join-Path $loggMapp "meddelande-$stampel.md"
$fragaRegel = @"


---
Om uppgiften är motsägelsefull, eller pekar på filer eller funktioner som inte finns: skriv ingen kod.
Skapa i stället filen FRAGA.md i repots rot med din fråga, kort och konkret, och sluta där.
Lägg alla filer på exakt de sökvägar som står i uppgiften.
Skriv hela innehållet i varje fil under "Ändra bara dessa filer". En tom eller oförändrad fil räknas som misslyckad.
"@
((Get-Content -Raw -Encoding UTF8 $iPagar) + $fragaRegel) | Set-Content -Encoding UTF8 $meddelande
$aiderArg = @(
    "--model", $Modell,
    "--message-file", $meddelande,
    "--yes-always", "--no-pretty", "--no-check-update", "--no-show-release-notes",
    "--analytics-disable", "--no-gitignore", "--map-tokens", "0",
    "--auto-test", "--test-cmd", $testFil
)
if ($EditFormat) { $aiderArg += @("--edit-format", $EditFormat) }
foreach ($f in $forebild) { $aiderArg += @("--read", $f) }
$aiderArg += $tillatna

$start = Get-Date
Status "kodar" "Aider och $Modell arbetar."
"" | Set-Content -Encoding UTF8 $aiderLogg
# Aiders utskrift visas i fönstret och sparas samtidigt för panelen.
# Utskriften går genom en pipe, och då använder Python Windows teckentabell (cp1252). Den saknar
# tecken som Aider skriver (till exempel █ i förloppsindikatorn) och Aider kraschar. Tvinga UTF-8
# i Python och låt PowerShell läsa pipen som UTF-8, så att å, ä och ö också blir rätt.
$env:PYTHONUTF8 = "1"
$env:PYTHONIOENCODING = "utf-8"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
$aiderKod = KorAider $aider $aiderArg $AiderTimeoutMin

# --- Andra varvet: filer som uppgiften kräver men som är tomma eller oförändrade ---
# Den lokala modellen lämnar ibland nya filer tomma. Utan detta varv går uppgiften hela vägen
# via PR, CI och Cursors granskning innan försök 2 startar; med det rättas det direkt.
function OtillrackligaFiler([string[]]$filer) {
    $ut = @()
    foreach ($f in $filer) {
        if (-not (Test-Path -LiteralPath $f)) { $ut += $f; continue }
        $text = Get-Content -Raw -Encoding UTF8 -LiteralPath $f -ErrorAction SilentlyContinue
        if (-not $text -or $text.Trim().Length -lt 20) { $ut += $f; continue }
        $sparad = git ls-files -- $f
        if ($sparad) {
            git diff --quiet main -- $f
            if ($LASTEXITCODE -eq 0) { $ut += $f }
        }
    }
    return ,$ut
}
$andraVarv = "nej"
$tunna = OtillrackligaFiler $tillatna
if ($tunna.Count -gt 0 -and -not (Test-Path "FRAGA.md") -and $aiderKod -ne 124) {
    $andraVarv = "ja ($($tunna -join ', '))"
    Write-Host "Andra varvet: tomma eller oförändrade filer: $($tunna -join ', ')" -ForegroundColor Yellow
    Status "kodar" "Andra varvet: $($tunna.Count) fil(er) tomma eller oförändrade."
    $lista = ($tunna | ForEach-Object { "- $_" }) -join "`n"
    $varv2 = Join-Path $loggMapp "meddelande-$stampel-varv2.md"
    $text2 = "Du är inte klar. Dessa filer är tomma eller oförändrade, men uppgiften kräver dem:`n$lista`n`n" +
        "Skriv hela innehållet i varje fil nu, enligt uppgiften nedan. Ändra inga andra filer. " +
        "Svara inte med en förklaring, gör ändringen.`n`n---`n`n" + (Get-Content -Raw -Encoding UTF8 $meddelande)
    $text2 | Set-Content -Encoding UTF8 $varv2
    $arg2 = $aiderArg.Clone()
    $arg2[[array]::IndexOf($aiderArg, "--message-file") + 1] = $varv2
    $aiderKod = KorAider $aider $arg2 $AiderTimeoutMin
}
$minuter = [math]::Round(((Get-Date) - $start).TotalMinutes, 1)

# --- Egen kontroll efteråt ---
Status "testar" "Kör testerna."
& $testFil
$testOk = ($LASTEXITCODE -eq 0)

# Aider committar normalt själv. Ligger ändringar i de tillåtna filerna kvar okommittade, committas de här.
$kvar = git status --porcelain -- $tillatna
if ($kvar) {
    KorGit (@("add", "--") + $tillatna)
    KorGit @("commit", "-m", "Lokal modell: $namn (okommittade ändringar efter Aider)")
}

$harFraga = (Test-Path "FRAGA.md") -and ((Get-Content -Raw "FRAGA.md") -match "\S")
if ($harFraga) {
    KorGit @("add", "FRAGA.md")
    if (git status --porcelain -- FRAGA.md) { KorGit @("commit", "-m", "Lokal modell: fråga om $namn") }
    Write-Host "Den lokala modellen har en fråga (FRAGA.md)." -ForegroundColor Yellow
    $testOk = $false
}

$andrade = git diff --name-only "main...HEAD" | Where-Object { $_ -notlike "uppgifter/*" -and $_ -ne "FRAGA.md" }
$saknas = if ($harFraga) { @() } else { $tillatna | Where-Object { $andrade -notcontains $_ } }
if ($saknas) { $testOk = $false; Write-Host "Filer som uppgiften kräver men som inte ändrats: $($saknas -join ', ')" -ForegroundColor Yellow }
$utanfor = $andrade | Where-Object { $tillatna -notcontains $_ }

$resultat = if ($testOk) { "gröna" } else { "RÖDA" }
Write-Host "Tid: $minuter min. Tester: $resultat. Aider: $aiderKod." -ForegroundColor Cyan
if ($utanfor) { Write-Host "Filer utanför uppgiften: $($utanfor -join ', ')" -ForegroundColor Yellow }

if ($IngenPush) {
    Write-Host "Ingen push (-IngenPush). Grenen $gren finns bara lokalt."
    KorGit @("checkout", "main")
    RensaAktiv
    Stop-Transcript | Out-Null
    exit 0
}

# --- Push och PR ---
Status "pushar" "Öppnar PR."
KorGit @("push", "-u", "origin", $gren)
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
- Andra varvet: $andraVarv

Granska mot uppgiften och CLAUDE.md punkt 8.
"@
# Texten går via fil, eftersom PowerShell 5.1 tar bort citattecken i argument till andra program.
$kroppFil = Join-Path $loggMapp "pr-$stampel.md"
$kropp | Set-Content -Encoding UTF8 $kroppFil
$prArg = @("pr", "create", "--base", "main", "--head", $gren, "--title", $(if ($harFraga) { "Fråga: $namn" } else { "Lokal: $namn" }), "--body-file", $kroppFil)
if (-not $testOk -or $utanfor -or $harFraga) { $prArg += "--draft" }
& gh @prArg
if ($LASTEXITCODE -ne 0) {
    # gh kan ge felkod fast PR:en skapades. Det räcker att den finns.
    $finns = gh pr list --head $gren --state open --json number --jq ".[0].number"
    if (-not $finns) { Avbryt "PR för $gren kunde inte skapas." "push" }
    Write-Host "PR #$finns finns." -ForegroundColor Yellow
}

KorGit @("checkout", "main")
Write-Host "Klart." -ForegroundColor Green
Status "klar" "PR öppnad för $namn. Tester: $resultat."
RensaAktiv
Stop-Transcript | Out-Null
