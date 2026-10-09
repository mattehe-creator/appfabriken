# Uppgift: kvittoknappar i formuläret

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 1
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
   - Liten förhandsvisning när `kvittoUri: Uri?` inte är null (t.ex. `AsyncImage` eller `Image` med `ContentScale.Crop`, höjd ca 120 dp).
   - Knapp **Ta bort kvitto** (`kvitto_ta_bort`) när det finns en förhandsvisning; anropa `onTaBortKvitto`.
2. Nya parametrar på `KopFormularSkarm`:
   - `kvittoUri: Uri?`
   - `onValjBild: () -> Unit`
   - `onTaFoto: () -> Unit`
   - `onTaBortKvitto: () -> Unit`
3. I **`MainActivity`**: registrera `ActivityResultContracts.PickVisualMedia()` och `TakePicture()` (eller motsvarande). Vid val: `viewModel.sattTillfalligtKvitto(uri)`. Kamera: skapa URI med `viewModel.skapaKameraKvittoUri()` före intent, anropa `sattTillfalligtKvitto` vid lyckat resultat. Skicka `viewModel.kvittoUriForFormular(valtKop)` till formuläret.

## Exempel
- Användaren trycker Välj bild och väljer foto → miniatyr syns i formuläret.
- Användaren trycker Ta bort kvitto → miniatyren försvinner; sparat kvitto tas bort vid Spara (via `markeraKvittoForBorttagning`).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormularValideringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Lägga till manifestbehörigheter.

utfall: underkänd 2026-10-09 av Cursor (PR #17). Ersatt av 2026-10-09-03-formular-kvitto-knappar-f2.md (verkställt av Claude, rött bygge stoppade cursor-beslut)
