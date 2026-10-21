# Uppgift: När blir saldot noll

uppdrag: uppdrag/claude/2026-10-21-flextid-gahem-semester.md
försök: 1
beror-på: 2026-10-21-flextid-gahem-01-gahemtid.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/GaHemTid.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/GaHemTidTest.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt

## Uppgift
Lägg till i `GaHemTid.kt`:

```kotlin
fun gaHemTidForNollsaldo(
    start: LocalTime,
    rastMin: Int,
    normaltidMin: Int,
    saldoForeIdagMin: Int,
): LocalTime?
```

`saldoForeIdagMin` är saldot utan dagens dag (positivt = plus, negativt = minus). Den tid då totalsaldot blir 0 är `start + rast + normaltid - saldoForeIdagMin`.

- Räkna `arbete = normaltidMin - saldoForeIdagMin`, med negativa `normaltidMin` och `rastMin` som 0.
- Är `arbete <= 0` har användaren redan nog med plus: returnera `start` (man kan inte gå hem före start).
- Annars samma som `gaHemTid(start, rastMin, arbete)`, inklusive `null` efter midnatt.

## Exempel
- 08:00, rast 30, normaltid 480, saldo 0 → 16:30
- 08:00, rast 30, normaltid 480, saldo +60 → 15:30
- 08:00, rast 30, normaltid 480, saldo −90 → 18:00
- 08:00, rast 30, normaltid 480, saldo +600 → 08:00
- 08:00, rast 30, normaltid 480, saldo +480 → 08:00
- 14:00, rast 30, normaltid 480, saldo −120 → null

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*GaHemTidTest"

## Gör inte
- Ändra andra filer.
- Ändra `gaHemTid`.
- Lägga till beroenden.
