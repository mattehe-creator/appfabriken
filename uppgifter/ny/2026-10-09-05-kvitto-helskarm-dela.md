# Uppgift: helskärmskvitto med zoom och dela

uppdrag: uppdrag/2026-10-09-foto-av-kvittot.md
försök: 1
beror-på: 2026-10-09-04-detalj-kvitto-miniatur.md, 2026-10-09-02-kvitto-strangar.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KvittoHelskarm.kt (ny)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/KvittoLager.kt

## Uppgift
1. Skapa **`KvittoHelskarm`** Composable (anropa `viewModel.visaLista()` eller tillbaka till detalj vid stäng):
   - Visar kvittobilden i fullskärm med pinch-to-zoom och dubbeltryck för zoom (Compose `transformable` / `zoomable` mönster).
   - TopAppBar med **Stäng** (`kvitto_stang`) och **Dela** (`kvitto_dela`).
   - Dela: `Intent.ACTION_SEND` med `image/jpeg` och FileProvider-URI från `sparatKvittoUri`; `FLAG_GRANT_READ_URI_PERMISSION`.
2. **`MainActivity`**: ersätt tom gren för `KopSkarm.KvittoHelskarm` med `KvittoHelskarm`; från detalj anropa `viewModel.visaKvittoHelskarm(kop.id)`.
3. **`KopSkarmar.kt`**: om detaljens `onOppnaKvitto` ännu inte kopplats, lägg till callback där (minimal ändring).

## Exempel
- Dubbeltryck på bilden zoomar in/ut.
- Dela öppnar systemets delningsmeny med kvittobilden.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Nya manifestbehörigheter.
