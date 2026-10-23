beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- PR #615 ändrade i praktiken bara uppgiftsflytt (ny → pagar); `ExempelPdf.kt` fick inga exportfunktioner och `ExempelPdfExportTest.kt` saknades — samma fel som försök 1 (MALL: Cursor tar över efter underkänt försök 2).
- Efter Cursors implementation: endast de två tillåtna filerna under `styrelserummet/…/exempel/` plus uppgiftsflytt till `klar/` och denna granskning.
- `exempelUnderhallsplanPdf()` och `exempelArsredovisningPdf()` anropar `skrivPdf` med respektive sidlistor enligt uppgiften.
- `ExempelPdfExportTest.exportPdfInnehallerKarninnehall` använder JUnit 4 och assertar icke-tom plan-PDF, `Kostnad`, `27 000 000` eller `5 000 000`, samt `Byggnader och mark` i årsredovisnings-PDF via `PdfLasare`.
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- Ingen `FRAGA.md` på grenen.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Implementerade exportfunktionerna i `ExempelPdf.kt` och hela `ExempelPdfExportTest.kt` (lokal modell levererade ingen kod).

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
