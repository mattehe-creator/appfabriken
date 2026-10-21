beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåten testfil plus förväntad flytt av uppgiftsfil till `pagar/` (lokal modell); felaktig fil i repots rot borttagen.
- Ingen produktionskod, inga nya beroenden, behörigheter eller nätverk.
- Testet följer uppgiften: PDFBox-PDF med löpande text → `PdfLasare` → stubbad `Tolkare` → `byggUtkast` med tkr-årsredovisning; assert på komponentantal, avstämning (1 700 000 kr, `stammer`), `sakerhet` och inga differens-anmärkningar.
- JUnit 4 (`org.junit`), inga hårdkodade UI-strängar.
- `:styrelserummet:test --tests "*SyntetiskPlanTkrTextTest"` grönt lokalt.

## Rättat av Cursor
- Flyttade testet från `SyntetiskPlanTkrTextTest.kt` i repots rot till `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTkrTextTest.kt` (korrekt sökväg var tom).
- Rättade ogiltig Kotlin i `pdfMedText` (imports enligt `PdfLasareTest`), tog bort felaktig `createPdfWithText` som anropade `lasPerSida` på PDF-bytes, lade assert att extraherad text innehåller tkr-beloppen.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
