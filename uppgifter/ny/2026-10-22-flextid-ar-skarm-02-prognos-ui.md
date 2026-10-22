# Uppgift: Hjälpfunktioner prognos och årsutfall

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt-skarm.md
försök: 1
beror-på: 2026-10-22-flextid-ar-skarm-01-strangar.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/ui/ArsOversiktUi.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/ui/ArsOversiktUiTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/SaldoPrognos.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt (`dagensUtfall`)

## Uppgift
Tunna hjälpfunktioner i paketet `ui` (ingen omräkning av saldo per månad — det gör `arsOversikt`).

```kotlin
fun utfallForKalenderarMin(
    dagar: List<se.tmconnect.flextid.data.Dag>,
    ar: Int,
    installning: se.tmconnect.flextid.data.Installning,
    idag: java.time.LocalDate,
): Int

fun uppskattatSaldoVidArsslut(
    saldoNuMin: Int,
    dagar: List<se.tmconnect.flextid.data.Dag>,
    ar: Int,
    installning: se.tmconnect.flextid.data.Installning,
    idag: java.time.LocalDate,
): Int?
```

- `utfallForKalenderarMin`: summa av `dagensUtfall` för dagar där `datum.year == ar` och `!datum.isAfter(idag)`.
- `uppskattatSaldoVidArsslut`: om `ar != idag.year` → `null`. Annars anropa `saldoVidArssluten(saldoNuMin, utfallForKalenderarMin(...), idag)`.

## Exempel
Installning standard (normaltid 480, mask vardagar). Dagar tomma, `ar = 2026`, `idag = 2026-07-02`, `saldoNuMin = 120`, utfall i år totalt 120 min → `uppskattatSaldoVidArsslut` = 239 (samma som `SaldoPrognosTest`).

`ar = 2025`, `idag = 2026-07-02` → `uppskattatSaldoVidArsslut` = null.

`idag = 2026-01-29` → null (via `saldoVidArssluten`).

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ArsOversiktUiTest"

## Gör inte
- Ändra `SaldoPrognos.kt`, `ArsOversikt.kt` eller ViewModel.
- Lägga till beroenden.
