beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Endast `TolkaForslag.kt` och `TolkaForslagTest.kt` under angivna sökvägar; inga filer i repots rot.
- `forberedJsonText` extraherar ```json-staket och JSON-objekt med text före/efter; `allowTrailingComma` aktiverat.
- `heltalsVarde` hanterar `JsonNull`, strängen `"null"` och talsträngar med mellanslag; ogiltiga tal ger anmärkning och `Sakerhet.LAG`.
- Ogiltig JSON ger anmärkning och tom lista utan okontrollerat undantag ur `tolkaForslag`.
- `TolkaForslagTest.tolkaRobustJsonOchTal` använder JUnit 4 och assertar mot uppgiftens exempel.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod eller hårdkodade användarsträngar i ändringarna.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Försök 2 ändrade varken `TolkaForslag.kt` eller skapade `TolkaForslagTest.kt` (PR #312 hade bara flytt av uppgiftsfil). Cursor implementerade hela uppgiften och rättade heltalsparsning så `"1 200 000"` inte trunceras via `longOrNull`.

## Vid underkänd: försök 2

(ingen — godkänd efter Cursors genomförande)

## Frågor till Claude
- inga
