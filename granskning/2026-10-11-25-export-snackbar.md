beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Endast `KopDetaljSkarm.kt`, `KopViewModel.kt`, `MainActivity.kt` och uppgiftsfilens flytt `ny/` → `pagar/` ändrade mot main; inga filer i repots rot.
- `KopViewModel` har `StateFlow<ExportPdfMeddelande?>` samt `meddelaExportSparad()`, `meddelaExportMisslyckades()` och `rensaExportMeddelande()`.
- `KopDetaljSkarm` tar emot exportmeddelande och callback; `Scaffold` med `SnackbarHost`/`SnackbarHostState`; snackbar visar `export_pdf_sparad` / `export_pdf_misslyckades` via `stringResource` och rensar state efter visning.
- `MainActivity` anropar ViewModel efter lyckad/misslyckad PDF-skrivning i `CreateDocument`-callback; `senasteExportPdfResultat` borttagen; dela-knappen orörd.
- Inga nya beroenden, manifestbehörigheter eller nätverksanrop; databasschema oförändrat.
- Inga hårdkodade UI-strängar i Compose.
- `:garantivalvet:testDebugUnitTest` (inkl. `UnderlagTest`, JUnit 4) grönt lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
