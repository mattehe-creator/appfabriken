# Uppgift: Servern använder kärnans bokfortVarde

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-01-heltal.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/CsvExport.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/CsvExportServerTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/ApiModell.kt

## Uppgift
`CsvExport.kt` i servern har en egen privat kopia av `bokfortVarde`. Två kopior av en räkneregel kan glida isär.

1. Ta bort den privata funktionen `bokfortVarde` i `CsvExport.kt`.
2. Importera `se.tmconnect.styrelserummet.regler.bokfortVarde` och använd den på samma ställe.
3. Inget annat i filen ändras.

Nytt test `CsvExportServerTest` som anropar `tillCsv` (serverns, paketet `se.tmconnect.styrelserummet.server`) med en `KomponentDto` och kontrollerar kolumnen Bokfört värde. Läs `ApiModell.kt` för hur `KomponentDto` skapas (grupp och säkerhet är texter).

## Exempel
- 300 000 kr, anskaffad 2020, 10 år, årsslut 2025 → andra raden innehåller `;120000;`
- Samma, årsslut 2019 → `;300000;` som bokfört värde
- Samma, årsslut 2035 → `;0;`
- Sista raden är fortfarande `UTKAST_RAD` (eventuellt inom citattecken om den innehåller semikolon; den gör det inte).

## Test som ska gå igenom
gradle :styrelserummet-server:test

## Gör inte
- Ändra kolumner eller rubriker.
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #459). Ersatt av 2026-10-21-styrelserummet-plan-07-server-dubblett-f2.md
