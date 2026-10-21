# Uppgift: Felmeddelande för AI-timeout och ogiltigt AI-svar

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: 2026-10-21-styrelserummet-felhantering-02-felmeddelande-storlek-typ-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/tolking/Tolkare.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/tolking/AnthropicTolkare.kt

## Uppgift
Utöka `ServerFel` med:
- `AiTimeout` — AI-anropet avbröts eller svarade inte i tid
- `AiOgiltigtSvar` — HTTP-fel, tomt svar eller JSON som inte går att tolka

Utöka `felTillMeddelande`. Meddelandena ska uppmuntra användaren att försöka igen vid timeout, och vid ogiltigt svar förklara att tolkningen misslyckades utan tekniska detaljer (inga HTTP-koder eller modellnamn).

## Exempel
- `felTillMeddelande(ServerFel.AiTimeout)` → t.ex. att tjänsten inte hann svara; försök igen om en stund.
- `felTillMeddelande(ServerFel.AiOgiltigtSvar)` → t.ex. att komponentförslagen inte kunde tas fram; kontrollera underhållsplanen och försök igen.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #376). Ersatt av 2026-10-21-styrelserummet-felhantering-03-felmeddelande-ai-timeout-ogiltigt-f2.md
