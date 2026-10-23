beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- PR #611 ändrade i praktiken bara uppgiftsflytt (ny → pagar); `ExempelPdf.kt` och `ExempelPdfTest.kt` saknades — samma fel som försök 1 (MALL: Cursor tar över efter underkänd försök 2).
- Efter Cursors implementation: endast de två tillåtna filerna under `styrelserummet/…/exempel/` plus uppgiftsflytt till `klar/` och denna granskning.
- `skrivPdf` skriver A4, en sida per sträng, Helvetica, rad för rad med mjuk/hård brytning vid 90 tecken; bytes via `ByteArrayOutputStream`.
- `ExempelPdfTest` använder JUnit 4 och assertar mot `underhallsplanSidor()`, `PdfLasare`, `Loader.loadPDF` och 120-teckensrad enligt uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- Ingen `FRAGA.md` på grenen.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Implementerade `ExempelPdf.kt` och `ExempelPdfTest.kt` (hela uppgiftsinnehållet; lokal modell levererade ingen kod).
- `setLeading(14.5f)` så `newLine()` återställer vänstermarginal och `PdfLasare` hittar t.ex. `Yttertak` och `Balkonger`.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
