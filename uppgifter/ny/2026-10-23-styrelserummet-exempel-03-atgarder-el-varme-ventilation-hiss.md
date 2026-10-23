# Uppgift: Exempelåtgärder el, värme, ventilation, hiss

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-02-atgarder-tak-fasad-fonster-stammar.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp2Test.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp1Test.kt

## Uppgift
Lägg till fyra åtgärder i `EXEMPEL_ATGARDER` (behåll grupp 1):

| namn | år | kostnad |
|---|---|---|
| Elstammar och elcentral | 2029 | 2_500_000 |
| Fjärrvärmecentral | 2027 | 2_000_000 |
| Ventilation FTX | 2034 | 1_800_000 |
| Hiss | 2031 | 1_500_000 |

Test `grupp2TackerElVarmeVentilationHiss`: exakt dessa namn finns, kostnader och år enligt tabellen.

## Exempel
- Summan av kostnader för grupp 1 + grupp 2 → `23_300_000` kronor (grupp 1 = 15_500_000, grupp 2 = 7_800_000).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelDataAtgarderGrupp2Test"

## Gör inte
- Ändra tidigare fyra åtgärder.
- Lägga till balkong eller mark ännu.
