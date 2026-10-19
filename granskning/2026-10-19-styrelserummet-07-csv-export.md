beslut: godkänd
granskare: cursor
datum: 2026-10-19

## Kontrollerat
- Lokala modellens commit skapade bara tomma `CsvExport.kt` och `CsvExportTest.kt` på rätt sökvägar; inga filer i repots rot.
- Efter granskning: endast de två tillåtna filerna under `styrelserummet/…/regler/` ändrade i denna granskning, plus uppgiftsflytt och beslutsfil enligt workflow.
- `tillCsv`: semikolon som skiljetecken, svensk rubrikrad, heltal utan tusentalsavgränsare, RFC 4180-citering för `;`, `"` och radbrytning, avskrivning året och bokfört värde via befintliga regler, avslutande rad `UTKAST. Beslutas av förening och revisor.` (via `UTKAST_RAD`).
- JUnit 4-tester (`org.junit`) täcker alla tre citatfallen samt rubrik, heltal och utkastrad; inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest` och `:styrelserummet:test --tests "*CsvExportTest"` grönt lokalt.

## Rättat av Cursor
- Hela `CsvExport.kt` och `CsvExportTest.kt` (lokal modell levererade inga implementation eller tester).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
