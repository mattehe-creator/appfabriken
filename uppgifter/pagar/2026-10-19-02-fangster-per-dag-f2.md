# Uppgift: Fångster per dag

## Vad som saknades
- Bygget eller testerna är röda. Kör testet i uppgiften och rätta tills det är grönt. Testerna i appmodulerna använder JUnit 4: import org.junit.Test och import org.junit.Assert.assertEquals (eller assertTrue, assertFalse), inte kotlin.test.

uppdrag: uppdrag/2026-10-19-rostfangst-minsta-version.md
försök: 2
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
