# Uppgift: prisbasbeloppFor

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-12-stamma-data-las-f2.md

## Vad som saknades
- `StammaData.kt` ändrades inte — lägg till `prisbasbeloppFor` och `lasStammaDataFranResurs` enligt uppgiften nedan.
- `StammaDataPrisbasbeloppTest.kt` skapades men är helt tom — skriv JUnit 4-tester som verifierar exemplen (59200, null för saknat år, null för fel enhet).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaDataPrisbasbeloppTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/resources/stamma.csv
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaDataLasTest.kt (JUnit 4-stil)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaCsvTest.kt (läs `/stamma.csv` från classpath)

## Uppgift
Lägg till i `StammaData.kt`:

fun prisbasbeloppFor(ar: Int, data: Map<String, StammaRad>): Long?
fun lasStammaDataFranResurs(): Map<String, StammaRad>

`prisbasbeloppFor`: slå upp nyckeln `"prisbasbelopp_$ar"` i `data`. Finns raden och `rad.typ == "prisbasbelopp"` och `rad.enhet == "kr"`, returnera `rad.varde`. Annars `null`.

`lasStammaDataFranResurs`: läs `/stamma.csv` från classpath (samma mönster som i `StammaCsvTest`: `getResourceAsStream("/stamma.csv")`), läs alla rader, anropa `lasStammaData(rader)`.

## Exempel
- `data` innehåller rad för 2026 (t.ex. via `lasStammaData` eller `lasStammaDataFranResurs`) → `prisbasbeloppFor(2026, data)` = `59200L`
- `data` utan nyckel `prisbasbelopp_2019` → `prisbasbeloppFor(2019, data)` = `null`
- `data` med `prisbasbelopp_2026` men `enhet` inte `kr` (t.ex. `promille_av_pbb`) → `null`

## Test som ska gå igenom
Skriv `StammaDataPrisbasbeloppTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` / `assertNull`) — **inte** `kotlin.test`. Minst tre tester som motsvarar exemplen ovan; gärna ett test som anropar `lasStammaDataFranResurs()` och kontrollerar att 2026 ger 59200.

Kör:
`gradle :styrelserummet:testDebugUnitTest --tests "*StammaDataPrisbasbeloppTest*"`

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (Cursor implementerade hela uppgiften efter tom försök 2)
