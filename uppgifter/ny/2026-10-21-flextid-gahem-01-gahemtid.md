# Uppgift: När kan jag gå hem

uppdrag: uppdrag/claude/2026-10-21-flextid-gahem-semester.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/GaHemTid.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/GaHemTidTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt

## Uppgift
Skapa `GaHemTid.kt` i paketet `se.tmconnect.flextid.regler`:

```kotlin
fun gaHemTid(start: LocalTime, rastMin: Int, normaltidMin: Int): LocalTime?
```

Returnerar `start` plus `rastMin` plus `normaltidMin` minuter. Om resultatet hamnar efter 23:59 samma dag (det vill säga passerar midnatt) returneras `null`. Negativa `rastMin` eller `normaltidMin` räknas som 0.

Räkna i minuter från midnatt: `val m = start.toSecondOfDay() / 60 + rast + normaltid`. Är `m >= 24 * 60` returneras `null`, annars `LocalTime.of(m / 60, m % 60)`. Sekunder i `start` ignoreras.

## Exempel
- 08:00, rast 30, normaltid 480 → 16:30
- 07:15, rast 45, normaltid 480 → 16:00
- 09:00, rast 0, normaltid 0 → 09:00
- 08:00, rast -10, normaltid 480 → 16:00
- 20:00, rast 30, normaltid 240 → 00:30 passerar midnatt → null
- 15:30, rast 30, normaltid 480 → null
- 08:00:45, rast 30, normaltid 480 → 16:30

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*GaHemTidTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
