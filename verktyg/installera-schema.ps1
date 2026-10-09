# Lägger in den lokala modellens körning i Schemaläggaren. Körs en gång:
#   powershell -ExecutionPolicy Bypass -File verktyg\installera-schema.ps1
# Uppgiften startar när datorn har varit oanvänd i 10 minuter, och dessutom varje natt kl. 01:00.
# Windows stoppar den när datorn används igen. Inga administratörsrättigheter behövs.
$namn = "Appfabriken lokal modell"
$skript = Join-Path $PSScriptRoot "kor-alla.ps1"
$atgard = New-ScheduledTaskAction -Execute "powershell.exe" `
    -Argument "-NoProfile -WindowStyle Hidden -ExecutionPolicy Bypass -File `"$skript`"" `
    -WorkingDirectory (Split-Path -Parent $PSScriptRoot)

$natt = New-ScheduledTaskTrigger -Daily -At 01:00
$klass = Get-CimClass -Namespace Root/Microsoft/Windows/TaskScheduler -ClassName MSFT_TaskIdleTrigger
$ledig = New-CimInstance -CimClass $klass -ClientOnly

$installningar = New-ScheduledTaskSettingsSet `
    -RunOnlyIfIdle -IdleDuration (New-TimeSpan -Minutes 10) -IdleWaitTimeout (New-TimeSpan -Hours 2) `
    -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries `
    -ExecutionTimeLimit (New-TimeSpan -Hours 8) -MultipleInstances IgnoreNew -StartWhenAvailable
$installningar.IdleSettings.StopOnIdleEnd = $true
$installningar.IdleSettings.RestartOnIdle = $true

$huvud = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -LogonType Interactive -RunLevel Limited

Register-ScheduledTask -TaskName $namn -Action $atgard -Trigger @($ledig, $natt) `
    -Settings $installningar -Principal $huvud -Force | Out-Null
Write-Host "Klart: '$namn' finns i Schemaläggaren." -ForegroundColor Green
Get-ScheduledTask -TaskName $namn | Select-Object TaskName, State
