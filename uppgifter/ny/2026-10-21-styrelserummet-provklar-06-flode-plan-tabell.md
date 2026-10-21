# Uppgift: Syntetisk underhållsplan tabelllayout

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar-forsta-korning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTabellTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt

## Uppgift
Test `planTabellLayoutGerAvstamdUtkast`:

1. Skapa en **påhittad** PDF i testet (PDFBox) med tabelltext: kolumner **Åtgärd**, **År**, **Kostnad** och minst två rader (t.ex. Tak 2030 2 000 000, Fasad 2035 1 500 000). Inga riktiga föreningar eller personuppgifter.
2. Extrahera text med `PdfLasare.lasSomText`.
3. Förinställd JSON (enligt tolkningsschemat i `TolkaForslag`) returneras av en lokal `Tolkare`-stub med komponenter som matchar tabellen och `sakerhet` HOG/MEDEL.
4. Årsredovisningstext (ren sträng): balans med **Byggnader och mark 3 500 000** så summan stämmer.
5. Kör `byggUtkast(planText, arText, Forening("Testföreningen BRF", 1975), tolkare, nuAr = 2025)`.
6. Assert: antal komponenter, `avstamning!!.stammer`, varje rad har förväntad `sakerhet`, och om någon komponent har `LAG` eller `UPPSKATTNING` ska `anmarkningarFor` (via utkastets anmärkningar) innehålla motsvarande varningstext.

## Exempel
- Två komponenter 2_000_000 + 1_500_000 → byggnad 3_500_000 → `stammer == true`, 2 komponenter.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskPlanTabellTest"

## Gör inte
- Ändra produktionskod om testerna redan går grönt.
- Lägga till beroenden.
- Checka in riktiga handlingar.
