# Uppgift: Färdiga exempel-PDF:er i minnet

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-09-pdf-skriv-f2.md

## Vad som saknades
- `ExempelPdf.kt` fick inga nya exportfunktioner.
- `ExempelPdfExportTest.kt` committades tom (0 byte) utan klass eller test.
- Committen «Lokal modell … (okommittade ändringar efter Aider)» innehöll ingen Kotlin-implementation.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdf.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdfExportTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelPdfTest.kt (`PdfLasare`, JUnit 4)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt (`underhallsplanSidor()`, `arsredovisningSidor()`)

## Uppgift
Lägg till i `ExempelPdf.kt` (samma paket som `skrivPdf`, inga extra imports om funktionerna redan finns i paketet):

- `fun exempelUnderhallsplanPdf(): ByteArray = skrivPdf(underhallsplanSidor())`
- `fun exempelArsredovisningPdf(): ByteArray = skrivPdf(arsredovisningSidor())`

Skriv testklassen `ExempelPdfExportTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert`), **inte** `kotlin.test`.

Test **`exportPdfInnehallerKarninnehall`**:

- `exempelUnderhallsplanPdf().isNotEmpty()` ska vara true.
- Plan-PDF-text via `PdfLasare().lasSomText(exempelUnderhallsplanPdf()).getOrThrow()` ska innehålla tabellkolumnen `Kostnad` och antingen totalsumman `27 000 000` eller delsumman `5 000 000`.
- Årsredovisnings-PDF-text via `PdfLasare().lasSomText(exempelArsredovisningPdf()).getOrThrow()` ska innehålla `Byggnader och mark`.

## Exempel
- `exempelUnderhallsplanPdf().isNotEmpty()` → true

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelPdfExportTest"

## Gör inte
- Skriva till filsystem (nästa uppgift).
- Lägga till beroenden.
- Committa enbart uppgiftsflytt eller tom testfil utan kod.
- Ändra andra filer än de två listade.
