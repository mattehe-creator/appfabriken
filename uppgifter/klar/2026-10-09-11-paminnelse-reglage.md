# Uppgift: reglage för påminnelser i listan

uppdrag: uppdrag/2026-10-09-paminnelser.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/res/values/strings.xml

## Uppgift
Visa ett reglage för påminnelser i köplistans toppfält (till exempel under appnamnet eller som en rad i `TopAppBar`-området).

1. **State:** Läs `paminnelserPa` från `KopViewModel` med `collectAsState()`.
2. **Reglage:** `Switch` (eller motsvarande) med rubrik och beskrivning från `paminnelse_installning_rubrik` och `paminnelse_installning_beskrivning`. Minst 48 dp tryckyta totalt.
3. **Slå på:** Anropa `viewModel.sattPaminnelser(true)` endast efter att notisbehörighet är ok på Android 13+ (`Build.VERSION.SDK_INT >= 33`). Begär `android.permission.POST_NOTIFICATIONS` med `ActivityResultContracts.RequestPermission` registrerat i `MainActivity`. Om användaren godkänner, anropa `sattPaminnelser(true)`. Om nekas, lämna reglaget av och visa `paminnelse_behorighet_nekad` under reglaget (synlig text, inte bara TalkBack).
4. **Slå av:** Anropa direkt `viewModel.sattPaminnelser(false)` utan behörighetsdialog.
5. **Äldre Android:** Under API 33 räcker det att anropa `sattPaminnelser(true)` när användaren slår på.

Skicka callbacks från `MainActivity` till `KopListaSkarm` (t.ex. `paminnelserPa`, `onPaminnelserAndras`, `behorighetNekad`) så att Compose-skärmen förblir testbar.

## Exempel
- Påminnelser av, användaren slår på och godkänner behörighet → reglaget blir på.
- Påminnelser av, användaren slår på och nekar behörighet → reglaget förblir av och nekad-text visas.
- Påminnelser på, användaren slår av → reglaget av utan dialog.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Begära notisbehörighet vid appstart.
- Ändra databasschemat.

utfall: underkänd 2026-10-09 av Cursor (PR #28). Ersatt av 2026-10-09-11-paminnelse-reglage-f2.md
