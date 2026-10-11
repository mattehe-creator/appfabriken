# Uppgift: meddelande efter PDF-export

uppdrag: uppdrag/2026-10-11-export-pdf.md
försök: 1
beror-på: 2026-10-11-22-export-pdf-strangar.md, 2026-10-11-23-export-knapp-detalj-f2.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/PdfUnderlagSkrivare.kt

## Uppgift
Visa **Snackbar** i detaljvyn efter export via dokumentväljaren (uppgift 23):

1. **`KopViewModel`**: Håll kortlivad state för exportresultat, t.ex. `StateFlow<ExportPdfMeddelande?>` med värden `Sparad` / `Misslyckades`, plus funktioner `meddelaExportSparad()`, `meddelaExportMisslyckades()`, `rensaExportMeddelande()`.

2. **`KopDetaljSkarm`**: Ta emot meddelande-state och `onExportMeddelandeVisat`. Använd `Scaffold` `snackbarHost` + `SnackbarHostState`; när meddelande inte är null, visa `export_pdf_sparad` eller `export_pdf_misslyckades` via `stringResource`, sedan rensa.

`MainActivity` anropar ViewModel efter lyckad/misslyckad skrivning i CreateDocument-callback.

Dela-knappen (uppgift 24) behöver inte snackbar enligt uppdraget.

## Exempel
- Användaren sparar PDF → Snackbar «PDF sparad».
- Skrivning misslyckas → Snackbar «PDF kunde inte skapas».

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*UnderlagTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-11 (inget)
