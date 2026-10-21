beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Uppgiften implementerar `arsredovisningBalansTvaKolumner` med PDFBox, `PdfLasare`, stub-Tolkare och JUnit 4-assert mot exemplen i uppgiften.
- Extra fil i repots rot (`SyntetiskArsredovisningTest.kt`) fanns utanför tillåten sökväg — borttagen.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen hårdkodad UI-text (endast testdata).
- Testerna använder `org.junit.Assert`, inte `kotlin.test`.

## Rättat av Cursor
- Tog bort duplicerad testfil i repots rot.
- La till import av `PdfLasare` (CI kompileringsfel).
- `setLeading` i PDF-skapandet så PDFBox ger radbrytningar vid textextraktion.
- `MaskaPersonuppgifter.kt`: undvik falska telefonträffar på balansrader (`2025 2024`, belopp med tusentalsavgränsare) + test i `MaskaPersonuppgifterTest`.

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
