# Uppgift: HA-fälten i inställningarna och rätt etikett på typ-knapparna (Röstfångst)

uppdrag: uppdrag/claude/2026-10-27-rostfangst-ha-reserv.md
försök: 1
beror-på: 2026-10-27-06-ha-viewmodel-f2.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ui/RostfangstSkarmar.kt
- rostfangst/src/main/res/values/strings.xml
- rostfangst/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ui/RostfangstViewModel.kt (`InstallningarUi`, `sparaHaInstallningar`)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/StringsNycklarTest.kt

## Uppgift
1. **Fel etikett i `ForslagKort`:** i `ForslagTyp.entries.forEach { valt -> FilterChip(... label = { Text(stringResource(typ.labelRes())) }) }` visar alla knappar den valda typens namn. Ändra `typ.labelRes()` till `valt.labelRes()`. Inget annat i `ForslagKort` ändras.
2. **HA i `InstallningarSkarm`:** efter knappen Spara för API-nyckeln, lägg till:
   - En rubrik `Text(stringResource(R.string.ha_rubrik))`.
   - Tre `OutlinedTextField` med `remember(ui)` som de befintliga: adress (`ui.haAdress`, etikett `R.string.ha_adress`), token (`ui.haToken`, etikett `R.string.ha_token`, `visualTransformation = PasswordVisualTransformation()`), att-göra-lista (`ui.haTodo`, etikett `R.string.ha_todo`).
   - En hjälptext `Text(stringResource(R.string.ha_hjalp))`.
   - En knapp `Button(onClick = { viewModel.sparaHaInstallningar(haAdress, haToken, haTodo) }) { Text(stringResource(R.string.spara)) }`.
3. **Strängar**, samma nycklar i båda filerna:

| nyckel | values (engelska) | values-sv |
|---|---|---|
| ha_rubrik | Home Assistant | Home Assistant |
| ha_adress | Address (http://… or https://…) | Adress (http://… eller https://…) |
| ha_token | Long-lived access token | Long-lived access token |
| ha_todo | To-do list (e.g. todo.shopping_list) | Att göra-lista (t.ex. todo.shopping_list) |
| ha_hjalp | Create the token in Home Assistant: Profile → Security → Long-lived access tokens. Leave everything empty to turn it off. | Skapa token i Home Assistant: Profil → Säkerhet → Long-lived access tokens. Lämna allt tomt för att stänga av. |

Importera `androidx.compose.ui.text.input.PasswordVisualTransformation`.

## Exempel
- Förslaget är KALENDER: knapparna visar Kalender, Påminnelse, Att göra, Anteckning, Fråga, Styr hemmet (var och en sitt namn), Kalender markerad.
- Inställningar: fälten visar sparade värden; token visas som punkter.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer eller andra delar av skärmarna.
- Hårdkoda text i Kotlin.
- Lägga till beroenden.
