# Uppgift: hämta från säkerhetskopia

uppdrag: uppdrag/2026-10-11-export-sakerhetskopia.md
försök: 1
beror-på: 2026-10-11-28-export-sakerhetskopia-spara.md, cursor:2026-10-11-export-sakerhetskopia

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/Sakerhetskopia.kt (`lasIn`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KopRepository.kt (`importeraSakerhetskopia`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/paminnelse/PaminnelsePlanerare.kt

## Uppgift
1. **`MainActivity`:** Registrera `ActivityResultContracts.OpenDocument()` med MIME `application/zip` och `application/octet-stream`. Vid vald URI:
   - Läs bytes/stream, anropa `Sakerhetskopia.lasIn`.
   - Vid `Ok` → anropa ViewModel för import.
   - Vid fel «nyare version» → visa `sakerhetskopia_nyare_version`.
   - Övriga läsfel → `sakerhetskopia_kunde_inte_lasas`.

2. **`KopViewModel`:** `fun importeraSakerhetskopia(resultat: SakerhetskopiaLasResult.Ok)` (eller motsvarande typ från export-paketet):
   - Anropa `repository.importeraSakerhetskopia(...)`.
   - Efter lyckad import: om påminnelser är på, anropa `PaminnelsePlanerare.synka(appContext, true)`.
   - Exponera meddelande för UI (StateFlow eller engångs-event) med `sakerhetskopia_importerad` och antal tillagda/hoppade.

3. Koppla `onHamtaSakerhetskopia` till openDocument.

## Exempel
- Giltig fil med 2 nya köp och 1 dubblett → «Importerade 2 köp, hoppade över 1».
- `version` 2 i filen → «Filen kommer från en nyare version av appen», inga köp tillagda.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Delvis import vid fel.
- Nya behörigheter.

utfall: godkänd 2026-10-12 (stängde input stream med use; tog bort nästlade viewModelScope.launch i import)
