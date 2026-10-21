# Uppgift: Sammanfattning för ett intervall

uppdrag: uppdrag/claude/2026-10-22-flextid-oversikt.md
försök: 1
beror-på: 2026-10-22-flextid-oversikt-01-normaltid-intervall.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattning.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattningTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsNormaltid.kt

## Uppgift
```kotlin
data class PeriodSammanfattning(val arbetadMin: Int, val normaltidMin: Int, val utfallMin: Int)

fun periodSammanfattning(
    dagar: List<se.tmconnect.flextid.data.Dag>,
    fran: LocalDate,
    till: LocalDate,
    installning: se.tmconnect.flextid.data.Installning,
    aftnarLediga: Boolean,
): PeriodSammanfattning
```

- Ta bara dagar med `datum` från och med `fran` till och med `till`.
- `arbetadMin` = summan av `arbetadeMinuter(start, slut, rastMin)` för dagar med typ `ARBETE`.
- `utfallMin` = summan av `dagensUtfall(dag, installning.normaltidMinPerDag, installning.arbetsdagarMask)`.
- `normaltidMin` = `normaltidForIntervallMin(fran, till, installning.arbetsdagarMask, installning.normaltidMinPerDag, aftnarLediga)`.

## Exempel
Installning normaltid 480, mask måndag–fredag. Vecka 2026-10-12 – 2026-10-18:
- Mån 08:00–16:30 rast 30, tis 07:45–16:45 rast 30, ons 08:00–16:30 rast 30, tors SEMESTER, fre 08:15–15:00 rast 15
- → arbetadMin 480 + 510 + 480 + 0 + 390 = 1860, utfallMin 0 + 30 + 0 + 0 − 90 = −60, normaltidMin 2400
- Tom lista → (0, 2400, 0)

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*PeriodSammanfattningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
