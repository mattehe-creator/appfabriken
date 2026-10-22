# Uppgift: hösta avgift enligt promille

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-13-prisbasbelopp-for-ar.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Overlatelseavgift.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/OverlatelseavgiftBeraknaTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaData.kt

## Uppgift
Skapa `Overlatelseavgift.kt`:

```kotlin
enum class Avgiftstyp { OVERLATELSE, PANTSATTNING, ANDRAHAND }

fun avgiftFranPromille(prisbasbelopp: Long, promilleAvPrisbasbelopp: Int): Long
fun hogstaOverlatelseavgift(prisbasbelopp: Long, data: Map<String, StammaRad>): Long
fun hogstaPantsattningsavgift(prisbasbelopp: Long, data: Map<String, StammaRad>): Long
fun hogstaAndrahandsavgift(prisbasbelopp: Long): Long
```

- `avgiftFranPromille`: `prisbasbelopp * promille / 1000`, **avrundat nedåt** till hela kronor (`floor`).
- `hogstaOverlatelseavgift` / `hogstaPantsattningsavgift`: promille från `referens_overlatelse_0` respektive `referens_pantsattning_0` i data (25 och 10 i standard-CSV). Saknas rad → `IllegalArgumentException`.
- `hogstaAndrahandsavgift`: **100 promille** (10 % enligt BRL 7 kap. 14 §), utan CSV.

Negativt prisbasbelopp eller promille < 0 → `IllegalArgumentException`.

## Exempel
- prisbasbelopp 59200, promille 25 → 1480
- prisbasbelopp 58800, promille 10 → 588
- prisbasbelopp 59200, andrahand → 5920
- prisbasbelopp 59201, promille 25 → 1480 (nedåt)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*OverlatelseavgiftBeraknaTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
