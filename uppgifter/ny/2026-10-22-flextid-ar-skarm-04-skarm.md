# Uppgift: Compose-skärm Årsöversikt

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt-skarm.md
försök: 1
beror-på: 2026-10-22-flextid-ar-skarm-01-strangar.md

Skärmen tar data via parametrar så att Robolectric-test kan köras utan ViewModel. Koppling sker i uppgift 05.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/ArsOversiktSkarm.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/ui/ArsOversiktSkarmTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/FlextidSkarmar.kt (`VeckaInnehall`, `TopAppBar`, `tryckyta48`)
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/ValkommenSkarm.kt (Robolectric-test)

## Uppgift
Composable `ArsOversiktSkarm` med egna parametrar (ingen ViewModel i denna fil):

```kotlin
@Composable
fun ArsOversiktSkarm(
    ar: Int,
    onArChange: (Int) -> Unit,
    rader: List<ManadsRad>,
    prognosMin: Int?,
    locale: Locale,
    onTillbaka: () -> Unit,
    onDelaCsv: () -> Unit,
    modifier: Modifier = Modifier,
)
```

- **TopAppBar**: titel `arsoversikt_rubrik`, tillbaka-knapp med `arsoversikt_tillbaka` som innehållsbeskrivning, anropar `onTillbaka`.
- **År**: `OutlinedTextField` eller liknande för `ar` (samma enkelhet som export-år i inställningar), etikett `arsoversikt_ar`.
- **Prognos** (överst under år): rubrik `arsoversikt_prognos_rubrik`. Om `prognosMin != null`: visa `arsoversikt_prognos_varde` med `formateraSaldoMinuter(prognosMin, locale)` och kort text `arsoversikt_prognos_forklaring`. Om null: `arsoversikt_prognos_ingen_data`.
- **Lista**: `LazyColumn` med en rad per `ManadsRad` — månadsnamn (`Month.of(rad.manad).getDisplayName(FULL_STANDALONE, locale)`), arbetad tid, utfall, saldo vid månadens slut (formatera som i CSV).
- **Knapp**: `OutlinedButton` `arsoversikt_dela_csv`, minst 48 dp, anropar `onDelaCsv`.

All text via `stringResource`. Kolumnrubriker: `csv_manad`, `csv_arbetad`, `csv_utfall`, `csv_saldo`.

## Exempel
Test (sv-rSE): sätt `rader` med en rad (januari, saldo vid slut 50 min → formaterat saldo), `prognosMin = 120`, `ar = 2026`. Assert: noder med "Årsöversikt", "Uppskattat saldo vid årets slut", "Dela som CSV", och januarinamn syns.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ArsOversiktSkarmTest"

## Gör inte
- Ändra `FlextidSkarmar.kt` eller `MainActivity.kt`.
- Anropa `arsOversikt` direkt här (data kommer via parametrar).
