# Uppgift: Test av månadens CSV-filnamn (Flextid)

uppdrag: uppdrag/claude/2026-10-26-testtackning-dag.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/export/ManadsCsvFilnamnTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/export/ManadsCsv.kt (funktionen `manadsCsvFilnamn`)

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) i paketet `se.tmconnect.flextid.export` för `manadsCsvFilnamn(arManad: YearMonth): String`. Månaden skrivs alltid med två siffror. Importera `java.time.YearMonth`.

## Exempel
- YearMonth.of(2026, 3) ger "flextid-2026-03.csv".
- YearMonth.of(2026, 12) ger "flextid-2026-12.csv".
- YearMonth.of(2027, 1) ger "flextid-2027-01.csv".

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ManadsCsvFilnamnTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
- Ändra databasschemat.
