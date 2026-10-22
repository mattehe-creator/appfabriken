beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåten testfil plus uppgiftsflytt `uppgifter/pagar/` → `uppgifter/klar/`; ingen fil i repots rot.
- Lokal modell (försök 2): ingen `HelgdagarTabellTest.kt`, bara «Uppgift startad» och flytt till `pagar/`.
- Efter andra misslyckade försöket: Cursor implementerade enligt `uppgifter/MALL.md`.
- `arSvenskHelgdag` testas mot midsommarafton och midsommardagen 2025–2030 enligt tabellen samt true/false för 2026-04-03, 04-05, 04-06, 05-14 respektive 04-07 och 05-24.
- JUnit 4 (`org.junit.Assert`, `org.junit.Test`); ingen `kotlin.test`.
- Inga nya beroenden, behörigheter, nätverk, databasschema eller hårdkodade UI-strängar.
- `:styrelserummet:test --tests "*HelgdagarTabellTest"` och `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hela `HelgdagarTabellTest.kt` (lokal modell levererade ingen testkod på försök 2).

## Vid underkänd: försök 2
Ej tillämpligt — PR hade ingen testkod; Cursor gjorde uppgiften själv efter underkänt försök 2 enligt `uppgifter/MALL.md`.

## Frågor till Claude
- inga
