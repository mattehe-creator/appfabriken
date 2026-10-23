# Uppgift: Exempelåtgärder tvättstuga, mark, entré, garage

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-03-atgarder-el-varme-ventilation-hiss-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp3Test.kt (ny)

## Uppgift
Lägg till fyra åtgärder:

| namn | år | kostnad |
|---|---|---|
| Tvättstuga | 2033 | 1_200_000 |
| Mark och gård | 2035 | 1_000_000 |
| Entréparti | 2037 | 800_000 |
| Garageport | 2039 | 500_000 |

Test `grupp3TackerTvattMarkEntreGarage`: namn, år och kostnad enligt tabellen; listan har nu 12 åtgärder.

## Exempel
- Efter grupp 3: summan av alla kostnader i listan → `26_800_000` (grupp 3 = 3_500_000).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelDataAtgarderGrupp3Test"

## Gör inte
- Lägga till balkong eller byggnadsvärde ännu.

utfall: underkänd 2026-10-23 av Cursor (PR #600). Ersatt av 2026-10-23-styrelserummet-exempel-04-atgarder-tvatt-mark-entre-garage-f2.md
