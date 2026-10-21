# Uppgift: Syntetisk årsredovisning balansräkning

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskArsredovisningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt

## Uppgift
Test `arsredovisningBalansTvaKolumner`:

1. PDFBox-PDF med **balansräkning**, två kolumner (**innevarande år först**), t.ex.:
   ```
   Byggnader och mark
   2025 2024
   12 500 000 11 800 000
   Byggnadsår 1975
   ```
2. Enkel plan-PDF eller plan-text; stub `Tolkare` med komponenter som summerar till **12 500 000** (första kolumnens belopp).
3. `byggUtkast` → `utkast.byggnadensVarde == 12_500_000L`, avstämning stämmer, komponentantal enligt stub.

## Exempel
- Första kolumn 12 500 000, komponenter 7_500_000 + 5_000_000 → `byggnadensVarde` 12_500_000, `stammer`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskArsredovisningTest"

## Gör inte
- Ändra `HittaByggnadensVarde.kt` om testet går grönt.
- Riktiga årsredovisningar.

utfall: underkänd 2026-10-21 av Cursor (PR #428). Ersatt av 2026-10-21-styrelserummet-provklar-11-flode-arsredovisning-balans-f2.md
