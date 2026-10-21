# Uppgift: Utökad falsk tolkare för tester

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FalskTolkare.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FalskTolkareTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/tolking/Tolkare.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Uppgift
Gränssnittet `Tolkare` finns redan. Utöka testklassen `FalskTolkare` så att den:

1. Sparar senaste `underhallsplanUtdrag` i en läsbar egenskap (t.ex. `senasteUtdrag`) som test kan läsa efter anrop.
2. Kan konfigureras att returnera ett eget JSON-svar (standard ska fortfarande fungera för befintliga `ApiTest`).
3. Kan konfigureras att simulera **timeout** (`Result.failure` med lämpligt undantag), **ogiltigt JSON** (`Result.success` med `"inte json"`), och **tomt svar** (`Result.success("")`).

## Exempel
- Efter `foreslaKomponenter("maskad text", listOf(1), "plan.pdf")` → `senasteUtdrag == "maskad text"`.
- Med timeout-läge → `foreslaKomponenter(...).isFailure` är true.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FalskTolkareTest"

## Gör inte
- Ändra produktionskod.
- Lägga till beroenden.
