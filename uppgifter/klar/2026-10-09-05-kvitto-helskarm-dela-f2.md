# Uppgift: helskärmskvitto med zoom och dela

uppdrag: uppdrag/2026-10-09-kvittofoto.md
försök: 2
beror-på: 2026-10-09-04-detalj-kvitto-miniatur-f2.md, 2026-10-09-02-kvitto-strangar-f2.md

## Vad som saknades
- All kod lades i repots rot (`KvittoHelskarm.kt`, `MainActivity.kt`, `KopSkarmar.kt`) i stället för sökvägarna under `garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/`.
- `garantivalvet/.../ui/KvittoHelskarm.kt` skapades tom. Modulens `MainActivity` uppdaterades inte: helskärmsgrenen är fortfarande tom.
- Använd **endast** befintliga strängnycklar (`kvitto_stang`, `kvitto_dela`, `kvitto_fornhandsvisning` för contentDescription/titel). Lägg inte till nya strängar i denna uppgift.
- Dela-dialogens chooser-titel: använd `context.getString(R.string.kvitto_dela)`, inte `stringResource` inuti `onClick`.
- Bygg `Scaffold` + `TopAppBar` som i `KopDetaljSkarm`, inte en fristående `TopAppBar` utanför scaffold.

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KvittoHelskarm.kt (ny, med innehåll)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt (endast om något saknas; callback sitter redan i `MainActivity` — då **ändra inte** denna fil)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KvittoLager.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt

## Uppgift
1. Skapa **`KvittoHelskarm`** i filen under `garantivalvet/.../ui/` (inte i repots rot):
   - Parametrar: `kvittoUri: Uri`, `onTillbaka: () -> Unit`, valfri `Modifier`.
   - `Scaffold` med `TopAppBar`: stäng (`R.string.kvitto_stang`) anropar `onTillbaka`, dela (`R.string.kvitto_dela`) startar delning.
   - Visa kvittobilden fullskärm (ladda bitmap via `contentResolver.openInputStream` + `remember(kvittoUri)` som i detaljvyn).
   - Pinch-to-zoom: `detectTransformGestures` på scale/offset.
   - Dubbeltryck: växla zoom (t.ex. 1f ↔ 2f) med `detectTapGestures(onDoubleTap = { ... })`, inte bara nollställning.
   - Dela: `Intent(ACTION_SEND)`, `type = "image/jpeg"`, `EXTRA_STREAM = kvittoUri`, `FLAG_GRANT_READ_URI_PERMISSION`; `startActivity(createChooser(...))`.
2. **`MainActivity`** (modulen): i `is KopSkarm.KvittoHelskarm` — ersätt kommentaren med `KvittoHelskarm(kvittoUri = viewModel.sparatKvittoUri(kop)!!, onTillbaka = { viewModel.visaDetalj(kop.id) })` när `valtKop` och URI finns (samma mönster som detaljvyn).
3. **`KopSkarmar.kt`**: ändra bara om `onOppnaKvitto` inte redan nås från detalj; annars rör inte filen.

## Exempel
- Dubbeltryck på bilden zoomar in/ut (växlar mellan normal och förstorad).
- Dela öppnar systemets delningsmeny med kvittobilden.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Skapa eller redigera `.kt`-filer i repots rot.
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Nya manifestbehörigheter.
- Nya strängnycklar (använd befintliga `kvitto_*`).

utfall: godkänd 2026-10-09 (Cursor flyttade implementation från repots rot till modulvägar, lade till stäng-knapp, rättade zoom/imports; tog bort rotfiler)
