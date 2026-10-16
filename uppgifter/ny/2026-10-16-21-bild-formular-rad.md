# Uppgift: bildrad i postformuläret

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: 2026-10-16-20-bild-strangar.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostBildFalt.kt (ny)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostFormularSkarm.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt (kvittoknappar)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostFormularFalt.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BildGrans.kt

## Uppgift
1. **`PostBildFalt`**: composable med miniatyrer (FileProvider-URI per filnamn), knappar **Välj bild** och **Ta foto** (minst 48 dp), borttagningsknapp per bild med `bild_ta_bort`. När `kanLäggaTillBild(antal)` är false: inaktivera lägg-till-knapparna och visa `bild_max_antal`.
2. **`PostFormularSkarm`**: visa `PostBildFalt` med data från `PostFormularTillstand` (bildfilnamn) och callbacks från ViewModel.
3. **`MainActivity`**: registrera `PickVisualMedia` och `TakePicture`; vid val anropa `viewModel.laggTillBildUri(uri)`; kamera med `viewModel.skapaKameraFotoUri()` före intent.

Parametrar ungefär: `bildFilnamn`, `bildUri: (String) -> Uri?`, `kanLaggaTill`, `onValjBild`, `onTaFoto`, `onTaBortBild`.

## Exempel
- Post med 10 bilder → Välj bild och Ta foto är inaktiva, förklaringstext syns.
- Tryck på ta bort på en miniatyr → bilden försvinner från formuläret.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*BildTolkningTest" --tests "*BildGransTest" --tests "*StringsNycklarTest"

## Gör inte
- Ändra `HusRepository`, `BildLager` eller databas.
- Lägga till beroenden eller manifestbehörigheter.
