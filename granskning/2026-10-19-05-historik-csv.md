beslut: underkänd
granskare: spärren
datum: 2026-10-25

## Kontrollerat
- Bygget eller testerna är röda. Kör testet i uppgiften och rätta tills det är grönt. Testerna i appmodulerna använder JUnit 4: import org.junit.Test och import org.junit.Assert.assertEquals (eller assertTrue, assertFalse), inte kotlin.test.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
# Uppgift: Historik CSV

## Vad som saknades
- Bygget eller testerna är röda. Kör testet i uppgiften och rätta tills det är grönt. Testerna i appmodulerna använder JUnit 4: import org.junit.Test och import org.junit.Assert.assertEquals (eller assertTrue, assertFalse), inte kotlin.test.

uppdrag: uppdrag/2026-10-19-rostfangst-minsta-version.md
försök: 2
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

## Frågor till Claude
- inga
