# Uppgift: Skriv PDF från exempeltexter

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-08-texter-stamav-f2.md

## Vad som saknades
- `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdf.kt` skapades inte.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdfTest.kt` skapades inte.
- Committen «Uppgift startad» flyttade bara uppgiftsfilen utan Kotlin-kod.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdf.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdfTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTabellTest.kt (PDFBox: `PDDocument`, `PDPage`, `PDPageContentStream`, `PDType1Font(Standard14Fonts.FontName.HELVETICA)`, `showText` + `newLine` — markerbar text)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt (`underhallsplanSidor()`)

## Uppgift
Implementera **`fun skrivPdf(sidor: List<String>): ByteArray`** i `ExempelPdf.kt` med PDFBox 3:

- **A4** (`PDRectangle.A4`), **en PDF-sida per** element i `sidor`.
- **Standardtypsnitt** (t.ex. `PDType1Font(Standard14Fonts.FontName.HELVETICA)`), inte raster/bild — text ska gå att markera (`PdfLasare` ska hitta innehåll).
- Varje sidsträng kan innehålla `\n`. Skriv rad för rad med `showText` + `newLine` (eller motsvarande), med startposition inom sidmarginal (t.ex. x≈50, y≈700 som i förebilden).
- **Radbrytning vid 90 tecken:** för varje **logisk rad** (mellan `\n` i indata), om raden är längre än 90 tecken, dela upp i flera utskrivna rader. Bryt **mjuk** på sista mellanslag före tecken 90 om möjligt; annars hård bryt vid 90. Varje delrad skrivs som egen PDF-rad.

Returnera PDF-bytes via `ByteArrayOutputStream` + `PDDocument.save` (ingen fil på disk).

## Exempel
- `skrivPdf(listOf("Hej"))` → giltig PDF, minst några hundra byte, en sida.
- `skrivPdf(underhallsplanSidor())` → antal sidor = `underhallsplanSidor().size`, läsbar text innehåller `Brf Exempelgården`, `Yttertak`, `Balkonger`.

## Test som ska gå igenom
Skriv `ExempelPdfTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert`), **inte** `kotlin.test`.

**`skrivPdfKanLasasTillbaka`:**
- `val bytes = skrivPdf(underhallsplanSidor())`
- `PdfLasare().lasSomText(bytes).getOrThrow()` innehåller `Brf Exempelgården`, `Yttertak` och `Balkonger`
- `Loader.loadPDF(bytes).use { it.numberOfPages }` == `underhallsplanSidor().size` (importera `org.apache.pdfbox.Loader`)

**`skrivPdfBryterLangRad`:**
- Bygg en rad på **120** tecken (t.ex. `"a".repeat(120)` eller 120 bokstäver utan mellanslag), `skrivPdf(listOf(rad))`
- `PdfLasare().lasSomText(...).getOrThrow()` ska innehålla **båda** delarna (minst två rader text efter brytning vid 90) — t.ex. första 90 tecken och resten som separata delsträngar.

gradle :styrelserummet:testDebugUnitTest --tests "*ExempelPdfTest"

## Gör inte
- Lägga till nya beroenden (PDFBox finns i `:styrelserummet`).
- Spara filer på disk i testet (bara minne).
- Committa enbart uppgiftsflytt utan kod.
- Ändra andra filer än de två listade.
