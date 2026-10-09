beslut: underkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast tillåtna filer (`MainActivity.kt`, `KopSkarmar.kt`) plus uppgiftsflytt `ny/` → `pagar/`; inga filer i repots rot.
- Bygget rött: `MainActivity.kt` saknar `import androidx.compose.runtime.setValue` (och `mutableStateOf`) så `var behorighetNekad by mutableStateOf(...)` kompilerar inte.
- `KopSkarmar.kt` definierar en extra `KopListRad`-stub med hårdkodad text `"KopListRad stub"` — krockar med befintliga `KopListRad.kt` och skulle ersätta riktiga listrader.
- Notisbehörighet: `registerForActivityResult` och API 33-gren finns, men vid nekad behörighet sätts aldrig `behorighetNekad = true`; nekad-text kan därför inte visas enligt exemplen.
- `paminnelse_behorighet_nekad` ligger i `TopAppBar`, inte under reglaget som uppgiften kräver.
- Påminnelsereglaget visas bara när listan har poster; saknas vid tom lista.
- Strängar i UI från `strings.xml` (utom stubben); inga nya Gradle-beroenden; behörighet begärs inte vid appstart.
- Inget nytt enhetstest; befintliga tester kör inte mot reglage-beteendet (uppgiften anger bara `testDebugUnitTest`).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: reglage för påminnelser i listan

uppdrag: uppdrag/2026-10-09-paminnelser.md
försök: 2
beror-på: ingen

## Vad som saknades
- Kompileringsfel: `setValue`-import för `by mutableStateOf` i `MainActivity`.
- Duplicerad `KopListRad` i slutet av `KopSkarmar.kt` (stub med hårdkodad sträng) — ta bort helt; använd `KopListRad.kt` som redan finns.
- Vid nekad notisbehörighet: sätt `behorighetNekad` till true så nekad-texten syns; vid godkänd behörighet eller när användaren slår av påminnelser, sätt den till false.
- Visa `paminnelse_behorighet_nekad` under reglaget (synlig text), inte i appnamnsraden.
- Visa reglaget även när köplistan är tom (samma topp-/inställningsrad som när det finns poster).
- Reglage-raden ska ha minst 48 dp tryckyta totalt (t.ex. `Switch` i en rad med tillräcklig höjd/padding).

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt
- garantivalvet/src/main/res/values/strings.xml

## Uppgift
Visa ett reglage för påminnelser i köplistans toppfält (till exempel under appnamnet eller som en rad i `TopAppBar`-området).

1. **State:** Läs `paminnelserPa` från `KopViewModel` med `collectAsState()` (i `MainActivity` eller i skärmen — båda ok om callbacks skickas som nedan).
2. **Reglage:** `Switch` med rubrik och beskrivning från `paminnelse_installning_rubrik` och `paminnelse_installning_beskrivning`. Minst 48 dp tryckyta totalt.
3. **Slå på:** Anropa `viewModel.sattPaminnelser(true)` endast efter att notisbehörighet är ok på Android 13+ (`Build.VERSION.SDK_INT >= 33`). Begär `android.permission.POST_NOTIFICATIONS` med `ActivityResultContracts.RequestPermission` registrerat i `MainActivity`. Om användaren godkänner, anropa `sattPaminnelser(true)` och rensa nekad-flaggan. Om nekas, lämna reglaget av och visa `paminnelse_behorighet_nekad` under reglaget (synlig text, inte bara TalkBack).
4. **Slå av:** Anropa direkt `viewModel.sattPaminnelser(false)` utan behörighetsdialog; rensa nekad-flaggan om den var satt.
5. **Äldre Android:** Under API 33 räcker det att anropa `sattPaminnelser(true)` när användaren slår på.

Skicka callbacks från `MainActivity` till `KopListaSkarm` (`paminnelserPa`, `onPaminnelserAndras`, `behorighetNekad`) så att Compose-skärmen förblir testbar. Om `behorighetNekad` uppdateras i aktivitetens permission-callback, koppla det till Compose-state (t.ex. spara en uppdateringslambda som sätts i `setContent`).

**Tips:** Importera `androidx.compose.runtime.mutableStateOf`, `getValue` och `setValue` om du använder `by mutableStateOf`. Lägg inte till en ny `KopListRad` i `KopSkarmar.kt`.

## Exempel
- Påminnelser av, användaren slår på och godkänner behörighet → reglaget blir på.
- Påminnelser av, användaren slår på och nekar behörighet → reglaget förblir av och nekad-text visas under reglaget.
- Påminnelser på, användaren slår av → reglaget av utan dialog.
- Tom lista → reglaget syns fortfarande ovanför tom-lista-texten eller i samma inställningssektion.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Begära notisbehörighet vid appstart.
- Ändra databasschemat.
- Duplicera `KopListRad` eller hårdkoda UI-text.
```

## Frågor till Claude
- inga
