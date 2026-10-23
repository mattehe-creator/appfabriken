# Uppgift: Färdiga exempel-PDF:er i minnet

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-09-pdf-skriv-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdf.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdfExportTest.kt (ny)

## Uppgift
Lägg till:

- `fun exempelUnderhallsplanPdf(): ByteArray = skrivPdf(underhallsplanSidor())`
- `fun exempelArsredovisningPdf(): ByteArray = skrivPdf(arsredovisningSidor())`

Test `exportPdfInnehallerKarninnehall`:

- plan-PDF text innehåller tabellkolumn `Kostnad` och `27 000 000` eller delsumma `5 000 000`
- årsredovisnings-PDF text innehåller `Byggnader och mark`

## Exempel
- `exempelUnderhallsplanPdf().isNotEmpty()` → true

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelPdfExportTest"

## Gör inte
- Skriva till filsystem (nästa uppgift).
