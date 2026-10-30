beslut: underkänd
granskare: claude
datum: 2026-10-30

## Kontrollerat
- PR:en ändrar bara uppgiftsfilens plats (ny till pagar). Ingen av de tre tillåtna filerna är ändrad, andra försöket i rad.
- Uppgiften är liten (3 filer, cirka 35 rader) men lokala modellen levererar ingen kod. Försök 3 skärper stegen till exakta kodrader.
- Beroendet 06-ha-viewmodel-f3 är mergat (PR #795), så `sparaHaInstallningar` och `InstallningarUi.haAdress/haToken/haTodo` finns.

## Rättat av Claude
- inget

## Vid underkänd: försök 2
# Uppgift: HA-fälten i inställningarna och rätt etikett på typ-knapparna (Röstfångst)

## Vad som saknades
- Inga filer var ändrade i försök 1 och 2. Läs "Uppgift" nedan och gör ändringarna i de tre filerna. Svara inte med text utan ändra filerna. Börja med steg 1 (en rad), sedan steg 3 (strängarna), sist steg 2.

uppdrag: uppdrag/claude/2026-10-27-rostfangst-ha-reserv.md
försök: 3
beror-på: 2026-10-27-06-ha-viewmodel-f3.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ui/RostfangstSkarmar.kt
- rostfangst/src/main/res/values/strings.xml
- rostfangst/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ui/RostfangstViewModel.kt (`InstallningarUi`, `sparaHaInstallningar(adress, token, todo)`)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/StringsNycklarTest.kt

## Uppgift
1. **Fel etikett i `ForslagKort` (rad 168):** byt `label = { Text(stringResource(typ.labelRes())) },` mot `label = { Text(stringResource(valt.labelRes())) },`. Inget annat i `ForslagKort` ändras.
2. **HA i `InstallningarSkarm`:** under `var modell by remember(ui) { mutableStateOf(ui.modell) }` lägg till tre rader:
   ```
   var haAdress by remember(ui) { mutableStateOf(ui.haAdress) }
   var haToken by remember(ui) { mutableStateOf(ui.haToken) }
   var haTodo by remember(ui) { mutableStateOf(ui.haTodo) }
   ```
   Direkt efter den första `Button(onClick = { viewModel.sparaInstallningar(nyckel, modell) }) { ... }` (före `Text(stringResource(R.string.fangster_per_dag_rubrik))`) lägg till:
   ```
   Text(stringResource(R.string.ha_rubrik))
   OutlinedTextField(
       value = haAdress,
       onValueChange = { haAdress = it },
       label = { Text(stringResource(R.string.ha_adress)) },
       modifier = Modifier.fillMaxWidth(),
   )
   OutlinedTextField(
       value = haToken,
       onValueChange = { haToken = it },
       label = { Text(stringResource(R.string.ha_token)) },
       visualTransformation = PasswordVisualTransformation(),
       modifier = Modifier.fillMaxWidth(),
   )
   OutlinedTextField(
       value = haTodo,
       onValueChange = { haTodo = it },
       label = { Text(stringResource(R.string.ha_todo)) },
       modifier = Modifier.fillMaxWidth(),
   )
   Text(stringResource(R.string.ha_hjalp))
   Button(onClick = { viewModel.sparaHaInstallningar(haAdress, haToken, haTodo) }) {
       Text(stringResource(R.string.spara))
   }
   ```
   Lägg till importen `import androidx.compose.ui.text.input.PasswordVisualTransformation` bland de andra `androidx.compose.ui`-importerna.
3. **Strängar:** lägg till fem `<string name="...">`-rader före `</resources>` i båda strings-filerna, samma nycklar:

| nyckel | values (engelska) | values-sv |
|---|---|---|
| ha_rubrik | Home Assistant | Home Assistant |
| ha_adress | Address (http://… or https://…) | Adress (http://… eller https://…) |
| ha_token | Long-lived access token | Long-lived access token |
| ha_todo | To-do list (e.g. todo.shopping_list) | Att göra-lista (t.ex. todo.shopping_list) |
| ha_hjalp | Create the token in Home Assistant: Profile → Security → Long-lived access tokens. Leave everything empty to turn it off. | Skapa token i Home Assistant: Profil → Säkerhet → Long-lived access tokens. Lämna allt tomt för att stänga av. |

## Exempel
- Förslaget är KALENDER: knapparna visar Kalender, Påminnelse, Att göra, Anteckning, Fråga, Styr hemmet (var och en sitt namn), Kalender markerad.
- Inställningar: fälten visar sparade värden; token visas som punkter.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer eller andra delar av skärmarna.
- Hårdkoda text i Kotlin.
- Lägga till beroenden.

## Frågor till Claude
- inga
