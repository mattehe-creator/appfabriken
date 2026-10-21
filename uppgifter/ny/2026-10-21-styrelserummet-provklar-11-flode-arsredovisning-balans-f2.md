# Uppgift: Syntetisk årsredovisning balansräkning

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 2
beror-på: ingen

## Vad som saknades
- `SyntetiskArsredovisningTest.kt` skapades men lämnades helt tom — inget test `arsredovisningBalansTvaKolumner`, inga assert mot exemplen i uppgiften.
- Bygget blev grönt eftersom filen inte innehåller någon testklass; det räknas inte som klart.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskArsredovisningTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTabellTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt

## Uppgift
Implementera klassen `SyntetiskArsredovisningTest` med testmetoden `arsredovisningBalansTvaKolumner`:

1. **Årsredovisning som PDF (PDFBox)** med balansräkning i **två kolumner**, innevarande år först. Skriv minst dessa rader i PDF:en (radbrytningar som i förebilderna):
   ```
   Balansräkning
   Byggnader och mark
   2025 2024
   12 500 000 11 800 000
   Byggnadsår 1975
   ```
   Använd samma PDFBox-mönster som `SyntetiskPlanTabellTest.skapaPdfMedTabell` / `PdfLasareTest.pdfMedText` (PDDocument, PDPage, PDPageContentStream, Helvetica 12f, `newLine()` mellan rader).

2. `val arText = PdfLasare().lasSomText(arPdfBytes).getOrThrow()` så att flödet går via PDF-läsning (som i övriga syntetiska flödestester).

3. **Plan**: enkel plan-text räcker, t.ex. `val planText = "Tak och stomme enligt underhållsplan."` (plan-PDF är valfritt).

4. **Stub `Tolkare`** (mönster från `ByggUtkastTest.fakeTolkare` / `SyntetiskPlanTabellTest`) med **två** komponenter vars `anskaffningsvarde` summerar till **12_500_000**, t.ex. **7_500_000** + **5_000_000**. Ange `grupp`, `anskaffningsar`, `nyttjandeperiodAr`, `kalla`, `sakerhet` som i övriga JSON-stubs.

5. `val utkast = byggUtkast(planText, arText, Forening("Testföreningen BRF", 1975), tolkare, nuAr = 2025)`.

6. **Assert** (JUnit 4, `org.junit.Assert`):
   - `utkast.byggnadensVarde == 12_500_000L` (första kolumnens belopp, inte 11_800_000)
   - `utkast.avstamning!!.stammer == true`
   - `utkast.avstamning!!.summa == 12_500_000L`
   - `utkast.komponenter.size == 2`
   - valfritt men bra: `utkast.arsredovisning.byggnadsar == 1975`

Använd **inte** `kotlin.test` eller `assertTrue(true)`.

## Exempel
- Första kolumn 12 500 000, komponenter 7_500_000 + 5_000_000 → `byggnadensVarde` 12_500_000, `stammer`, två komponenter.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskArsredovisningTest"

## Gör inte
- Tom fil eller placeholder-test.
- Skapa filer i repots rot.
- Ändra `HittaByggnadensVarde.kt` eller annan produktionskod om testet kan göras grönt med befintlig logik (se `HittaByggnadensVardeTest.hittaByggnadensVardeTvaKolumner`).
- Lägga till beroenden.
