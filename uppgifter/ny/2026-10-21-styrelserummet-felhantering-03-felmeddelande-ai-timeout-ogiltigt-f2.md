# Uppgift: Felmeddelande för AI-timeout och ogiltigt AI-svar

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-02-felmeddelande-storlek-typ-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Varken `ServerFel.AiTimeout` / `ServerFel.AiOgiltigtSvar` eller meddelanden/tester lades till.
- `gradle :styrelserummet-server:test --tests "*FelHanteringTest"` kör befintliga tester men verifierar inte uppgiftens AI-exempel.

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

Utöka `felTillMeddelande` med svenska, styrelsevänliga texter (inga HTTP-koder, modellnamn, «Exception» eller stacktrace). Meddelandena ska:
- vid **timeout** uppmuntra användaren att försöka igen om en stund (tjänsten hann inte svara);
- vid **ogiltigt svar** förklara att komponentförslagen inte kunde tas fram och att användaren kan kontrollera underhållsplanen och försöka igen.

Uppdatera `loggJobbFel` så `when (fel)` fortfarande är uttömmande (korta koder `AiTimeout` respektive `AiOgiltigtSvar` i loggraden).

Lägg till **två** nya JUnit 4-tester i `FelHanteringTest` som assertar innehåll mot exemplen nedan (inte bara att strängen är icke-tom).

## Exempel (ska finnas som assert i tester)
- `felTillMeddelande(ServerFel.AiTimeout)` → innehåller att tjänsten inte hann svara (eller liknande) **och** uppmaning att försöka igen; **inte** «504», «HTTP», modellnamn eller «Exception».
- `felTillMeddelande(ServerFel.AiOgiltigtSvar)` → innehåller att komponentförslagen inte kunde tas fram (eller liknande) **och** underhållsplan/kontroll + försök igen; **inte** tekniska HTTP-detaljer.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Lägga filer i repots rot.
