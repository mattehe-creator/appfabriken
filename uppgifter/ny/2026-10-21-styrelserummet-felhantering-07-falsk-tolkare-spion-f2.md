# Uppgift: Utökad falsk tolkare för tester

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). `FalskTolkare.kt` utökades inte och `FalskTolkareTest.kt` skapades inte.
- Ingen `senasteUtdrag`, inga fel-lägen (timeout, ogiltigt JSON, tomt svar), inga tester enligt exemplen.

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FalskTolkare.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FalskTolkareTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/tolking/Tolkare.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Uppgift
Utöka testklassen `FalskTolkare` (implementerar `Tolkare`):

1. **Spion:** Efter varje anrop till `foreslaKomponenter` ska argumentet `underhallsplanUtdrag` sparas i en läsbar egenskap, t.ex. `var senasteUtdrag: String?` (null innan första anrop).
2. **Eget JSON:** Behåll nuvarande standard-JSON som default (så `FalskTolkare()` utan argument fortfarande fungerar i alla befintliga `ApiTest`). Låt konstruktorn ta valfri `jsonSvar: String` som idag.
3. **Fel-lägen:** Lägg till ett konfigurerbart läge (t.ex. enum `SvarLage { NORMAL, TIMEOUT, OGILTIGT_JSON, TOMT }` med default `NORMAL`). I `foreslaKomponenter`:
   - `NORMAL` → `Result.success(jsonSvar)` (och uppdatera `senasteUtdrag`)
   - `TIMEOUT` → `Result.failure(...)` med ett lämpligt undantag (t.ex. `java.util.concurrent.TimeoutException("timeout")` eller `TolkningMisslyckades("timeout")` från `se.tmconnect.styrelserummet.tolking`)
   - `OGILTIGT_JSON` → `Result.success("inte json")`
   - `TOMT` → `Result.success("")`
   Uppdatera `senasteUtdrag` i alla lägen.

## Exempel
- Efter `foreslaKomponenter("maskad text", listOf(1), "plan.pdf")` → `senasteUtdrag == "maskad text"`.
- `FalskTolkare()` (default) → `foreslaKomponenter(...).isSuccess` och JSON innehåller `"Tak"` (befintligt beteende).
- `FalskTolkare(lage = SvarLage.TIMEOUT)` (eller motsvarande) → `foreslaKomponenter(...).isFailure` är true.
- `FalskTolkare(lage = SvarLage.OGILTIGT_JSON)` → `getOrNull() == "inte json"`.
- `FalskTolkare(lage = SvarLage.TOMT)` → `getOrNull() == ""`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FalskTolkareTest"

Skapa `FalskTolkareTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert` — **inte** kotlin.test). Minst:
- `senasteUtdragSparas` — anropa med `"maskad text"`, assert `senasteUtdrag == "maskad text"`.
- `standardJsonFungerar` — default-konstruktor, assert success och att svarsträngen innehåller `"komponenter"`.
- `timeoutGerFailure` — timeout-läge, assert `isFailure`.
- `ogiltigtJsonSvar` — assert exakt `"inte json"`.
- `tomtSvar` — assert exakt `""`.

## Gör inte
- Ändra produktionskod.
- Ändra `ApiTest.kt` eller andra filer.
- Lägga till beroenden.
