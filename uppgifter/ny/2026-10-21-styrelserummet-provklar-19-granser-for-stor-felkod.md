# Uppgift: Felkod FOR_STOR_HANDLING

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar-forsta-korning.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-18-granser-skatta-tecken.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felkoder.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felmeddelanden.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelmeddelandenTest.kt

## Uppgift
1. Lägg till `FOR_STOR_HANDLING` i `Felkoder` (och i `allaKoder()`).
2. `Felmeddelanden.forklara(Felkoder.FOR_STOR_HANDLING)` — vänligt svenskt meddelande utan tekniska tal, ungefär: handlingen är för stor för automatisk tolkning, prova korta utdrag eller färre sidor.
3. Test: meddelandet är icke-tomt, på svenska, nämner inte API eller teckengräns exakt.

## Exempel
- `forklara("FOR_STOR_HANDLING")` innehåller "stor" eller "tolkning" (case-insensitive).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelmeddelandenTest.forStorHandlingMeddelande"

## Gör inte
- Ändra JobbKo ännu.
