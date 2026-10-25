# Uppgift: Fångster per dag

uppdrag: uppdrag/2026-10-19-rostfangst-minsta-version.md
försök: 1
beror-på: cursor:r-stf-ngst-modul-344d

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/FangsterPerDag.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/regler/FangsterPerDagTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/data/RostfangstRepository.kt

## Uppgift
Implementera `fun fangsterPerDag(tider: List<Instant>, idag: LocalDate, zon: ZoneId): List<Pair<LocalDate, Int>>`.
Returnera exakt 30 dagar bakåt inklusive `idag`, med antal 0 för dagar utan fångster.

## Exempel
- `idag` = 2026-10-19, zon Europe/Stockholm, en fångst 2026-10-19 10:00 och en 2026-10-18 08:00. Förväntat: 2026-10-19 → 1, 2026-10-18 → 1, övriga dagar i fönstret → 0.
- Tom lista. Förväntat: 30 par med 0.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*FangsterPerDagTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-25 av Cursor (PR #716). Ersatt av 2026-10-19-02-fangster-per-dag-f2.md
