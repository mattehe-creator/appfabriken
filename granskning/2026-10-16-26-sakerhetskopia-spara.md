beslut: underkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- `MainActivity.kt` och `HusViewModel.kt` är oförändrade mot main; uppgiften (CreateDocument, `sparaSakerhetskopia`, koppling av `onSparaSakerhetskopia`) är inte implementerad.
- `onSparaSakerhetskopia` är fortfarande tom callback `{}` i `MainActivity.kt`.
- Endast `FRAGA.md` och flytt av uppgiftsfil till `pagar/` tillkom på grenen; ingen appkod.
- Inga nya beroenden, behörigheter eller nätverk (ingen kod ändrad).
- `FRAGA.md` i rot: fråga om `Sakerhetskopia.skriv` — besvaras i försök 2 nedan.
- Angivet test `:husloggen:testDebugUnitTest --tests "*StringsNycklarTest"` körs grönt (oförändrad kodbas).

## Rättat av Cursor
- Tog bort `FRAGA.md` (svaret finns i försök 2).

## Vid underkänd: försök 2

```
# Uppgift: spara säkerhetskopia till fil

uppdrag: uppdrag/2026-10-16-husloggen-sakerhetskopia.md
försök: 2
beror-på: 2026-10-16-25-sakerhetskopia-installning.md, cursor:2026-10-16-husloggen-sakerhetskopia-v2

## Vad som saknades
- Ingen implementation: varken `CreateDocument`, ViewModel-export eller koppling av `onSparaSakerhetskopia` gjordes. Commit-meddelandet matchade inte diffen (endast `FRAGA.md` lades till).
- **Svar på FRAGA.md:** `Sakerhetskopia.skriv` ligger **inte** i `:karna`. Den finns redan i Husloggen-modulen: `husloggen/src/main/kotlin/se/tmconnect/husloggen/export/Sakerhetskopia.kt` (läs som förebild). `:karna` bidrar med `BildLager` och zip-hjälpare; du ska **inte** implementera `skriv` i karna och **inte** lägga filer i repots rot.
- För `bildLager` vid manuell export: använd samma mönster som `SakerhetskopiaWorker` — `skapaHusBildLager(appContext)` tillsammans med `repository.allaUppgifter()` och `repository.allaPoster()` inuti `withContext(Dispatchers.IO)`.
- Snackbar: utöka `HusUiMeddelande` i `HusViewModel.kt` med t.ex. `SakerhetskopiaSparad` och `SakerhetskopiaKundeInteLasas`, emit efter sparning, och visa i `MainActivity` via befintliga `meddelanden`/`SnackbarHost` med `R.string.sakerhetskopia_sparad` respektive `R.string.sakerhetskopia_kunde_inte_lasas` (inga hårdkodade texter).

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/Sakerhetskopia.kt (`skriv`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaFilnamn.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaWorker.kt (`korSakerhetskopiaJobb`, rad med `Sakerhetskopia.skriv`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt (createDocument för ZIP, snackbar för säkerhetskopia)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt (`sparaSakerhetskopia`)

## Uppgift
1. **`MainActivity`:** Registrera `ActivityResultContracts.CreateDocument("application/zip")`. Föreslaget filnamn från `sakerhetskopiaFilnamn(LocalDate.now())`. Vid vald URI: anropa ViewModel för att skriva ZIP (I/O på IO-tråd i ViewModel), visa snackbar med `sakerhetskopia_sparad` eller `sakerhetskopia_kunde_inte_lasas` vid fel.

2. **`HusViewModel`:** Metod `sparaSakerhetskopia(uri: Uri)` som hämtar uppgifter och poster (`repository.allaUppgifter()`, `repository.allaPoster()`), anropar `Sakerhetskopia.skriv(outputStream, uppgifter, poster, bildLager)` med `contentResolver.openOutputStream(uri)` inuti `withContext(Dispatchers.IO)`.

3. Koppla `onSparaSakerhetskopia` till createDocument-kontraktet (starta med föreslaget filnamn).

## Exempel
- Användaren sparar → ZIP skrivs, «Säkerhetskopia sparad» visas.
- Skrivfel → «Filen kunde inte läsas».

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Import eller openDocument här.
- Nya behörigheter.
- Lägg `FRAGA.md` i rot — implementera enligt förebilderna ovan.
```

## Frågor till Claude
- inga
