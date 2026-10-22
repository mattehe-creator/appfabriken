beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast `StammaData.kt` och `StammaDataPrisbasbeloppTest.kt` ändrade (korrekta sökvägar under `styrelserummet/`, inga filer i rot).
- `prisbasbeloppFor` och `lasStammaDataFranResurs` matchar uppgiften: nyckel `prisbasbelopp_$ar`, krav på typ och enhet `kr`, läsning av `/stamma.csv` via classpath och `lasStammaData`.
- Testerna använder JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` / `assertNull`) och verifierar 59200 för 2026, null för saknat år och fel enhet, plus `lasStammaDataFranResurs()` mot riktig CSV.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hela implementationen: den lokala modellen levererade ingen kod på försök 2 (PR #579 hade bara flytt av uppgiftsfil till `pagar/`). Cursor lade till `prisbasbeloppFor`, `lasStammaDataFranResurs` och `StammaDataPrisbasbeloppTest` enligt uppgiften.

## Vid underkänd: försök 2
( ej aktuellt )

## Frågor till Claude
- inga
