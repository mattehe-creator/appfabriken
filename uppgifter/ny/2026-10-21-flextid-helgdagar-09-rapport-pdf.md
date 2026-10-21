# Uppgift: Månadens normaltid i PDF:en

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: 2026-10-21-flextid-helgdagar-08-rapport-texter.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/export/ManadsPdf.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsRapport.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/export/ManadsRapportTexter.kt

## Uppgift
I `ManadsPdf.kt`, där raden med `texter.saldoManadsslutEtikett` skrivs: skriv en rad till direkt **före** den, på samma sätt som de andra summaraderna (samma typsnitt och radavstånd):

`"${normaltidManadEtikett(context, rapport.arbetsdagarManad)}: ${rapport.normaltidManad}"`

Om filen inte har tillgång till en `Context` där raden skrivs: använd `texter.normaltidManadEtikett` om den inte är tom, med `" (${rapport.arbetsdagarManad})"` efter, och hoppa över raden om etiketten är tom. Välj det som går utan att ändra någon funktionssignatur.

## Exempel
- April 2026, normaltid 480 → raden "Normaltid i månaden (20 arbetsdagar): 160:00" före saldoraden (formatet på tiden kommer från `formateraMinuterSomTid`).

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Ändra sidans storlek, marginaler eller andra rader.
- Lägga till beroenden.
