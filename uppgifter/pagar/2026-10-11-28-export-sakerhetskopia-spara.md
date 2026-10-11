# Uppgift: spara säkerhetskopia till fil

uppdrag: uppdrag/2026-10-11-export-sakerhetskopia.md
försök: 1
beror-på: 2026-10-11-27-export-sakerhetskopia-installning.md, cursor:2026-10-11-export-sakerhetskopia

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/export/Sakerhetskopia.kt (`skriv`, `sakerhetskopiaFilnamn`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt (befintlig `createPdfContract`)

## Uppgift
1. **`MainActivity`:** Registrera `ActivityResultContracts.CreateDocument("application/zip")`. Föreslaget filnamn från `sakerhetskopiaFilnamn(LocalDate.now())`. Vid vald URI:
   - Hämta alla köp (via ViewModel/repository).
   - Skriv ZIP med `Sakerhetskopia.skriv(outputStream, kop, kvittoLager)`.
   - Visa snackbar/toast med `sakerhetskopia_sparad`, eller feltext `sakerhetskopia_kunde_inte_lasas` vid IO-fel.

2. **`KopViewModel`:** Suspend-funktion eller callback-vänlig metod som returnerar aktuell köplista för export (t.ex. `suspend fun allaKopForExport(): List<Kop>`).

3. Koppla `onSparaSakerhetskopia` i listskärmen till att starta createDocument-kontraktet (endast när Pro; gaten sitter redan i UI).

## Exempel
- Pro, användaren sparar → ZIP skrivs, «Säkerhetskopia sparad» visas.
- Skrivfel → «Filen kunde inte läsas» (samma nyckel som importfel).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Import eller filväljare för öppning.
- Nya behörigheter.
