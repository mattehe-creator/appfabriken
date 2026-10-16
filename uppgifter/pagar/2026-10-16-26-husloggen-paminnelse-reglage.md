# Uppgift: reglage för påminnelser i Att göra

uppdrag: uppdrag/2026-10-16-husloggen-paminnelser.md
försök: 1
beror-på: cursor:2026-10-16-husloggen-paminnelser

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt (notisbehörighet)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt (reglage-mönster)
- husloggen/src/main/res/values/strings.xml

## Uppgift
Visa ett reglage för **globala** påminnelser högst upp i Att göra-fliken (`AttGoraFlik`), ovanför uppgiftslistan (eller ovanför tom-lista-texten när det inte finns uppgifter).

1. **State:** Läs `paminnelserPa` från `HusViewModel` med `collectAsState()` i `MainActivity` och skicka till `HusStartSkarm` → `AttGoraFlik`.
2. **Reglage:** `Switch` med rubrik och beskrivning från `paminnelse_installning_rubrik` och `paminnelse_installning_beskrivning`. Minst 48 dp tryckyta totalt (t.ex. `tryckyta48()` på raden).
3. **Slå på:** Anropa `viewModel.sattPaminnelser(true)` endast efter godkänd notisbehörighet på Android 13+ (`Build.VERSION.SDK_INT >= 33`). Begär `POST_NOTIFICATIONS` med `ActivityResultContracts.RequestPermission` registrerat i `MainActivity`. Vid godkännande: `sattPaminnelser(true)` och rensa nekad-flagga. Vid nekan: reglaget förblir av och visa `paminnelse_behorighet_nekad` under reglaget (synlig text).
4. **Slå av:** Anropa `viewModel.sattPaminnelser(false)` direkt; rensa nekad-flaggan.
5. **Äldre Android:** Under API 33 räcker `sattPaminnelser(true)` när användaren slår på.

Skicka `paminnelserPa`, `onPaminnelserAndras` och `behorighetNekad` som callbacks så att Compose-skärmen förblir testbar. Koppla `behorighetNekad` till Compose-state från permission-callback (t.ex. `mutableStateOf` i aktiviteten, uppdateras i `setContent`).

Importera `mutableStateOf`, `getValue`, `setValue` om du använder `by mutableStateOf`.

## Exempel
- Påminnelser av, användaren slår på och godkänner → reglaget på.
- Påminnelser av, användaren nekar behörighet → reglaget av, nekad-text under reglaget.
- Tom uppgiftslista → reglaget syns fortfarande.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Begära notisbehörighet vid appstart.
- Ändra databasschemat.
