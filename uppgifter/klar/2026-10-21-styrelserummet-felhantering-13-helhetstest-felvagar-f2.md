# Uppgift: Helhetstest för AI-felvägar

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-11-koppla-loggning-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). `ApiTest.kt` ändrades inte.
- Inga testmetoder med `helhet` och `Fel` i namnet; gradle-filtret `*ApiTest*helhet*Fel*` matchar inget.
- Timeout-, ogiltigt-JSON- och tomt-svar-lägen via HTTP GET testades inte.

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt (`skapaHamtaCsvRadera`, `helhetFlodeMedTreKomponenterOchMaskning`, hjälparen `pdfMedText`)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbKoTest.kt (`aiTimeoutGerAiTimeoutFel`, `ogiltigtAiSvarGerAiOgiltigtSvarFel` — förväntade `felTillMeddelande`)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt (`felTillMeddelande`)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FalskTolkare.kt (`SvarLage.TIMEOUT`, `OGILTIGT_JSON`, `TOMT`)

## Uppgift
Lägg till **tre** tester (eller ett `@ParameterizedTest` med tre rader — JUnit 4 har inget inbyggt parametriserat test utan extra bibliotek, så **tre `@Test`-metoder** är enklast) i klassen `ApiTest`.

Varje test ska ha **`helhet` och `Fel` i metodnamnet**, t.ex.:
- `helhetFelAiTimeout()`
- `helhetFelOgiltigtJson()`
- `helhetFelTomtSvar()`

Följ samma mönster som `skapaHamtaCsvRadera` / `helhetFlodeMedTreKomponenterOchMaskning`:
`createTempDirectory`, `testApplication`, `module(ServerMiljo(..., synkronJobb = true))`, `createClient`.

1. **Falsk tolkare:** `FalskTolkare(lage = SvarLage.TIMEOUT)` respektive `OGILTIGT_JSON` respektive `TOMT`.

2. **PDF med hemlig klienttext:** Använd `pdfMedText("HEMLIG_KLIENTTEXT_12345 Underhall tak")` för plan och en enkel årsredovisnings-PDF (t.ex. `pdfMedText("Byggnader och mark 500 000")`) — samma multipart-POST som befintliga tester (`forening`, `arsslut`, `filename=plan.pdf`, `filename=arsredovisning.pdf`).

3. **POST /api/jobb** → hämta `id` från JSON.

4. **GET /api/jobb/{id}:** Parse JSON; assert:
   - `meta.status == "FEL"`
   - `meta.felmeddelande == felTillMeddelande(ServerFel.AiTimeout)` för timeout-läget
   - `meta.felmeddelande == felTillMeddelande(ServerFel.AiOgiltigtSvar)` för ogiltigt JSON **och** tomt svar

5. **Inga tekniska läckor i GET-svarstexten** (`val statusBody = s.bodyAsText()`):
   - `assertFalse(statusBody.contains("Exception"))`
   - `assertFalse(statusBody.contains("at se.tmconnect"))`
   - `assertFalse(statusBody.contains("HEMLIG_KLIENTTEXT_12345"))`

6. (Valfritt men bra) Om du fångar serverlogg via `loggSkriv` i `ServerMiljo`/`JobbKo`: assert att loggraden **inte** innehåller `HEMLIG_KLIENTTEXT_12345`. Annars räcker steg 5.

Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`) — **inte** kotlin.test.

## Exempel
- `SvarLage.TIMEOUT` → GET-body ungefär `{ "meta": { "status": "FEL", "felmeddelande": "Tjänsten hann inte svara i tid. Försök igen om en liten stund." } }` utan stacktrace eller PDF-text.
- `SvarLage.OGILTIGT_JSON` → `FEL` och `felmeddelande` som `felTillMeddelande(ServerFel.AiOgiltigtSvar)` (`"Komponentförslagen kunde inte tas fram..."`).
- `SvarLage.TOMT` → samma användarmeddelande som ogiltigt JSON.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*ApiTest*helhet*Fel*"

## Gör inte
- Ändra produktionskod.
- Ändra andra filer än `ApiTest.kt`.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (helhetstester i ApiTest.kt; timeout utan parsbart byggnadsvärde i årsredovisning)
