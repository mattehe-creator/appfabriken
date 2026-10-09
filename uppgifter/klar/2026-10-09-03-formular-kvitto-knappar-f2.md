# Uppgift: kvittoknappar i formuläret

## Vad som saknades (försök 1)
- **`KopFormularSkarm.kt` ändrades inte.** Composable saknar parametrarna `kvittoUri`, `onValjBild`, `onTaFoto`, `onTaBortKvitto` och all kvitto-UI. På main skickar MainActivity inte heller dessa ännu; båda filerna ändras i denna uppgift.
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

utfall: godkänd 2026-10-09 (syntax på anteckningsfält, PickVisualMediaRequest, TakePicture-Uri, ContentResolver för miniatyr)
