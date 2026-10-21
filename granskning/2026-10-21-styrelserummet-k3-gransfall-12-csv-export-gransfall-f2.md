beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Lokal modell ändrade otillåtet `CsvExportTest.kt` (fyra tester och `komponent()` borttagna); `CsvExport.kt` orörd — i linje med uppgiften efter Cursors rättelse.
- Endast tillåten testsökväg under `styrelserummet/…/regler/`; inga filer i repots rot; inga nya beroenden, behörigheter eller nätverk.
- `CsvExportGransfallTest` testar `tillCsv` enligt uppgiften: kombinerad citering, svenska tecken, talkolumner utan U+00A0/tusentalsavgränsare, rubrik+utkast, tom lista exakt som `CsvExportTest.tomListaHarRubrikOchUtkastRad`.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inget `kotlin.test`.
- `:styrelserummet:test --tests "*CsvExport*"` och `:styrelserummet:testDebugUnitTest` gröna lokalt.

## Rättat av Cursor
- Återställde `CsvExportTest.kt` till main (uppgiften förbjuder ändring av den filen).
- `rubrikOchUtkast`: `trimEnd('\n')` före `split("\n")` så radantalet stämmer med avslutande radbrytning i `tillCsv` (orsak till CI-fel).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
