# Uppgift: midsommarafton som helgdag

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-02-helgdag-roda-dagar.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarMidsommarTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md (avsnitt Osäkert om midsommarafton)

## Uppgift
Utöka `arSvenskHelgdag` så att **midsommarafton** (dagen före midsommardagen) också ger true.

## Exempel
- 2026-06-19 → true
- 2026-06-20 → true (midsommardagen)
- 2025-06-20 → true (midsommarafton 2025)
- 2025-06-21 → true (midsommardagen 2025)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HelgdagarMidsommarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-22 av Cursor (PR #570). Ersatt av 2026-10-22-styrelserummet-stamma-03-midsommarafton-f2.md
