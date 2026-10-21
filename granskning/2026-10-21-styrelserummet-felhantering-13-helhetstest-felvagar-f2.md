beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Grenen hade bara «Uppgift startad»; `ApiTest.kt` saknade helhetstester (samma som försök 1).
- Efter Cursors implementation: endast `ApiTest.kt` ändrad (plus flytt av uppgiftsfil till `klar/`).
- Tre `@Test` med `helhet` och `Fel` i namnet; HTTP-flöde enligt uppgiften med `FalskTolkare`, multipart, GET `meta.status`/`felmeddelande` mot `felTillMeddelande`, inga läckor i body.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inga nya beroenden, behörigheter eller nätverk; ingen produktionskod.
- `:styrelserummet-server:test --tests "*ApiTest*helhet*Fel*"` och `:styrelserummet-server:test` gröna lokalt.

## Rättat av Cursor
- Hela uppgiften (försök 2 underkänt igen enligt MALL.md): `helhetFelAiTimeout`, `helhetFelOgiltigtJson`, `helhetFelTomtSvar` och hjälparen `helhetFelViaApi`.
- Timeout-testet använder årsredovisnings-PDF utan parsbart byggnadsvärde så att `JobbKo` kan klassa `AiTimeout` (med «Byggnader och mark 500 000» tillkommer avstämningsanmärkning och svaret blir `AiOgiltigtSvar`).

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
