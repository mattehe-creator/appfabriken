# Uppgift: Historik CSV

uppdrag: uppdrag/2026-10-19-rostfangst-minsta-version.md
försök: 1
beror-på: cursor:r-stf-ngst-modul-344d

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/export/HistorikCsv.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/export/HistorikCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/data/FangstRad.kt

## Uppgift
Implementera export av historik som CSV med rubrikrad och kommatecken som avgränsare.
Citera fält som innehåller kommatecken eller radbrytning med dubbla citattecken enligt CSV-konvention.

## Exempel
- En rad med titel `Hej, världen` ska ha citattecken runt fältet.
- Rubrikrad ska innehålla minst tid, typ och titel.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*HistorikCsvTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-25 av Cursor (PR #720). Ersatt av 2026-10-19-05-historik-csv-f2.md
