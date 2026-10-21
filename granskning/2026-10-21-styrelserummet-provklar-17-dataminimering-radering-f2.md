beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade: `DataminimeringTest.kt` (plus flytt av uppgiftsfil `ny` → `pagar` på grenen). `JobbLagring.kt` oförändrad — korrekt eftersom befintlig `raderaJobb` redan tar bort hela katalogen.
- Koden gör det uppgiften säger: skapa jobb till KLAR, DELETE med 204, assert att `tmp/jobb/{id}` inte finns.
- Testet jämför mot exemplen (`Files.notExists`/ekvivalent via `!Files.exists(jobbKatalog)`).
- Inga nya beroenden, behörigheter eller nätverksanrop utöver befintlig testmiljö.
- Inga hårdkodade UI-strängar (servertest, ingen Compose).
- JUnit 4 (`org.junit.Assert`, `@Test`), inte `kotlin.test`.
- `:styrelserummet-server:test` och `--tests "*DataminimeringTest.efterRadera*"` gröna lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
