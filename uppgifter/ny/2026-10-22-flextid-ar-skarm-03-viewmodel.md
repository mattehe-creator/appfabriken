# Uppgift: ViewModel-state för årsöversikt

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt-skarm.md
försök: 1
beror-på: 2026-10-22-flextid-ar-skarm-02-prognos-ui.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/FlextidViewModel.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/FlextidViewModel.kt (`manadSammanfattning`, `skapaArsCsv`)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversikt.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/ArsOversiktUi.kt

## Uppgift
Exponera state och navigation för årsöversiktsskärmen:

1. **`arsOversiktSynlig`**: `StateFlow<Boolean>`, standard `false`. Metoder `oppnaArsOversikt()` och `stangArsOversikt()`.

2. **`arsOversiktAr`**: `StateFlow<Int>`, standard `java.time.LocalDate.now().year`. Metod `sattArsOversiktAr(ar: Int)`.

3. **`arsOversiktRader`**: `StateFlow<List<se.tmconnect.flextid.regler.ManadsRad>>` från `combine(repository.observeraAllaDagar(), installning, arsOversiktAr)` som anropar `arsOversikt(dagar, ar, installning)`.

4. **`arsOversiktPrognosMin`**: `StateFlow<Int?>` från `combine(saldo, repository.observeraAllaDagar(), installning, arsOversiktAr)` som anropar `uppskattatSaldoVidArsslut(saldo, dagar, ar, installning, LocalDate.now())`.

5. **`forberedArsCsvExport()`**: sätt exportår i UI-lagret så CSV matchar skärmen — `uiInstallningarLager.sattExportArManad(YearMonth.of(arsOversiktAr.value, 1))` (samma mönster som export i inställningar).

Använd `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), …)` som övriga flows.

## Exempel
- Efter `oppnaArsOversikt()` är `arsOversiktSynlig` true.
- `sattArsOversiktAr(2026)` ger 12 rader i `arsOversiktRader` (tom databas: alla saldon = ingångssaldo).

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ArsOversiktUiTest" --tests "*ArsOversiktTest" --tests "*SaldoPrognosTest"

## Gör inte
- Ändra andra filer.
- Skriv om `arsOversikt` eller `saldoVidArssluten`.
