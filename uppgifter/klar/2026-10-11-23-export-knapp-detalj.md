# Uppgift: exportknapp i detaljvyn

uppdrag: uppdrag/2026-10-11-export-pdf.md
försök: 1
beror-på: 2026-10-11-22-export-pdf-strangar.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Underlag.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/PdfUnderlagSkrivare.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KvittoHelskarm.kt

## Uppgift
1. **`KopDetaljSkarm`**: Lägg till knapp `export_pdf` (minst 48 dp tryckyta) ovanför «Ändra». Parametrar:
   - `arPro: Boolean`
   - `onExportPdf: () -> Unit` — anropas när användaren har Pro och trycker
   - `onVisaProDialog: () -> Unit` — anropas utan Pro
   - Vid tryck: om `arPro` → `onExportPdf()`, annars `onVisaProDialog()`.

2. **`MainActivity`**: Registrera `ActivityResultContracts.CreateDocument("application/pdf")`. Vid export:
   - Bygg `Underlag` med `byggUnderlag(kop, LocalDate.now(), etiketter, dokumentRubrik, locale)`: `etiketter` är en `UnderlagEtiketter` med texterna från `underlag_*`-strängarna, `dokumentRubrik` är `getString(R.string.underlag_rubrik)` och `locale` kommer från `resources.configuration`.
   - Föreslaget filnamn: `reklamationsPdfFilnamn(kop.vad, kop.kopdatum)`.
   - Öppna dokumentväljaren; i callback skriv PDF med `skrivUnderlagTillPdf` till `contentResolver.openOutputStream(uri)`, ladda kvittobitmap via `contentResolver.openInputStream(viewModel.sparatKvittoUri(kop))` och `BitmapFactory.decodeStream` om köpet har kvitto (samma mönster som miniatyr i detaljvyn).
   - Exponera resultat till snackbar-uppgiften via callback eller ViewModel (minimal state, t.ex. `MutableStateFlow` i ViewModel om det behövs — annars håll logiken i Activity tills uppgift 25).

Skicka `arPro`, export- och Pro-callbacks från `MainActivity` till `KopDetaljSkarm`.

## Exempel
- Pro: tryck «Exportera PDF» → systemets spara-dialog med `.pdf`.
- Utan Pro: samma knapp → Pro-dialogen.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*UnderlagTest" --tests "*PdfFilnamnTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Nya manifestbehörigheter.

utfall: underkänd 2026-10-11 av Cursor (PR #58). Ersatt av 2026-10-11-23-export-knapp-detalj-f2.md
