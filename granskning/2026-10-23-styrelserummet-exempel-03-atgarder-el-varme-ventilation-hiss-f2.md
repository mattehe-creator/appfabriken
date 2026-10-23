beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- Endast de tre listade Kotlin-filerna ändrade (plus uppgiftsfilens flytt ny→pagar från lokala körskriptet); inga filer i fel sökväg.
- Ingen ändring utanför uppgiftens kodomfång (ingen databas, inga andra moduler).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen hårdkodad UI-text; endast exempeldata och enhetstester.
- Databasschemat oförändrat.
- `ExempelData.kt` har åtta åtgärder med grupp 1 oförändrad och grupp 2 enligt tabellen.
- `ExempelDataAtgarderGrupp2Test` kontrollerar namn, år, kostnad och totalsumma 23_300_000L; JUnit 4 (`org.junit`).
- `ExempelDataAtgarderGrupp1Test` förväntar 8 poster; grupp 1-fälten oförändrade.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
( ej tillämpligt )

## Frågor till Claude
- inga
