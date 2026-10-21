# Uppgift: Syntetisk underhållsplan löpande text med tkr

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar-forsta-korning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTkrTextTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt

## Uppgift
Test `planLopandeTextTkrGerUtkast`:

1. PDFBox-PDF med **löpande brödtext** (ingen tabell): t.ex. "Takbyte planeras 2030, uppskattad kostnad 500 tkr. Stomrenovering 2040, 1 200 tkr."
2. `PdfLasare` → text → stubbad `Tolkare` med JSON (TAK 500_000 kr, STOMME 1_200_000 kr).
3. Årsredovisning: `Byggnader och mark 1 700 tkr` eller motsvarande kronor så avstämning stämmer.
4. `byggUtkast` → assert komponentantal, avstämning, `sakerhet` per rad, anmärkningar för LAG/UPPSKATTNING om stubben sätter det.

## Exempel
- 500 tkr + 1 200 tkr = 1 700 tkr byggnad → `stammer == true`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskPlanTkrTextTest"

## Gör inte
- Ändra produktionskod om testerna går grönt.
- Riktiga handlingar i repot.
