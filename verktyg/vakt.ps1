# Vakt: avgör om datorn används till något tungt (spel, videoredigering, annat som tar GPU:n), så att den
# lokala modellen inte tar grafikminnet och GPU-tiden från det. Den mäter vad datorn faktiskt gör och
# använder ingen lista över program, så den fångar även spel som inte finns än.
#
# Används av kor-alla.ps1 och kor-uppgift.ps1:
#   . (Join-Path $PSScriptRoot "vakt.ps1")
#   $orsak = Tungt-Pagar        # text om något tungt körs, annars $null
#
# Tre mått, i den här ordningen:
#   1. Ett program kör i helskärm (Windows egen koll, SHQueryUserNotificationState).
#   2. Något annat än Ollama använder mer än $VaktAnnatVramGB GB grafikminne. Det är minnet som är
#      flaskhalsen: modellen behöver nästan hela de 12 GB, och ett spel som delar kortet gör att den
#      spiller över till vanligt RAM och att spelet hackar.
#   3. Något annat än Ollama belastar GPU:ns 3D-motor mer än $VaktAnnanGpuProcent procent (medel över 3 s).
# Mätningen går via WMI (Win32_PerfFormattedData_GPUPerformanceCounters_*), som inte är översatt till
# svenska på det sätt som prestandaräknarnas namn är. Går mätningen inte att göra räknas datorn som ledig
# (samma beteende som före vakten), men helskärmskollen gäller ändå.

$script:VaktAnnatVramGB = 3
$script:VaktAnnanGpuProcent = 25
# Processer som inte räknas som "annat": Ollama själv (och Aiders modell), plus Windows eget skrivbord.
$script:VaktEgna = @("ollama", "ollama app", "ollama_llama_server", "ollama-runner", "dwm", "csrss", "system", "idle")

function Vakt-Helskarm {
    # 2 = helskärmsprogram, 3 = Direct3D i helskärm, 4 = presentationsläge.
    try {
        if (-not ("Appfabriken.Skal" -as [type])) {
            Add-Type -Namespace Appfabriken -Name Skal -MemberDefinition '[System.Runtime.InteropServices.DllImport("shell32.dll")] public static extern int SHQueryUserNotificationState(out int state);'
        }
        $tillstand = 0
        $hr = [Appfabriken.Skal]::SHQueryUserNotificationState([ref]$tillstand)
        if ($hr -eq 0 -and ($tillstand -eq 2 -or $tillstand -eq 3 -or $tillstand -eq 4)) { return $true }
    } catch { }
    return $false
}

function Vakt-Namn([hashtable]$namn, [int]$id) {
    if ($namn.ContainsKey($id)) { return $namn[$id] }
    return "process $id"
}

function Tungt-Pagar {
    $namn = @{}
    $egnaId = @{}
    foreach ($p in (Get-Process -ErrorAction SilentlyContinue)) {
        $namn[[int]$p.Id] = $p.ProcessName
        if ($script:VaktEgna -contains $p.ProcessName) { $egnaId[[int]$p.Id] = $true }
    }

    if (Vakt-Helskarm) { return "ett program kör i helskärm" }

    # Grafikminne
    try {
        $minne = @{}
        foreach ($m in (Get-CimInstance Win32_PerfFormattedData_GPUPerformanceCounters_GPUProcessMemory -ErrorAction Stop)) {
            if ($m.Name -match '^pid_(\d+)_') {
                $id = [int]$Matches[1]
                if ($egnaId.ContainsKey($id)) { continue }
                $minne[$id] = [double]$minne[$id] + [double]$m.DedicatedUsage
            }
        }
        $topp = $minne.GetEnumerator() | Sort-Object Value -Descending | Select-Object -First 1
        if ($topp -and $topp.Value -gt ($script:VaktAnnatVramGB * 1GB)) {
            $gb = [math]::Round($topp.Value / 1GB, 1)
            return ("{0} använder {1} GB grafikminne" -f (Vakt-Namn $namn ([int]$topp.Key)), $gb)
        }
    } catch { }

    # GPU-belastning. Första mätningen kastas, eftersom WMI ibland ger 0 vid första frågan.
    try {
        $summa = @{}
        for ($i = 0; $i -lt 4; $i++) {
            if ($i -gt 0) { Start-Sleep -Seconds 1 }
            $prov = Get-CimInstance Win32_PerfFormattedData_GPUPerformanceCounters_GPUEngine -ErrorAction Stop
            if ($i -eq 0) { continue }
            foreach ($e in $prov) {
                if ($e.Name -match '^pid_(\d+)_.*engtype_3D$') {
                    $id = [int]$Matches[1]
                    if ($egnaId.ContainsKey($id)) { continue }
                    $summa[$id] = [double]$summa[$id] + [double]$e.UtilizationPercentage
                }
            }
        }
        $topp = $summa.GetEnumerator() | Sort-Object Value -Descending | Select-Object -First 1
        if ($topp) {
            $medel = [math]::Round($topp.Value / 3)
            if ($medel -gt $script:VaktAnnanGpuProcent) {
                return ("{0} belastar GPU:n ({1} %)" -f (Vakt-Namn $namn ([int]$topp.Key)), $medel)
            }
        }
    } catch { }

    return $null
}

# Läget till panelen (verktyg/panel.py läser status.json).
function Vakt-Status([string]$fas, [string]$text) {
    try {
        $mapp = Join-Path $env:LOCALAPPDATA "appfabriken"
        New-Item -ItemType Directory -Force -Path $mapp | Out-Null
        @{ fas = $fas; text = $text; uppgift = ""; modell = ""; tid = (Get-Date).ToString("s") } |
            ConvertTo-Json | Set-Content -Encoding UTF8 (Join-Path $mapp "status.json")
    } catch { }
}

# Lastar ur modellen ur grafikminnet direkt, i stället för att vänta ut Ollamas 5 minuter.
function Vakt-FrigorModell([string]$modell) {
    $ren = ($modell -replace '^ollama_chat/', '') -replace '^ollama/', ''
    try { & ollama stop $ren 2>&1 | Out-Null } catch { }
}
