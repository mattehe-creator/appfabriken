# Uppgift: spara säkerhetskopia till fil

uppdrag: uppdrag/2026-10-16-husloggen-sakerhetskopia.md
försök: 1
beror-på: 2026-10-16-25-sakerhetskopia-installning.md, cursor:2026-10-16-husloggen-sakerhetskopia

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/Sakerhetskopia.kt (`skriv`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaFilnamn.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt (createDocument för ZIP)

## Uppgift
1. **`MainActivity`:** Registrera `ActivityResultContracts.CreateDocument("application/zip")`. Föreslaget filnamn från `sakerhetskopiaFilnamn(LocalDate.now())`. Vid vald URI: skriv ZIP via ViewModel på IO-tråd, visa snackbar med `sakerhetskopia_sparad` eller `sakerhetskopia_kunde_inte_lasas` vid fel.

2. **`HusViewModel`:** Suspend-funktion eller metod som hämtar uppgifter och poster för export och anropar `Sakerhetskopia.skriv(outputStream, uppgifter, poster, bildLager)`.

3. Koppla `onSparaSakerhetskopia` till createDocument-kontraktet.

## Exempel
- Användaren sparar → ZIP skrivs, «Säkerhetskopia sparad» visas.
- Skrivfel → «Filen kunde inte läsas».

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Import eller openDocument här.
- Nya behörigheter.
