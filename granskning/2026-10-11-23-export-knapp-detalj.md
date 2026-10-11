beslut: underkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- PR #58 ändrar fyra filer mot main; `KopViewModel.kt` får inte ändras enligt uppgiften (endast `KopDetaljSkarm.kt` och `MainActivity.kt`).
- `KopDetaljSkarm`: exportknapp med `export_pdf`, Pro-gate (`arPro` → export / annars Pro-dialog), placerad ovanför «Ändra» — delvis enligt uppgift; tryckyta saknar explicit `heightIn(min = 48.dp)`.
- `MainActivity`: `CreateDocument("application/pdf")` registrerad, men callback anropar `viewModel.exporteraPdf` som är tom stub — ingen `byggUnderlag`, ingen `skrivUnderlagTillPdf`, ingen kvittobitmap.
- Filnamn hårdkodat (`reklamationsunderlag-…` + egen regex) i stället för `reklamationsPdfFilnamn(kop.vad, kop.kopdatum)` från `export/PdfFilnamn.kt`.
- Oanvända imports i `MainActivity` (`skrivUnderlagTillPdf`, `UnderlagEtiketter`, `byggUnderlag`).
- Exportresultat exponeras inte till snackbar-uppgift 25 (stub gör inget).
- Inga nya beroenden, manifestbehörigheter eller nätverk; inga hårdkodade UI-strängar i Kotlin (knapp använder `R.string.export_pdf`).
- Databasschema oförändrat.
- Angivna tester (`*UnderlagTest`, `*PdfFilnamnTest`) körde inte lokalt (saknar Android SDK i miljön); CI på grenen: success.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: exportknapp i detaljvyn

uppdrag: uppdrag/2026-10-11-export-pdf.md
försök: 2
beror-på: 2026-10-11-22-export-pdf-strangar.md

## Vad som saknades
- **`KopViewModel.kt` får inte ändras** — ta bort `exporteraPdf`-stubben och alla ViewModel-ändringar; hela exportlogiken ska ligga i `MainActivity` (uppgift 25 lägger snackbar-state i ViewModel).
- I `CreateDocument`-callback: bygg `Underlag` med `byggUnderlag`, skriv PDF med `skrivUnderlagTillPdf`, ladda kvittobitmap om köpet har kvitto — inte en tom funktion.
- Använd `reklamationsPdfFilnamn(kop.vad, kop.kopdatum)` som föreslaget filnamn, inte egen sträng/logik.
- Spara exportresultat (`PdfExportResult`) i en **Activity**-egenskap (t.ex. `var senasteExportPdfResultat: PdfExportResult?`) efter skrivning så uppgift 25 kan koppla snackbar; ingen ViewModel i denna uppgift.
- Exportknapp: minst 48 dp tryckyta (`Modifier.heightIn(min = 48.dp)` på knappen).

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Underlag.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/PdfUnderlagSkrivare.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/PdfFilnamn.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KvittoHelskarm.kt

## Uppgift
1. **`KopDetaljSkarm`**: Knapp `export_pdf` med minst 48 dp tryckyta, ovanför «Ändra». Parametrar: `arPro`, `onExportPdf`, `onVisaProDialog`. Vid tryck: om `arPro` → `onExportPdf()`, annars `onVisaProDialog()`.

2. **`MainActivity`**:
   - Håll `var exportKop: Kop? = null` (eller motsvarande) så callback vet vilket köp som exporteras.
   - Registrera `ActivityResultContracts.CreateDocument("application/pdf")`.
   - I `onExportPdf` från detaljvyn: sätt `exportKop = kop`, anropa `createPdfContract.launch(reklamationsPdfFilnamn(kop.vad, kop.kopdatum))`.
   - I callback när `uri != null`: hämta `kop` från `exportKop`, nollställ den, bygg `UnderlagEtiketter` med `getString(R.string.underlag_*)` för alla fält (samma fält som i `UnderlagTest`), `dokumentRubrik = getString(R.string.underlag_rubrik)`, `locale` från `resources.configuration.locales[0]` (API 24+).
   - `byggUnderlag(kop, LocalDate.now(), etiketter, dokumentRubrik, locale)`.
   - Öppna `contentResolver.openOutputStream(uri)` och anropa `skrivUnderlagTillPdf(underlag, stream, kvittoBitmap)` där `kvittoBitmap` laddas med `BitmapFactory.decodeStream` på `viewModel.sparatKvittoUri(kop)` om kvitto finns (samma URI-mönster som detaljminiatyren, men full avkodning utan `inSampleSize` för PDF).
   - Sätt `senasteExportPdfResultat` till `PdfExportResult.Ok` eller `PdfExportResult.Fel(...)` efter skrivning; stäng strömmar med `use { }`.
   - Skicka `arPro`, export- och Pro-callbacks till `KopDetaljSkarm` som idag.

## Exempel
- Pro: tryck «Exportera PDF» → systemets spara-dialog med `.pdf` och filnamn enligt `PdfFilnamnTest`.
- Utan Pro: samma knapp → Pro-dialogen.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*UnderlagTest" --tests "*PdfFilnamnTest"

## Gör inte
- Ändra andra filer (särskilt inte `KopViewModel.kt`).
- Lägga till beroenden.
- Ändra databasschemat.
- Nya manifestbehörigheter.
```

## Frågor till Claude
- inga
