beslut: underkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Filer utanför uppgiften ändrade: ny fil `KvittoHelskarm.kt` (hör till uppgift 05), massiv duplicering av `KopDetaljSkarm`/`KopDetaljSkelett` i `KopSkarmar.kt` i stället för att utöka befintlig `KopDetaljSkarm.kt`.
- Uppgiften kräver kvittominiatyr och wiring i MainActivity; detaljvyn på main ligger i `KopDetaljSkarm.kt`, inte i `KopDetaljSkelett` (borttaget i uppgift 2026-10-08-08).
- Kompilering misslyckades (CI): okända strängresurser, saknad `@OptIn` för TopAppBar, `Uri.asImageBitmap()` (extension gäller `Bitmap`, inte `Uri`), samt dubbel `KopDetaljSkarm` mot `KopDetaljSkarm.kt`.
- Inga nya Gradle-beroenden eller manifestbehörigheter tillagda.
- Testet `*KopDaoTest` kördes inte grönt på grenen p.g.a. kompileringsfel.
- Hårdkodade engelska kommentarer i MainActivity/KvittoHelskarm (mindre; huvudproblemet är struktur och kompilering).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: kvittominiatyr i detaljvyn

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 2
beror-på: 2026-10-09-02-kvitto-strangar-f2.md

## Vad som saknades
- Skapade `KvittoHelskarm.kt` och fyllde i helskärmsgrenen — det tillhör uppgift `2026-10-09-05-kvitto-helskarm-dela.md`, inte denna uppgift.
- La till en andra, förenklad `KopDetaljSkarm` + `KopDetaljSkelett` i `KopSkarmar.kt` i stället för att utöka den befintliga detaljvyn i `KopDetaljSkarm.kt` (som redan används från `MainActivity`).
- Använde `kvittoUri.asImageBitmap()` — så laddas inte en `Uri`; använd `ContentResolver` + `BitmapFactory.decodeStream` och sedan `Bitmap.asImageBitmap()`.
- Använde strängnycklar som inte finns (`detalj_titel`, `var_kopt`, `kopdatum`, …); använd befintliga fältsträngar och `R.string.kvitto_fornhandsvisning` som i uppgiften.
- MainActivity ska bara koppla `onOppnaKvitto` till `viewModel.visaKvittoHelskarm(kop.id)`; lämna `KopSkarm.KvittoHelskarm`-grenen som tom kommentar tills uppgift 05.

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Uppgift
1. Utöka **`KopDetaljSkarm`** med parametrar `kvittoUri: Uri?` och `onOppnaKvitto: () -> Unit`.
2. När `kvittoUri != null`: visa miniatyr (ca 160 dp höjd, `Modifier.fillMaxWidth()`) med innehållsbeskrivning `stringResource(R.string.kvitto_fornhandsvisning)`; klick på miniatyren anropar `onOppnaKvitto`.
3. Ladda bilden från URI utan nya beroenden, t.ex. `remember(kvittoUri)` med `LocalContext.current.contentResolver.openInputStream(uri)` och `BitmapFactory.decodeStream`.
4. Placera miniatyren i innehållskolumnen före knapparna Ändra/Ta bort.
5. I **`MainActivity`**: på detaljskärmen skicka `kvittoUri = viewModel.sparatKvittoUri(kop)` och `onOppnaKvitto = { viewModel.visaKvittoHelskarm(kop.id) }`.
6. **Rensa** felaktiga tillägg från försök 1 om de fortfarande finns på grenen: ta bort `KvittoHelskarm.kt`, ta bort duplicerad `KopDetaljSkarm`/`KopDetaljSkelett` ur `KopSkarmar.kt`, och återställ helskärmsgrenen i MainActivity till enbart kommentaren om uppgift 05.

## Exempel
- Köp med `kvittoFil` satt → miniatyr syns på detaljsidan; klick sätter navigering till `KopSkarm.KvittoHelskarm` (tom vy tills uppgift 05).
- Köp utan kvitto → ingen miniatyr.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopDaoTest"

## Gör inte
- Ändra andra filer (skapa inte `KvittoHelskarm.kt`, duplicera inte detaljvyn i `KopSkarmar.kt`).
- Lägga till beroenden.
- Ändra databasschemat.
```

## Frågor till Claude
- Försök 1 pekade på `KopDetaljSkelett` i `KopSkarmar.kt`, men skelettet togs bort när `KopDetaljSkarm.kt` infördes (uppgift 2026-10-08-08). Försök 2 byter till `KopDetaljSkarm.kt` — bekräfta att det stämmer med uppdragsordningen, eller uppdatera uppgiftsmallen på main så försök 1 inte leder fel igen.
