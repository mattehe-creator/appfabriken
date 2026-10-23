# Uppgift: Skriv PDF från exempeltexter

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-08-texter-stamav-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdf.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdfTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTabellTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt

## Uppgift
`fun skrivPdf(sidor: List<String>): ByteArray` med PDFBox 3:

- A4, en PDF-sida per element i `sidor`
- Standardtypsnitt (Helvetica eller motsvarande Standard14)
- Markerbar text (inte raster)
- Bryt långa rader vid **90 tecken** (mjuk brytning på mellanslag där det går)

Test `skrivPdfKanLasasTillbaka`:

- `val bytes = skrivPdf(underhallsplanSidor())`
- `PdfLasare().lasSomText(bytes).getOrThrow()` innehåller `Brf Exempelgården`, `Yttertak` och `Balkonger`
- antalet sidor i PDF (via PDFBox `PDDocument.load`) == `underhallsplanSidor().size`

Test `skrivPdfBryterLangRad`: en sida med en rad på 120 tecken ger minst två rader text vid utläsning (innehåller båda delarna).

## Exempel
- `skrivPdf(listOf("Hej"))` → giltig PDF, minst några hundra byte.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelPdfTest"

## Gör inte
- Lägga till nya beroenden (PDFBox finns i `:styrelserummet`).
- Spara filer på disk i testet (bara minne).
