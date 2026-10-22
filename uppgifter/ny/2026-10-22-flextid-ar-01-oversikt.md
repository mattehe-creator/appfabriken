# Uppgift: Årsöversikt per månad

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversikt.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ArsOversiktTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattning.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattningTest.kt

## Uppgift
```kotlin
data class ManadsRad(val manad: Int, val arbetadMin: Int, val utfallMin: Int, val saldoVidSlutMin: Int)

fun arsOversikt(dagar: List<Dag>, ar: Int, installning: Installning): List<ManadsRad>
```

- Alltid 12 rader, `manad` 1..12.
- `arbetadMin`: summan av `arbetadeMinuter(start, slut, rastMin)` för dagar med typ `ARBETE` i månaden.
- `utfallMin`: summan av `dagensUtfall(dag, installning.normaltidMinPerDag, installning.arbetsdagarMask)` för dagarna i månaden.
- `saldoVidSlutMin`: `installning.ingangssaldoMin` plus utfallet för **alla** dagar med datum till och med månadens sista dag, även dagar från tidigare år.

## Exempel
Installning(normaltidMinPerDag = 480, standardRastMin = 30, ingangssaldoMin = 60), övriga fält standard (måndag–fredag).
Dagar:
- 2025-12-01 (måndag) 08:00–17:00, rast 0 → arbetat 540, utfall +60
- 2026-01-05 (måndag) 08:00–17:00, rast 30 → arbetat 510, utfall +30
- 2026-03-02 (måndag) 08:00–16:00, rast 60 → arbetat 420, utfall −60

`arsOversikt(dagar, 2026, installning)`:
- januari: arbetad 510, utfall 30, saldo 150
- februari: 0, 0, 150
- mars: 420, −60, 90
- april–december: 0, 0, 90

Inga dagar alls → 12 rader med 0, 0, 60.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ArsOversiktTest"

## Gör inte
- Ändra andra filer.
