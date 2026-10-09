# Kör uppgifter med kor-uppgift.ps1 tills ingen uppgift är redo, eller högst -Max stycken.
# Startas av Schemaläggaren (se installera-schema.ps1). Avbryts av Windows när datorn används igen.
param([int]$Max = 10)
$skript = Join-Path $PSScriptRoot "kor-uppgift.ps1"
for ($i = 1; $i -le $Max; $i++) {
    & powershell -NoProfile -ExecutionPolicy Bypass -File $skript
    $kod = $LASTEXITCODE
    if ($kod -eq 3) { break }          # ingen uppgift redo
    if ($kod -ne 0) { break }          # fel: stanna och låt loggen visa varför
}
