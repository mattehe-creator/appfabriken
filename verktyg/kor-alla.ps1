# Kör uppgifter med kor-uppgift.ps1 i följd, utan att någonsin ge upp av sig själv.
#  - Ingen uppgift redo (de väntar på granskning): väntar 3 minuter och försöker igen.
#  - Körningen föll (git, Gradle, nätet, Aider): väntar 5 minuter och försöker igen. Efter 6 fel i rad
#    väntar den 30 minuter mellan försöken, så att ett fel som kräver Mattias syns utan att loopen spinner.
#  - Tidsgränsen -VantaTimmar räknas från senaste lyckade uppgift (eller start), inte från start.
#    Tidigare stängde en fast gräns på 4 timmar från start av loopen, även mitt i ett fungerande flöde.
# Stängs fönstret, eller används datorn när Schemaläggaren har startat den, så stoppas den.
param([int]$Max = 200, [double]$VantaTimmar = 8)
$skript = Join-Path $PSScriptRoot "kor-uppgift.ps1"
$slut = (Get-Date).AddHours($VantaTimmar)
$korda = 0
$felIRad = 0
while ($korda -lt $Max -and (Get-Date) -lt $slut) {
    & powershell -NoProfile -ExecutionPolicy Bypass -File $skript
    $kod = $LASTEXITCODE
    if ($kod -eq 0) {
        $korda++; $felIRad = 0
        $slut = (Get-Date).AddHours($VantaTimmar)
        continue
    }
    if ($kod -eq 3) {
        Write-Host "Ingen uppgift redo. Försöker igen om 3 minuter. Stäng fönstret för att sluta." -ForegroundColor DarkGreen
        Start-Sleep -Seconds 180
        continue
    }
    $felIRad++
    $vanta = if ($felIRad -ge 6) { 1800 } else { 300 }
    Write-Host "Körningen stannade med fel ($kod), fel i rad: $felIRad. Se loggen i $env:LOCALAPPDATA\appfabriken\logg. Försöker igen om $($vanta / 60) minuter." -ForegroundColor Red
    Start-Sleep -Seconds $vanta
}
Write-Host "Loopen avslutad ($korda uppgifter körda). Starta 'Starta lokal modell' igen." -ForegroundColor Yellow
