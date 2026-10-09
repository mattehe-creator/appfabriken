# Kör uppgifter med kor-uppgift.ps1 i följd. När ingen uppgift är redo (de väntar på granskning)
# väntar den 10 minuter och försöker igen, i högst -VantaTimmar. Stängs fönstret, eller används
# datorn när Schemaläggaren har startat den, så stoppas den.
param([int]$Max = 20, [double]$VantaTimmar = 4)
$skript = Join-Path $PSScriptRoot "kor-uppgift.ps1"
$slut = (Get-Date).AddHours($VantaTimmar)
$korda = 0
while ($korda -lt $Max -and (Get-Date) -lt $slut) {
    & powershell -NoProfile -ExecutionPolicy Bypass -File $skript
    $kod = $LASTEXITCODE
    if ($kod -eq 0) { $korda++; continue }
    if ($kod -eq 3) {
        Write-Host "Väntar 10 minuter på granskning. Stäng fönstret för att sluta." -ForegroundColor DarkGreen
        Start-Sleep -Seconds 600
        continue
    }
    Write-Host "Körningen stannade med fel ($kod). Se loggen i $env:LOCALAPPDATA\appfabriken\logg." -ForegroundColor Red
    break
}
