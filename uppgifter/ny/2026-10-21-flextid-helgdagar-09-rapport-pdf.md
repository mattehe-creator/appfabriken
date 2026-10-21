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
`skrivManadsPdf` har ingen `Context`; etiketten kommer färdig i `texter.normaltidManadEtikett` (Claude fyller i den där rapporten skapas).

I `ManadsPdf.kt`, där raden med `texter.saldoManadsslutEtikett` skrivs: skriv en rad till direkt **före** den, på samma sätt som de andra summaraderna (samma typsnitt och radavstånd), men bara om `texter.normaltidManadEtikett` inte är tom:

`"${texter.normaltidManadEtikett}: ${rapport.normaltidManad}"`

## Exempel
- Etikett "Normaltid i månaden (20 arbetsdagar)", normaltid "160:00" → raden "Normaltid i månaden (20 arbetsdagar): 160:00" före saldoraden.
- Tom etikett → ingen ny rad.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Ändra sidans storlek, marginaler eller andra rader.
- Lägga till beroenden.
