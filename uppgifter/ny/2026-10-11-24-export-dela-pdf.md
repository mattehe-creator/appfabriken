# Uppgift: dela reklamations-PDF

uppdrag: uppdrag/2026-10-11-export-pdf.md
försök: 1
beror-på: 2026-10-11-22-export-pdf-strangar.md, 2026-10-11-23-export-knapp-detalj.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt
- garantivalvet/src/main/res/xml/kvittofil_paths.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KvittoHelskarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/PdfUnderlagSkrivare.kt

## Uppgift
1. **`kvittofil_paths.xml`**: Lägg till `cache-path` för tillfälliga PDF-filer (t.ex. `path="export/"`).

2. **`KopDetaljSkarm`**: Knapp `export_dela` (minst 48 dp). Samma Pro-gate som export: utan Pro → Pro-dialog, med Pro → `onDelaPdf()`.

3. **`MainActivity`**: `onDelaPdf`:
   - Skriv PDF till `cacheDir/export/` med filnamn från `reklamationsPdfFilnamn`.
   - `FileProvider.getUriForFile` med authority `${applicationId}.kvittofil`.
   - `Intent.ACTION_SEND`, typ `application/pdf`, `FLAG_GRANT_READ_URI_PERMISSION`, chooser med `export_dela`.

Pro-gate gäller bara export/dela, inte visning av köpet.

## Exempel
- Pro + Dela: systemets delningsmeny med PDF.
- Utan Pro: Pro-dialog.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Nya manifestbehörigheter (FileProvider finns).
