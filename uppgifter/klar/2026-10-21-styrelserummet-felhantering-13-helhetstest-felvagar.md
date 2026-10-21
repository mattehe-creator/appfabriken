# Uppgift: Helhetstest för AI-felvägar

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-11-koppla-loggning-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Uppgift
Lägg till tre tester (eller ett parametriserat) i `ApiTest` med `synkronJobb = true` och `FalskTolkare` i respektive fel-läge:

| Läge | Förväntat efter GET |
|------|---------------------|
| Timeout | `meta.status == "FEL"`, `felmeddelande` som vid `AiTimeout`, inget stacktrace i JSON-svaret |
| Ogiltigt JSON | `FEL`, meddelande som vid `AiOgiltigtSvar` |
| Tomt svar | `FEL`, meddelande som vid `AiOgiltigtSvar` |

PDF ska innehålla unik hemlig text (t.ex. `"HEMLIG_KLIENTTEXT_12345"`) så att testet kan verifiera att varken GET-svar eller inspelad logg (om testet fångar logg-callback) innehåller den strängen.

Assert också att svaret inte innehåller `"Exception"`, `"at se.tmconnect"` eller PDF-texten.

## Exempel
- Timeout-läge → `{ "meta": { "status": "FEL", "felmeddelande": "…" } }` utan tekniska detaljer.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest*helhet*Fel*"

## Gör inte
- Ändra produktionskod.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #407). Ersatt av 2026-10-21-styrelserummet-felhantering-13-helhetstest-felvagar-f2.md
