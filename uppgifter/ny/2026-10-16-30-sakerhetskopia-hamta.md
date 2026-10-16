# Uppgift: hämta från säkerhetskopia

uppdrag: uppdrag/2026-10-16-husloggen-sakerhetskopia.md
försök: 1
beror-på: 2026-10-16-26-sakerhetskopia-spara.md, cursor:2026-10-16-husloggen-sakerhetskopia-v2

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/Sakerhetskopia.kt (`lasIn`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/HusRepository.kt (`importeraSakerhetskopia`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt (`lasInSakerhetskopia`, importmeddelanden)

## Uppgift
1. **`MainActivity`:** Registrera `ActivityResultContracts.OpenDocument()` med MIME `application/zip` och `application/octet-stream`. Vid vald URI: läs stream, anropa `Sakerhetskopia.lasIn`, delegera till ViewModel. Visa snackbar för resultat.

2. **`HusViewModel`:** Metod `lasInSakerhetskopia(uri: Uri)` som importerar vid `Ok`, mappar fel till meddelanden (`sakerhetskopia_nyare_version`, `sakerhetskopia_kunde_inte_lasas`, `sakerhetskopia_importerad` med antal). Exponera engångsmeddelande (SharedFlow eller StateFlow) som `MainActivity` visar i snackbar.

3. Koppla `onHamtaSakerhetskopia` till openDocument.

## Exempel
- Giltig fil med 2 nya poster och 1 dubblett → «Importerade 2 poster, hoppade över 1».
- `version` 2 → «Filen kommer från en nyare version av appen», inga poster tillagda.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Delvis import vid fel.
- Nya behörigheter.
