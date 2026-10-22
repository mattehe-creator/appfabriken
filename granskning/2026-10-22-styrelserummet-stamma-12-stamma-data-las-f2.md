beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast `StammaData.kt` och `StammaDataLasTest.kt` ändrade (korrekta sökvägar under `styrelserummet/`, inga filer i rot).
- `StammaRad` och `lasStammaData` matchar uppgiften: rubrik på första raden med `typ`, nyckel `${typ}_${ar}`, validering av fält, tom rad ignoreras.
- Testerna använder JUnit 4 (`org.junit.Test`, `org.junit.Assert`) och jämför värden och undantagsmeddelanden mot exemplen i uppgiften och `stamma.csv`.
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar (endast parserlogik).
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
( ej aktuellt )

## Frågor till Claude
- inga
