beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Endast `KopDetaljSkarm.kt`, `MainActivity.kt` och uppgiftsfilens flytt är ändrade; inga filer i repots rot eller `KopViewModel.kt`.
- Exportknapp med `export_pdf`, minst 48 dp, ovanför «Ändra»; Pro startar `CreateDocument` med `reklamationsPdfFilnamn`, icke-Pro öppnar Pro-dialogen.
- `MainActivity` bygger underlag med `getString(R.string.underlag_*)`, skriver PDF via `skrivUnderlagTillPdf`, laddar kvittobitmap från `sparatKvittoUri` utan `inSampleSize`, sparar `senasteExportPdfResultat` på Activity.
- Inga nya beroenden, manifestbehörigheter eller nätverksanrop.
- UI-text via `stringResource`/`getString`; inga hårdkodade UI-strängar i Compose.
- Databasschemat oförändrat.
- `UnderlagTest` och `PdfFilnamnTest` (JUnit 4) gröna lokalt.

## Rättat av Cursor
- Tog bort hårdkodad fallback «Okänt fel» i export-catch (samma mönster som `PdfUnderlagSkrivare`), förenklad kvittoladdning och imports för `Kop`/`PdfExportResult`.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
