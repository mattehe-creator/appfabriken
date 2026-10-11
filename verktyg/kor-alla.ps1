# Kör uppgifter med kor-uppgift.ps1 i följd, utan att någonsin ge upp av sig själv.
#  - Ingen uppgift redo (de väntar på granskning): väntar 3 minuter och försöker igen.
#  - Körningen föll (git, Gradle, nätet, Aider): väntar 5 minuter och försöker igen. Efter 6 fel i rad
#    väntar den 30 minuter mellan försöken, så att ett fel som kräver Mattias syns utan att loopen spinner.
#  - Tidsgränsen -VantaTimmar räknas från senaste lyckade uppgift (eller start), inte från start.
#    Tidigare stängde en fast gräns på 4 timmar från start av loopen, även mitt i ett fungerande flöde.
#  - Vakten (vakt.ps1): körs något tungt, till exempel ett spel, väntar loopen och lastar ur modellen ur
#    grafikminnet. Den startar igen först när datorn varit fri i 60 sekunder. Pausen räknas inte som fel
#    och inte mot tidsgränsen.
# Stängs fönstret, eller används datorn när Schemaläggaren har startat den, så stoppas den.
param([int]$Max = 200, [double]$VantaTimmar = 8, [string]$Modell = "ollama_chat/qwen3-coder:30b")
. (Join-Path $PSScriptRoot "vakt.ps1")
$skript = Join-Path $PSScriptRoot "kor-uppgift.ps1"
$slut = (Get-Date).AddHours($VantaTimmar)
$korda = 0
$felIRad = 0
$sistVakt = [datetime]::MinValue
$varPausad = $false
$klarSedan = $null
# GitHubs schema (cron) körs inte pålitligt. Den här loopen tickar redan var 3:e minut, så den startar
# vaktmästaren (.github/workflows/vaktmastare.yml) högst var 10:e minut. Fel här stoppar ingenting.
function Vakta {
    if (((Get-Date) - $script:sistVakt).TotalMinutes -lt 10) { return }
    $script:sistVakt = Get-Date
    try { & gh api -X POST repos/mattehe-creator/appfabriken/actions/workflows/vaktmastare.yml/dispatches -f ref=main 2>&1 | Out-Null } catch { }
}
while ($korda -lt $Max -and (Get-Date) -lt $slut) {
    Vakta
    $tungt = Tungt-Pagar
    if ($tungt) {
        $varPausad = $true; $klarSedan = $null
        Vakt-FrigorModell $Modell
        Vakt-Status "pausad" "Något tungt körs ($tungt). Den lokala modellen väntar."
        Write-Host "Pausad: $tungt. Kontrollerar igen om 60 sekunder." -ForegroundColor Yellow
        $slut = (Get-Date).AddHours($VantaTimmar)
        Start-Sleep -Seconds 60
        continue
    }
    if ($varPausad) {
        # Datorn blev fri. Vänta 60 sekunder till, så att en kort paus i ett spel inte startar modellen.
        if (-not $klarSedan) { $klarSedan = Get-Date }
        if (((Get-Date) - $klarSedan).TotalSeconds -lt 60) { Start-Sleep -Seconds 20; continue }
        $varPausad = $false; $klarSedan = $null
    }
    & powershell -NoProfile -ExecutionPolicy Bypass -File $skript
    $kod = $LASTEXITCODE
    if ($kod -eq 0) {
        $korda++; $felIRad = 0
        $slut = (Get-Date).AddHours($VantaTimmar)
        continue
    }
    if ($kod -eq 4) {
        # kor-uppgift.ps1 avbröt för att något tungt startade. Inget fel.
        $varPausad = $true; $klarSedan = $null
        $slut = (Get-Date).AddHours($VantaTimmar)
        Start-Sleep -Seconds 60
        continue
    }
    if ($kod -eq 3) {
        Write-Host "Ingen uppgift redo. Försöker igen om 3 minuter. Stäng fönstret för att sluta." -ForegroundColor DarkGreen
        Start-Sleep -Seconds 180
        Vakta
        continue
    }
    $felIRad++
    $vanta = if ($felIRad -ge 6) { 1800 } else { 300 }
    Write-Host "Körningen stannade med fel ($kod), fel i rad: $felIRad. Se loggen i $env:LOCALAPPDATA\appfabriken\logg. Försöker igen om $($vanta / 60) minuter." -ForegroundColor Red
    Start-Sleep -Seconds $vanta
}
Write-Host "Loopen avslutad ($korda uppgifter körda). Starta 'Starta lokal modell' igen." -ForegroundColor Yellow
