# Kör uppgifter med kor-uppgift.ps1 i följd, utan att någonsin ge upp av sig själv.
# Övervakar barnprocessen (tillsyn): dödar hängda körningar, räddar commits, full återställning före varje körning.
#  - Tom uppgifter/ny/: väntar $TomKoMinuter minuter och tittar igen (exit 3). Loopen avslutas inte.
#  - Uppgift inte redo (beroenden m.m.): väntar 1 minut och försöker igen (exit 4).
#  - Ingen uppgift redo (granskning m.m.): väntar 3 minuter och försöker igen (exit 3).
#  - Fel eller avbrott: full återställning och ett omförsök; därefter stoppnotering + högst 5 min vila.
#  - Tidsgränsen -VantaTimmar räknas från senaste lyckade uppgift (eller start).
param(
    [int]$Max = 200,
    [double]$VantaTimmar = 8,
    [int]$HangMinuter = 15,
    [int]$MaxUppgiftMinuter = 90,
    [int]$TomKoMinuter = 10
)

$DefaultModell = "ollama_chat/qwen3-coder:30b"

$Repo = Split-Path -Parent $PSScriptRoot
$skript = Join-Path $PSScriptRoot "kor-uppgift.ps1"
$loggMapp = Join-Path $env:LOCALAPPDATA "appfabriken\logg"
New-Item -ItemType Directory -Force -Path $loggMapp | Out-Null
$korAllaLogg = Join-Path $loggMapp "kor-alla.log"
$statusFil = Join-Path $env:LOCALAPPDATA "appfabriken\status.json"
$aiderLogg = Join-Path $loggMapp "aider-senaste.log"
$aktivFil = Join-Path $env:LOCALAPPDATA "appfabriken\aktiv-uppgift.json"
$senasteFelFil = Join-Path $env:LOCALAPPDATA "appfabriken\senaste-fel.json"

$slut = (Get-Date).AddHours($VantaTimmar)
$korda = 0
$retriedEfterFel = $false
$tomKoSenast = $false
$script:aktivKorUppgiftPid = $null
$sistVakt = [datetime]::MinValue
$VaktMinuter = 30
$pollSek = 30
$VilaEfterStoppMin = 5
$script:korUppgiftStart = $null
$script:aiderBasTid = [datetime]::MinValue

function Logga([string]$text) {
    $rad = "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') $text"
    Write-Host $rad
    try { Add-Content -Encoding UTF8 -Path $korAllaLogg -Value $rad } catch { }
}

function Set-JavaHomeOmBehov {
    if ($env:JAVA_HOME) {
        $env:JAVA_HOME = ($env:JAVA_HOME -replace '^["'']|["'']$', '').Trim()
        if (-not (Test-Path $env:JAVA_HOME)) {
            Logga "JAVA_HOME pekar på ogiltig katalog ($($env:JAVA_HOME)) — försöker Android Studio jbr."
            $env:JAVA_HOME = $null
        }
    }
    if (-not $env:JAVA_HOME) {
        $jbr = "C:\Program Files\Android\Android Studio\jbr"
        if (Test-Path $jbr) {
            $env:JAVA_HOME = $jbr
            Logga "JAVA_HOME satt till Android Studio jbr."
        } else {
            Logga "JAVA_HOME saknas och Android Studios JDK hittades inte — gradlew --stop kan misslyckas."
        }
    }
}

function Stop-Gradle {
    $bat = Join-Path $Repo "gradlew.bat"
    if (-not (Test-Path $bat)) { return }
    Push-Location $Repo
    try {
        $ErrorActionPreference = "Continue"
        $ut = & $bat --stop 2>&1
        $kod = $LASTEXITCODE
        if ($ut) { $ut | ForEach-Object { Logga "gradlew --stop: $_" } }
        if ($kod -eq 0) {
            Logga "gradlew --stop lyckades."
        } else {
            Logga "gradlew --stop gav felkod $kod."
        }
    } catch {
        Logga "gradlew --stop misslyckades: $_"
    } finally {
        Pop-Location
    }
}

function Get-OllamaModellNamn([string]$modellFull) {
    if ($modellFull -match 'ollama_chat/(.+)$') { return $Matches[1] }
    if ($modellFull -match '/(.+)$') { return $Matches[1] }
    return $modellFull
}

function Stop-KvarvarandeKorProcesser {
    if ($script:aktivKorUppgiftPid) {
        try {
            $p = Get-Process -Id $script:aktivKorUppgiftPid -ErrorAction SilentlyContinue
            if ($p) { Stop-ProcessTree $p }
        } catch { }
        $script:aktivKorUppgiftPid = $null
    }
    $repoEsc = [regex]::Escape($Repo)
    $processer = Get-CimInstance Win32_Process -ErrorAction SilentlyContinue
    if (-not $processer) { return }
    foreach ($proc in $processer) {
        $namn = $proc.Name
        $cl = $proc.CommandLine
        if (-not $cl) { continue }
        if ($namn -eq 'aider.exe') {
            Logga "Full återställning: dödar aider (PID $($proc.ProcessId))."
            & taskkill /F /PID $proc.ProcessId 2>&1 | Out-Null
            continue
        }
        if ($namn -eq 'java.exe' -and ($cl -match 'GradleDaemon' -or $cl -match $repoEsc)) {
            Logga "Full återställning: dödar java/Gradle (PID $($proc.ProcessId))."
            & taskkill /F /PID $proc.ProcessId 2>&1 | Out-Null
        }
    }
}

function Stop-OllamaModell([string]$modellFull) {
    if (-not (Get-Command ollama -ErrorAction SilentlyContinue)) {
        Logga "ollama-kommando saknas — hoppar över ollama stop."
        return
    }
    $tag = Get-OllamaModellNamn $modellFull
    Logga "ollama stop $tag"
    & ollama stop $tag 2>&1 | ForEach-Object { Logga "ollama: $_" }
}

function Test-OllamaSvarar {
    try {
        Invoke-RestMethod "http://127.0.0.1:11434/api/tags" -TimeoutSec 8 | Out-Null
        return $true
    } catch {
        return $false
    }
}

function Start-OllamaOmBehov {
    if (Test-OllamaSvarar) { return $true }
    Logga "Ollama svarar inte — försöker starta om."
    Get-Process -Name "ollama*" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    $ollama = Get-Command ollama -ErrorAction SilentlyContinue
    if ($ollama) {
        Start-Process -FilePath $ollama.Source -ArgumentList "serve" -WindowStyle Hidden -ErrorAction SilentlyContinue | Out-Null
        for ($i = 0; $i -lt 15; $i++) {
            Start-Sleep -Seconds 2
            if (Test-OllamaSvarar) {
                Logga "Ollama svarar efter omstart."
                return $true
            }
        }
    }
    Logga "Ollama svarar fortfarande inte efter omstart."
    return $false
}

function Invoke-FullAtstallning([string]$modell = $DefaultModell) {
    Logga "Full återställning (Gradle, processer, Ollama)."
    Stop-KvarvarandeKorProcesser
    Stop-Gradle
    Stop-OllamaModell $modell
    Start-OllamaOmBehov | Out-Null
}

function Mask-LoggRad([string]$rad) {
    if ($null -eq $rad) { return "" }
    $hem = [regex]::Escape($env:USERPROFILE)
    $l = $rad -replace $hem, '~'
    $l = $l -replace '(?i)(ghp_[A-Za-z0-9]{20,}|gho_[A-Za-z0-9]{20,}|github_pat_[A-Za-z0-9_]{20,})', '[MASKERAD]'
    $l = $l -replace '\bsk-[A-Za-z0-9]{20,}\b', '[MASKERAD]'
    $l = $l -replace 'Bearer\s+[A-Za-z0-9_\-\.]+', 'Bearer [MASKERAD]'
    return $l
}

function Get-LoggSlut([string]$fil, [int]$antalRader = 40) {
    if (-not (Test-Path $fil)) { return @("(fil saknas: $(Split-Path -Leaf $fil))") }
    $all = @(Get-Content -Encoding UTF8 -Path $fil -ErrorAction SilentlyContinue)
    if ($all.Count -eq 0) { return @("(tom logg)") }
    $start = [Math]::Max(0, $all.Count - $antalRader)
    return @($all[$start..($all.Count - 1)] | ForEach-Object { Mask-LoggRad $_ })
}

function Get-SenasteKorTranscript {
    $filer = @(Get-ChildItem -Path $loggMapp -Filter "kor-*.log" -ErrorAction SilentlyContinue |
        Sort-Object LastWriteTime -Descending)
    if ($filer.Count -eq 0) { return $null }
    return $filer[0].FullName
}

function Get-Felbeskrivning([int]$kod, [hashtable]$ut) {
    $kalla = "okänd"
    $text = ""
    if (Test-Path $senasteFelFil) {
        try {
            $j = Get-Content -Raw -Encoding UTF8 $senasteFelFil | ConvertFrom-Json
            if ($j.kalla) { $kalla = [string]$j.kalla }
            if ($j.text) { $text = [string]$j.text }
        } catch { }
    }
    if ($ut.avbrott) {
        $kalla = "tillsyn"
        if ($ut.orsak) { $text = [string]$ut.orsak }
    } elseif ($kod -eq 2 -and $kalla -eq "okänd") {
        $kalla = "git"
        if (-not $text) { $text = "Uppgiften fastnade (t.ex. git-gren)." }
    } elseif ($kod -eq 4) {
        $kalla = "tillsyn"
    }
    return @{ kod = $kod; kalla = $kalla; text = $text }
}

function Format-FelRad([hashtable]$fel) {
    $del = "Felkod $($fel.kod), källa: $($fel.kalla)."
    if ($fel.text) { $del += " $($fel.text)" }
    return $del
}

function Vakta {
    if (((Get-Date) - $script:sistVakt).TotalMinutes -lt $VaktMinuter) { return }
    $script:sistVakt = Get-Date
    try {
        & gh api -X POST repos/mattehe-creator/appfabriken/actions/workflows/vaktmastare.yml/dispatches -f ref=main 2>&1 | Out-Null
        Logga "Vaktmästare (workflow_dispatch) anropad."
    } catch {
        Logga "Vaktmästare kunde inte anropas: $_"
    }
}

function KorGit([string[]]$argument) {
    $ErrorActionPreference = "Continue"
    Push-Location $Repo
    try {
        $ut = & git @argument 2>&1
        $kod = $LASTEXITCODE
        if ($ut) { $ut | ForEach-Object { Logga "git: $_" } }
        if ($kod -ne 0) { throw "git $($argument -join ' ') gav felkod $kod" }
    } finally {
        Pop-Location
    }
}

function Skriv-LokalStopp([string]$rad, [hashtable]$fel = $null) {
    if ($fel) {
        $rad = "$rad $(Format-FelRad $fel)"
    }
    $full = "$(Get-Date -Format s) $rad"
    Logga "Skriver lokal-stopp: $full"
    $korTranscript = Get-SenasteKorTranscript
    $loggInnehall = @(
        "# Senaste lokala stopp",
        "",
        $full,
        "",
        "## Fel",
        $(if ($fel) { Format-FelRad $fel } else { "(ingen felbeskrivning)" })
    )
    if ($korTranscript) {
        $loggInnehall += @(
            "",
            "## Sista rader: $(Split-Path -Leaf $korTranscript)",
            ""
        ) + (Get-LoggSlut $korTranscript)
    } else {
        $loggInnehall += @("", "## Sista rader: kor-uppgift transcript", "", "(ingen kor-*.log hittades)")
    }
    $loggInnehall += @(
        "",
        "## Sista rader: kor-alla.log",
        ""
    ) + (Get-LoggSlut $korAllaLogg)
    $inkluderaAider = $false
    if ($script:korUppgiftStart -and (Test-Path $aiderLogg)) {
        $aiderSenast = (Get-Item $aiderLogg).LastWriteTime
        if ($aiderSenast -gt $script:aiderBasTid) { $inkluderaAider = $true }
    }
    if ($inkluderaAider) {
        $loggInnehall += @(
            "",
            "## Sista rader: aider-senaste.log",
            ""
        ) + (Get-LoggSlut $aiderLogg)
    } else {
        $loggInnehall += @("", "## Sista rader: aider-senaste.log", "", "(oförändrad sedan kor-uppgift startade — utelämnad)")
    }
    Push-Location $Repo
    try {
        $ErrorActionPreference = "Continue"
        if (git status --porcelain) {
            git stash push -u -m "kor-alla före lokal-stopp $(Get-Date -Format yyyyMMdd-HHmmss)" 2>&1 | ForEach-Object { Logga $_ }
        }
        & git fetch --prune origin 2>&1 | Out-Null
        & git checkout main 2>&1 | Out-Null
        if ($LASTEXITCODE -ne 0) { throw "checkout main" }
        & git reset --hard origin/main 2>&1 | Out-Null
        if ($LASTEXITCODE -ne 0) { throw "reset origin/main" }
        $fil = Join-Path $Repo "granskning/lokal-stopp.md"
        if (-not (Test-Path $fil)) {
            @(
                "# Lokala körstopp",
                "",
                "Rader skrivs av ``verktyg/kor-alla.ps1`` när den lokala körningen stannat upprepade gånger.",
                ""
            ) | Set-Content -Encoding UTF8 $fil
        }
        Add-Content -Encoding UTF8 -Path $fil -Value $full
        $loggFil = Join-Path $Repo "granskning/lokal-stopp-logg.md"
        $loggInnehall | Set-Content -Encoding UTF8 $loggFil
        & git add $fil $loggFil 2>&1 | Out-Null
        & git commit -m "Lokal körning: stoppnotering" 2>&1 | ForEach-Object { Logga $_ }
        if ($LASTEXITCODE -ne 0) { throw "commit lokal-stopp" }
        & git push origin main 2>&1 | ForEach-Object { Logga $_ }
        if ($LASTEXITCODE -ne 0) { throw "push main" }
    } catch {
        Logga "Kunde inte skriva lokal-stopp på main: $_"
    } finally {
        Pop-Location
    }
}

function Get-AktivUppgift {
    if (Test-Path $aktivFil) {
        try { return Get-Content -Raw -Encoding UTF8 $aktivFil | ConvertFrom-Json } catch { }
    }
    if (Test-Path $statusFil) {
        try {
            $s = Get-Content -Raw -Encoding UTF8 $statusFil | ConvertFrom-Json
            if ($s.uppgift) { return @{ namn = $s.uppgift; modell = $s.modell } }
        } catch { }
    }
    return $null
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

function Set-Forsok([string]$uppgiftsFil, [int]$nytt) {
    $rader = Get-Content -Encoding UTF8 $uppgiftsFil
    $ut = @()
    $fixat = $false
    foreach ($r in $rader) {
        if (-not $fixat -and $r -match '^försök:\s*') { $ut += "försök: $nytt"; $fixat = $true; continue }
        $ut += $r
    }
    if (-not $fixat) {
        $ins = @()
        foreach ($r in $ut) {
            $ins += $r
            if ($r -match '^uppdrag:') { $ins += "försök: $nytt" }
        }
        $ut = $ins
    }
    $ut | Set-Content -Encoding UTF8 $uppgiftsFil
}

function Invoke-MisslyckatForsok([string]$namn, [string]$orsak) {
    Logga "Misslyckat försök utan kod-commits: $namn ($orsak)"
    Push-Location $Repo
    try {
        $gren = "lokal/$namn"
        $ErrorActionPreference = "Continue"
        & git checkout main 2>&1 | Out-Null
        & git show-ref --verify --quiet "refs/heads/$gren" 2>&1 | Out-Null
        if ($LASTEXITCODE -eq 0) {
            & git branch -D $gren 2>&1 | ForEach-Object { Logga $_ }
        }
        $fil = Join-Path $Repo "uppgifter/ny/$namn.md"
        if (-not (Test-Path $fil)) {
            Logga "Hittade ingen uppgiftsfil för $namn i uppgifter/ny/ på main."
            return
        }
        $forsok = 1
        foreach ($r in (Get-Content -Encoding UTF8 $fil)) {
            if ($r -match '^försök:\s*(\d+)') { $forsok = [int]$Matches[1]; break }
        }
        if ($forsok -lt 2) {
            Set-Forsok $fil 2
            $rel = "uppgifter/ny/$namn.md"
            & git add $rel 2>&1 | ForEach-Object { Logga "git: $_" }
            & git commit -m "Uppgift tillbaka: $namn (försök 2)" 2>&1 | ForEach-Object { Logga "git: $_" }
            if ($LASTEXITCODE -ne 0) {
                Logga "Kunde inte committa försök 2 för $namn — ändringen ligger kvar ocommittad på main."
            } else {
                & git push origin main 2>&1 | ForEach-Object { Logga "git: $_" }
                if ($LASTEXITCODE -ne 0) {
                    Logga "Push av försök 2 för $namn misslyckades — återställer commit, ändringen ligger kvar ocommittad."
                    & git reset HEAD~1 2>&1 | ForEach-Object { Logga "git: $_" }
                } else {
                    Logga "Uppgift $namn kvar i uppgifter/ny/ med försök 2 (committad och pushad på main)."
                }
            }
        } else {
            Logga "Uppgift $namn (försök 2) utan lokala kod-commits; Cursor tar över enligt ARBETSFLODE."
            $fel = @{ kod = 1; kalla = "tillsyn"; text = $orsak }
            Skriv-LokalStopp "Uppgift ``$namn`` misslyckades lokalt två gånger. Cursor ska ta över." $fel
        }
        try { if (Test-Path $aktivFil) { Remove-Item -Force $aktivFil } } catch { }
    } finally {
        Pop-Location
    }
}

function Invoke-RaddaAvbruten([string]$orsak) {
    Logga "TILLSYN: $orsak"
    Stop-Gradle
    $aktiv = Get-AktivUppgift
    if (-not $aktiv -or -not $aktiv.namn) {
        Logga "Ingen aktiv uppgift i status — ingen räddning."
        return
    }
    $namn = $aktiv.namn
    $gren = "lokal/$namn"
    $Modell = if ($aktiv.modell) { $aktiv.modell } else { "okänd" }
    Push-Location $Repo
    try {
        $ErrorActionPreference = "Continue"
        & git show-ref --verify --quiet "refs/heads/$gren" 2>&1 | Out-Null
        if ($LASTEXITCODE -ne 0) {
            Invoke-MisslyckatForsok $namn $orsak
            return
        }
        & git checkout $gren 2>&1 | ForEach-Object { Logga $_ }
        if ($LASTEXITCODE -ne 0) {
            Invoke-MisslyckatForsok $namn $orsak
            return
        }
        & git fetch --prune origin 2>&1 | Out-Null
        $bas = "origin/main"
        $andrade = @(git diff --name-only "$bas...HEAD" 2>$null | Where-Object { $_ -notlike "uppgifter/*" -and $_ -ne "FRAGA.md" })
        if (-not $andrade -or $andrade.Count -eq 0) {
            Invoke-MisslyckatForsok $namn $orsak
            return
        }
        $pagarRel = "uppgifter/pagar/$namn.md"
        if (-not (Test-Path $pagarRel)) {
            Logga "Saknar $pagarRel trots commits — behandlar som misslyckat försök."
            Invoke-MisslyckatForsok $namn $orsak
            return
        }
        $rader = Get-Content -Encoding UTF8 $pagarRel
        $tillatna = Filer (Sektion $rader "Ändra bara dessa filer")
        $testRad = (Sektion $rader "Test som ska gå igenom" | Where-Object { $_ -match '^\s*gradle\s' } | Select-Object -First 1)
        $testOk = $false
        $testFil = $aktiv.testFil
        if ($testFil -and (Test-Path $testFil)) {
            Logga "Kör tester efter avbrott: $testFil"
            & $testFil
            $testOk = ($LASTEXITCODE -eq 0)
        } elseif ($testRad) {
            $testCmd = ($testRad.Trim() -replace '^gradle\s', 'call gradlew.bat ')
            $stampel = Get-Date -Format "yyyyMMdd-HHmmss"
            $testFil = Join-Path $loggMapp "test-radda-$stampel.cmd"
            "@echo off`r`ncd /d `"$Repo`"`r`n$testCmd`r`n" | Set-Content -Encoding ASCII $testFil
            & $testFil
            $testOk = ($LASTEXITCODE -eq 0)
        }
        $kvar = git status --porcelain -- $tillatna 2>$null
        if ($kvar) {
            $addArgs = @("add", "--") + @($tillatna)
            & git @addArgs 2>&1 | Out-Null
            & git commit -m "Lokal modell: $namn (okommittade efter tillsyn)" 2>&1 | ForEach-Object { Logga $_ }
        }
        $harFraga = (Test-Path "FRAGA.md") -and ((Get-Content -Raw "FRAGA.md") -match "\S")
        $saknas = if ($harFraga) { @() } else { $tillatna | Where-Object { $andrade -notcontains $_ } }
        if ($saknas) { $testOk = $false }
        $utanfor = $andrade | Where-Object { $tillatna -notcontains $_ }
        $resultat = if ($testOk) { "gröna" } else { "RÖDA" }
        if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
            Logga "gh saknas — push/PR hoppades över efter räddning."
            return
        }
        & git push -u origin $gren 2>&1 | ForEach-Object { Logga $_ }
        if ($LASTEXITCODE -ne 0) {
            Logga "Push misslyckades efter räddning."
            return
        }
        $iPagar = $pagarRel
        $utanforText = if ($utanfor) { $utanfor -join ", " } else { "inga" }
        $saknasText = if ($saknas) { $saknas -join ", " } else { "inga" }
        $kropp = @"
Lokal modell körde uppgiften ``$iPagar``.

**Körningen avbröts av tillsynen** ($orsak).

- Modell: ``$Modell``
- Tester (``$testRad``): $resultat
- Filer utanför uppgiften: $utanforText
- Filer som saknas: $saknasText

Granska mot uppgiften och CLAUDE.md punkt 8.
"@
        $kroppFil = Join-Path $loggMapp "pr-tillsyn-$(Get-Date -Format yyyyMMdd-HHmmss).md"
        $kropp | Set-Content -Encoding UTF8 $kroppFil
        $prArg = @(
            "pr", "create", "--base", "main", "--head", $gren,
            "--title", $(if ($harFraga) { "Fråga: $namn (tillsyn)" } else { "Lokal: $namn (tillsyn)" }),
            "--body-file", $kroppFil
        )
        if (-not $testOk -or $utanfor -or $harFraga) { $prArg += "--draft" }
        & gh @prArg 2>&1 | ForEach-Object { Logga $_ }
        if ($LASTEXITCODE -ne 0) {
            $finns = gh pr list --head $gren --state open --json number --jq ".[0].number" 2>$null
            if ($finns) { Logga "PR #$finns fanns redan." }
        }
        & git checkout main 2>&1 | Out-Null
        Logga "Räddning klar för $namn (tester: $resultat)."
        try { Remove-Item -Force $aktivFil -ErrorAction SilentlyContinue } catch { }
    } catch {
        Logga "Räddning misslyckades: $_"
    } finally {
        Pop-Location
    }
}

function Stop-ProcessTree([System.Diagnostics.Process]$proc) {
    if (-not $proc -or $proc.HasExited) { return }
    try {
        & taskkill /T /F /PID $proc.Id 2>&1 | ForEach-Object { Logga "taskkill: $_" }
    } catch {
        Logga "taskkill misslyckades: $_"
        try { $proc.Kill() } catch { }
    }
}

function Wait-KorUppgift {
    Vakta
    $start = Get-Date
    $script:korUppgiftStart = $start
    $script:aiderBasTid = if (Test-Path $aiderLogg) { (Get-Item $aiderLogg).LastWriteTime } else { [datetime]::MinValue }
    $senasteAktivitet = Get-Date
    $senasteStatusTid = if (Test-Path $statusFil) { (Get-Item $statusFil).LastWriteTime } else { $start }
    $senasteAiderLangd = if (Test-Path $aiderLogg) { (Get-Item $aiderLogg).Length } else { 0 }
    $proc = Start-Process -FilePath "powershell.exe" -WorkingDirectory $Repo -PassThru -NoNewWindow -ArgumentList @(
        "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $skript
    )
    $script:aktivKorUppgiftPid = $proc.Id
    # Läs Handle direkt: annars kan ExitCode bli null när processen hunnit avsluta, och det blir felkod 1.
    $null = $proc.Handle
    Logga "kor-uppgift startad (PID $($proc.Id))."
    $dodad = $false
    $dodsOrsak = ""
    while (-not $proc.HasExited) {
        Start-Sleep -Seconds $pollSek
        Vakta
        $totalMin = ((Get-Date) - $start).TotalMinutes
        if ($totalMin -ge $MaxUppgiftMinuter) {
            $dodsOrsak = "uppgiften översteg $MaxUppgiftMinuter minuter totalt"
            $dodad = $true
            break
        }
        $nuStatusTid = if (Test-Path $statusFil) { (Get-Item $statusFil).LastWriteTime } else { $senasteStatusTid }
        $nuAiderLangd = if (Test-Path $aiderLogg) { (Get-Item $aiderLogg).Length } else { 0 }
        if ($nuStatusTid -gt $senasteStatusTid -or $nuAiderLangd -gt $senasteAiderLangd) {
            $senasteStatusTid = $nuStatusTid
            $senasteAiderLangd = $nuAiderLangd
            $senasteAktivitet = Get-Date
        }
        $stillaMin = ((Get-Date) - $senasteAktivitet).TotalMinutes
        if ($stillaMin -ge $HangMinuter) {
            $dodsOrsak = "status.json och aider-senaste.log oförändrade i $HangMinuter minuter"
            $dodad = $true
            break
        }
    }
    if ($dodad) {
        Stop-ProcessTree $proc
        Start-Sleep -Seconds 2
        if (-not $proc.HasExited) { Stop-ProcessTree $proc }
        Invoke-RaddaAvbruten $dodsOrsak
        $script:aktivKorUppgiftPid = $null
        return @{ kod = 4; avbrott = $true; orsak = $dodsOrsak }
    }
    $script:aktivKorUppgiftPid = $null
    $proc.WaitForExit()
    $kod = $proc.ExitCode
    if ($null -eq $kod) { $kod = 1 }
    if ($kod -ne 0) {
        $tmpFel = Get-Felbeskrivning $kod @{ avbrott = $false; orsak = "" }
        Logga "kor-uppgift avslutades: $(Format-FelRad $tmpFel)"
    }
    return @{ kod = $kod; avbrott = $false; orsak = "" }
}

Set-JavaHomeOmBehov
Logga "kor-alla startar (HangMinuter=$HangMinuter, MaxUppgiftMinuter=$MaxUppgiftMinuter, VantaTimmar=$VantaTimmar)."

while ($korda -lt $Max -and (Get-Date) -lt $slut) {
    $modell = $DefaultModell
    $aktiv = Get-AktivUppgift
    if ($aktiv -and $aktiv.modell) { $modell = $aktiv.modell }
    # Full återställning behövs inte efter en tom kö; då har inget körts.
    if (-not $tomKoSenast) { Invoke-FullAtstallning $modell }
    $tomKoSenast = $false

    $ut = Wait-KorUppgift
    $kod = $ut.kod

    if ($kod -eq 0) {
        $korda++
        $retriedEfterFel = $false
        $slut = (Get-Date).AddHours($VantaTimmar)
        continue
    }

    if ($kod -eq 4) {
        $retriedEfterFel = $false
        Logga "Ingen uppgift redo (cursor-beroenden). Väntar 60 sekunder."
        Write-Host "Ingen uppgift redo (beroenden saknas). Försöker igen om 1 minut. Stäng fönstret för att sluta." -ForegroundColor DarkGreen
        Start-Sleep -Seconds 60
        continue
    }

    if ($kod -eq 3) {
        $retriedEfterFel = $false
        $antalNy = @(Get-ChildItem (Join-Path $Repo "uppgifter\ny\*.md") -ErrorAction SilentlyContinue).Count
        if ($antalNy -eq 0) {
            # Tom kö är inget fel. Vänta och titta igen; Cursor fyller på kön. Loopen avslutas inte av tom kö.
            Logga "Kön är tom (inget fel). Väntar $TomKoMinuter minuter och tittar igen."
            $tomKoSenast = $true
            $slut = (Get-Date).AddHours($VantaTimmar)
            Start-Sleep -Seconds (60 * $TomKoMinuter)
            continue
        }
        Write-Host "Ingen uppgift redo. Försöker igen om 3 minuter. Stäng fönstret för att sluta." -ForegroundColor DarkGreen
        Start-Sleep -Seconds 180
        continue
    }

    $fel = Get-Felbeskrivning $kod $ut
    Logga (Format-FelRad $fel)

    if (-not $retriedEfterFel) {
        Logga "Full återställning och nytt försök (högst ett omförsök per uppgift och försök)."
        $retriedEfterFel = $true
        Invoke-FullAtstallning $modell
        continue
    }

    Skriv-LokalStopp "Lokal körning stannade efter omförsök." $fel
    Logga "Omförsök misslyckades — vilar $VilaEfterStoppMin minuter innan nästa uppgift."
    Start-Sleep -Seconds (60 * $VilaEfterStoppMin)
    $retriedEfterFel = $false
}

Logga "Loopen avslutad ($korda uppgifter körda)."
Write-Host "Loopen avslutad ($korda uppgifter körda). Starta 'Starta lokal modell' igen." -ForegroundColor Yellow
