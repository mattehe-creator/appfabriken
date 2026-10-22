# Uppgift: helgdagar 2025–2030 mot tabell

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-04-narmaste-arbetsdag.md

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarTabellTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt

## Uppgift
Testa att `arSvenskHelgdag` stämmer för **midsommarafton** och **midsommardagen** åren 2025–2030 enligt tabellen (datum från lag 1989:253 om midsommar + afton dagen före):

| År | Midsommarafton | Midsommardagen |
|---|---|---|
| 2025 | 2025-06-20 | 2025-06-21 |
| 2026 | 2026-06-19 | 2026-06-20 |
| 2027 | 2027-06-25 | 2027-06-26 |
| 2028 | 2028-06-23 | 2028-06-24 |
| 2029 | 2029-06-22 | 2029-06-23 |
| 2030 | 2030-06-21 | 2030-06-22 |

Lägg också tester: `arSvenskHelgdag` true för 2026-04-03, 2026-04-05, 2026-04-06, 2026-05-14; false för 2026-04-07 och 2026-05-24 (pingst, söndag).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HelgdagarTabellTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-22 av Cursor (PR #578). Ersatt av 2026-10-22-styrelserummet-stamma-05-helgdag-tabell-f2.md
