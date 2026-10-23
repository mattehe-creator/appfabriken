# Uppgift: Exempelåtgärder tak, fasad, fönster, stammar

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-01-forening.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataAtgarderGrupp1Test.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Utöka `EXEMPEL_ATGARDER` i `ExempelData.kt` med exakt dessa fyra rader (kostnad i hela kronor):

| namn | år | kostnad |
|---|---|---|
| Yttertak | 2032 | 5_000_000 |
| Fasadputs | 2036 | 4_000_000 |
| Fönster | 2028 | 3_500_000 |
| Stammar vatten och avlopp | 2030 | 3_000_000 |

Test `grupp1TackerTakFasadFonsterStammar`: listan har minst dessa fyra namn, alla har icke-null kostnad, årtalen stämmer.

## Exempel
- Rad "Yttertak" → `kostnadKronor == 5_000_000L`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelDataAtgarderGrupp1Test"

## Gör inte
- Lägga till andra åtgärder än de fyra.
- Ändra föreningskonstanterna.

utfall: underkänd 2026-10-23 av Cursor (PR #594). Ersatt av 2026-10-23-styrelserummet-exempel-02-atgarder-tak-fasad-fonster-stammar-f2.md
