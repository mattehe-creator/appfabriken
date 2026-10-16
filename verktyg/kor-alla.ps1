# Kör uppgifter med kor-uppgift.ps1 i följd, utan att någonsin ge upp av sig själv.
# Övervakar barnprocessen (tillsyn): dödar hängda körningar, räddar commits, stoppar Gradle.
#  - Ingen uppgift redo: väntar 3 minuter och försöker igen (exit 3).
#  - Fel eller avbrott: går vidare till nästa försök/uppgift. Tre avbrott i rad: 30 min vila + rad i granskning/lokal-stopp.md på main.
#  - Tidsgränsen -VantaTimmar räknas från senaste lyckade uppgift (eller start).
param(
    [int]$Max = 200,
    [double]$VantaTimmar = 8,
    [int]$HangMinuter = 15,
    [int]$MaxUppgiftMinuter = 90
)

$Repo = Split-Path -Parent $PSScriptRoot
$skript = Join-Path $PSScriptRoot "kor-uppgift.ps1"
$loggMapp = Join-Path $env:LOCALAPPDATA "appfabriken\logg"
New-Item -ItemType Directory -Force -Path $loggMapp | Out-Null
$korAllaLogg = Join-Path $loggMapp "kor-alla.log"
$statusFil = Join-Path $env:LOCALAPPDATA "appfabriken\status.json"
$aiderLogg = Join-Path $loggMapp "aider-senaste.log"
$aktivFil = Join-Path $env:LOCALAPPDATA "appfabriken\aktiv-uppgift.json"

$slut = (Get-Date).AddHours($VantaTimmar)
$korda = 0
$avbrottIRad = 0
$sistVakt = [datetime]::MinValue
$VaktMinuter = 30
$pollSek = 30

function Logga([string]$text) {
    $rad = "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') $text"
    Write-Host $rad
    try { Add-Content -Encoding UTF8 -Path $korAllaLogg -Value $rad } catch { }
}

function Stop-Gradle {
    $bat = Join-Path $Repo "gradlew.bat"
    if (-not (Test-Path $bat)) { return }
    try {
        Push-Location $Repo
        & cmd /c "`"$bat`" --stop" 2>&1 | ForEach-Object { Logga "gradlew --stop: $_" }
    } catch {
        Logga "gradlew --stop misslyckades: $_"
    } finally {
        Pop-Location
    }
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

function Skriv-LokalStopp([string]$rad) {
    $full = "$(Get-Date -Format s) $rad"
    Logga "Skriver lokal-stopp: $full"
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
        & git add $fil 2>&1 | Out-Null
        & git commit -m "Lokal körning: stoppnotering" 2>&1 | ForEach-Object { Logga $_ }
        if ($LASTEXITCODE -ne 0) { throw "commit lokal-stopp" }
        & git push origin main 2>&1 | ForEach-Object { Logga $_ }
        if ($LASTEXITCODE -ne 0) { throw "push main" }
    } catch {
        Logga "Kunde inte skriva lokal-stopp.md på main: $_"
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
            Skriv-LokalStopp "Uppgift ``$namn`` misslyckades lokalt två gånger (senast: $orsak). Cursor ska ta över."
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
    $senasteAktivitet = Get-Date
    $senasteStatusTid = if (Test-Path $statusFil) { (Get-Item $statusFil).LastWriteTime } else { $start }
    $senasteAiderLangd = if (Test-Path $aiderLogg) { (Get-Item $aiderLogg).Length } else { 0 }
    $proc = Start-Process -FilePath "powershell.exe" -WorkingDirectory $Repo -PassThru -NoNewWindow -ArgumentList @(
        "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", $skript
    )
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
        Stop-Gradle
        return @{ kod = 4; avbrott = $true; orsak = $dodsOrsak }
    }
    $kod = if ($proc.HasExited) { $proc.ExitCode } else { 1 }
    if ($null -eq $kod) { $kod = 1 }
    return @{ kod = $kod; avbrott = $false; orsak = "" }
}

Logga "kor-alla startar (HangMinuter=$HangMinuter, MaxUppgiftMinuter=$MaxUppgiftMinuter)."
Stop-Gradle

while ($korda -lt $Max -and (Get-Date) -lt $slut) {
    $ut = Wait-KorUppgift
    $kod = $ut.kod
    if ($ut.avbrott) {
        $avbrottIRad++
        Logga "Avbrott i rad: $avbrottIRad ($($ut.orsak))."
        if ($avbrottIRad -ge 3) {
            Skriv-LokalStopp "Tre avbrott i rad på den lokala körningen (senast: $($ut.orsak))."
            Logga "Tre avbrott i rad — vilar 30 minuter."
            Start-Sleep -Seconds 1800
            $avbrottIRad = 0
        }
        continue
    }
    if ($kod -eq 0) {
        $korda++
        $avbrottIRad = 0
        $slut = (Get-Date).AddHours($VantaTimmar)
        continue
    }
    if ($kod -eq 3) {
        $avbrottIRad = 0
        Write-Host "Ingen uppgift redo. Försöker igen om 3 minuter. Stäng fönstret för att sluta." -ForegroundColor DarkGreen
        Start-Sleep -Seconds 180
        continue
    }
    $avbrottIRad++
    Stop-Gradle
    $orsak = if ($kod -eq 2) { "Uppgiften fastnade (t.ex. git-gren)." } else { "Körningen stannade med fel ($kod)." }
    Logga "$orsak Avbrott i rad: $avbrottIRad."
    if ($avbrottIRad -ge 3) {
        Skriv-LokalStopp "Tre avbrott i rad på den lokala körningen (senast: $orsak)."
        Logga "Tre avbrott i rad — vilar 30 minuter."
        Start-Sleep -Seconds 1800
        $avbrottIRad = 0
    }
    Start-Sleep -Seconds 60
}

Logga "Loopen avslutad ($korda uppgifter körda)."
Write-Host "Loopen avslutad ($korda uppgifter körda). Starta 'Starta lokal modell' igen." -ForegroundColor Yellow
