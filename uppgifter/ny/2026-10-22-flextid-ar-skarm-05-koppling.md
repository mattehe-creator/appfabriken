# Uppgift: Navigering och CSV från årsöversikt

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt-skarm.md
försök: 1
beror-på: 2026-10-22-flextid-ar-skarm-03-viewmodel.md, 2026-10-22-flextid-ar-skarm-04-skarm.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/FlextidSkarmar.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/MainActivity.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/MainActivity.kt (`onExporteraArsCsv`, `skapaArsCsvContract`)
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/ArsOversiktSkarm.kt

## Uppgift
1. **Inställningar-fliken** (`InstallningarInnehall`): lägg en tydlig ingång (t.ex. `OutlinedButton` eller `TextButton` med `tryckyta48`) med text `arsoversikt_oppna`, ovanför export-rubriken. Ny callback `onOppnaArsOversikt: () -> Unit` från `FlextidStartSkarm`.

2. **Overlay**: I `FlextidStartSkarm`, när `viewModel.arsOversiktSynlig` är true (collectAsState), visa `ArsOversiktSkarm` i en `Box(Modifier.fillMaxSize())` ovanpå flikinnehållet (behåll bottom bar valfritt dold eller synlig — hela skärmen ska kännas som egen vy med tillbaka).

   Koppla:
   - `ar` / `onArChange` → `arsOversiktAr` / `viewModel::sattArsOversiktAr`
   - `rader` → `arsOversiktRader`
   - `prognosMin` → `arsOversiktPrognosMin`
   - `onTillbaka` → `viewModel::stangArsOversikt`
   - `onDelaCsv` → `viewModel.forberedArsCsvExport()` sedan befintlig `onExporteraArsCsv()`

3. **MainActivity**: skicka `onOppnaArsOversikt = viewModel::oppnaArsOversikt` till `FlextidStartSkarm`. Ingen ny Activity Result (CSV finns redan).

## Exempel
- Tryck "Årsöversikt" under Inställningar → skärmen med 12 månader syns.
- Tillbaka → tillbaka till Inställningar.
- "Dela som CSV" → systemets spara-dialog med filnamn `flextid-ar-<år>.csv` och samma innehåll som idag via `skapaArsCsv`.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*StringsNycklarTest" --tests "*ArsOversiktSkarmTest" --tests "*ArsOversiktTest"

## Gör inte
- Ändra `:karna`, databas eller `regler/ArsOversikt.kt`.
- Lägga till nätverk eller behörigheter.
- Hårdkoda texter.
