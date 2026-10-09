beslut: underkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- **Filer utanför uppgiften:** Ny fil `KvittoHelskarm.kt` (helskärm/dela) ingår inte i uppgiften och tillhör senare steg. `MainActivity` ändrad med `onOppnaKvitto`, full `KvittoHelskarm`-gren och dela-intent — utanför denna uppgift.
- **Saknad huvudleverans:** `KopFormularSkarm.kt` är oförändrad trots att uppgiften kräver kvittoknappar, förhandsvisning och nya parametrar (`kvittoUri`, `onValjBild`, `onTaFoto`, `onTaBortKvitto`).
- **Bygge:** CI `compileDebugKotlin` misslyckades (saknade parametrar på `KopFormularSkarm`, felaktigt `TakePicturePreview` + `launch(uri)`, `stringResource` utanför `@Composable`, Coil `AsyncImage` utan beroende, `kvitto_bild` finns inte).
- **Uppgiftens beteende:** Kamera sparar inte bitmap till fil-URI (`TakePicturePreview` + tom fil-URI). `PickVisualMedia` anropades med `null` i stället för `PickVisualMediaRequest`.
- **Tester:** `FormularValideringTest` kördes inte (kompilering stoppade). Inga nya tester tillagda (uppgiften kräver befintligt test).
- **Beroenden/behörigheter/nätverk:** Coil importerad utan `build.gradle`-beroende (får inte läggas till av lokal modell). Inga manifestbehörigheter tillagda.
- **Hårdkodade strängar:** KvittoHelskarm använder fel strängnyckel `kvitto_bild`; övrig ny kod följer i huvudsak `strings.xml` där den kompilerar.
- **JUnit:** Ingen `kotlin.test` i grenens kodändringar.

## Rättat av Cursor
- inget (för stora avvikelser; se försök 2)

## Vid underkänd: försök 2

```
# Uppgift: kvittoknappar i formuläret

## Vad som saknades (försök 1)
- **`KopFormularSkarm.kt` ändrades inte.** MainActivity skickar redan `kvittoUri`, `onValjBild`, `onTaFoto` och `onTaBortKvitto`, men composable saknar dessa parametrar och all kvitto-UI.
- **Otillåten fil:** `garantivalvet/.../ui/KvittoHelskarm.kt` skapades och ska **tas bort** (helskärm byggs i uppgift `2026-10-09-05-kvitto-helskarm-dela.md`).
- **MainActivity utanför scope:** Ta bort `onOppnaKvitto` på `KopDetaljSkarm`, hela implementationen under `KopSkarm.KvittoHelskarm` (behåll kommentarsplatshållaren som på main) och dela-intent/`Intent`-import om de bara används där.
- **Fel kamera-API:** Använd `ActivityResultContracts.TakePicture()`, inte `TakePicturePreview`. Flöde: spara URI från `viewModel.skapaKameraKvittoUri()` i en **fältvariabel** i aktiviteten före `launch(uri)`; i callback vid `success == true` anropa `viewModel.sattTillfalligtKvitto` med **samma** URI (kameran skriver till filen).
- **Välj bild:** `pickImageContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))` — inte `launch(null)`.
- **Ingen Coil:** Projektet har inget Coil-beroende. Ladda miniatyr med `androidx.compose.foundation.Image` + `BitmapFactory`/`ContentResolver` (eller motsvarande utan nya bibliotek). `contentDescription`: `R.string.kvitto_fornhandsvisning`.
- Bygget måste vara grönt: `gradle :garantivalvet:testDebugUnitTest --tests "*FormularValideringTest"`.

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 2
beror-på: 2026-10-09-02-kvitto-strangar-f2.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KvittoLager.kt

## Uppgift
1. Utöka **`KopFormularSkarm`** med:
   - Knappar **Välj bild** och **Ta foto** (`kvitto_valj_bild`, `kvitto_ta_foto`).
   - Liten förhandsvisning när `kvittoUri: Uri?` inte är null (höjd ca 120 dp, `ContentScale.Crop`).
   - Knapp **Ta bort kvitto** (`kvitto_ta_bort`) när det finns en förhandsvisning; anropa `onTaBortKvitto`.
2. Nya parametrar på `KopFormularSkarm`:
   - `kvittoUri: Uri?`
   - `onValjBild: () -> Unit`
   - `onTaFoto: () -> Unit`
   - `onTaBortKvitto: () -> Unit`
3. I **`MainActivity`**: registrera `ActivityResultContracts.PickVisualMedia()` och `TakePicture()`. Vid val: `viewModel.sattTillfalligtKvitto(uri)`. Kamera: skapa URI med `viewModel.skapaKameraKvittoUri()` före intent, anropa `sattTillfalligtKvitto` vid lyckat resultat. Skicka `viewModel.kvittoUriForFormular(valtKop)` till formuläret (lägg till **och** ändra-formulär).
4. **Rensa försök 1:** radera `KvittoHelskarm.kt` om den finns kvar. Ändra inte detaljvyn eller helskärmsgrenen utöver att återställa det som hör till senare uppgifter.

## Exempel
- Användaren trycker Välj bild och väljer foto → miniatyr syns i formuläret.
- Användaren trycker Ta bort kvitto → miniatyren försvinner; sparat kvitto tas bort vid Spara (via `markeraKvittoForBorttagning`).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormularValideringTest"

## Gör inte
- Ändra andra filer (inklusive ingen ny `KvittoHelskarm.kt`).
- Lägga till beroenden (inga Coil m.m.).
- Ändra databasschemat.
- Lägga till manifestbehörigheter.
- Implementera helskärm, dela eller `onOppnaKvitto` (annan uppgift).
```

## Frågor till Claude
- inga
