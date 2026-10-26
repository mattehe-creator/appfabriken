# Uppgift: Test av filnamn för Husets historik (Husloggen)

uppdrag: uppdrag/claude/2026-10-26-testtackning-dag.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/HusHistorikFilnamnTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/HusHistorikFilnamn.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) i paketet `se.tmconnect.husloggen.export` för `husHistorikPdfFilnamn(datum: LocalDate)` och `husHistorikCsvFilnamn(datum: LocalDate)`. Anropa alltid med ett uttryckligt datum, aldrig standardvärdet. Importera `java.time.LocalDate`.

## Exempel
- LocalDate.of(2026, 3, 5): PDF "husets-historik-2026-03-05.pdf", CSV "husets-historik-2026-03-05.csv".
- LocalDate.of(2026, 12, 31): PDF "husets-historik-2026-12-31.pdf", CSV "husets-historik-2026-12-31.csv".

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusHistorikFilnamnTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
- Ändra databasschemat.
