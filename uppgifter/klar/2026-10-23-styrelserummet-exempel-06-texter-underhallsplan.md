# Uppgift: Exempeltexter underhållsplan tre sidor

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-05-atgarder-balkong-brand-summor.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterPlanTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTvaSidorTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTkrTextTest.kt

## Uppgift
`fun underhallsplanSidor(): List<String>` returnerar **tre** sidor med markerbar text:

1. **Sida 1 — inledning:** föreningens namn, adress, organisationsnummer, byggår, kort text om att planen är påhittad för demo.
2. **Sida 2 — tabell:** rubrikraden `Åtgärd`, `År`, `Kostnad` och en rad per post i `EXEMPEL_ATGARDER`. Kostnad formateras med mellanslag som tusentalsavgränsare (`1 200 000`). Balkonger ska ha `–` eller tom kostnad enligt `SyntetiskPlanSaknadKostnadTest`.
3. **Sida 3 — fortsättning:** minst en rad med belopp uttryckt i **tkr** (t.ex. total investering) som refererar till planens summor.

Test `underhallsplanHarTreSidorOchTabell`:

- `underhallsplanSidor().size == 3`
- sida 1 innehåller `FORENING_NAMN` och `ORGANISATIONSNUMMER`
- sida 2 innehåller `Åtgärd`, `Yttertak`, `Balkonger`
- sida 3 innehåller `tkr` (gemener eller versaler)

## Exempel
- Sida 2 innehåller strängen `5 000 000` för yttertak.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelTexterPlanTest"

## Gör inte
- Implementera `arsredovisningSidor()` ännu.
- Hårdkoda andra föreningar än `ExempelData`.

utfall: underkänd 2026-10-23 av Cursor (PR #603). Ersatt av 2026-10-23-styrelserummet-exempel-06-texter-underhallsplan-f2.md
