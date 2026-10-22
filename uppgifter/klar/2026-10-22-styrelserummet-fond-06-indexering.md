# Uppgift: Prisuppräkning av åtgärderna

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondIndexering.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondIndexeringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt

## Uppgift
Underhållsplaner anger ofta belopp i dagens prisnivå. Användaren ska kunna räkna upp dem med en årlig prisökning.

```kotlin
fun indexeratBelopp(belopp: Long, ar: Int, basAr: Int, okningTiondelsProcent: Int): Long
fun indexeradeAtgarder(atgarder: List<PlaneradAtgard>, basAr: Int, okningTiondelsProcent: Int): List<PlaneradAtgard>
```

- `okningTiondelsProcent`: 20 betyder 2,0 % per år. Tillåtet 0..200, annars `IllegalArgumentException`.
- `indexeratBelopp`: för år efter `basAr` räknas beloppet upp ett år i taget, och avrundas till hel krona varje år: `v = (v * (1000 + t) * 2 + 1000) / 2000`. År lika med eller före `basAr` ger beloppet oförändrat.
- `indexeradeAtgarder`: samma lista med `belopp` uppräknat för respektive `ar`; `ar` och `beskrivning` oförändrade.
- Inga flyttal.

## Exempel
- (100 000, 2028, 2026, 20) → 104 040
- (600 000, 2030, 2026, 20) → 649 460
- (100 000, 2026, 2026, 20) → 100 000
- (100 000, 2036, 2026, 25) → 128 010
- (100 000, 2025, 2026, 20) → 100 000
- okning 201 → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondIndexeringTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.

utfall: godkänd 2026-10-22 (JUnit-import och indexeradeAtgarder-tester)
