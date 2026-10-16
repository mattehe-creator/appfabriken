beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast de tre angivna husloggen/ui-filerna plus uppgiftsflytt ändrades; tre felplacerade kopior i reporoten borttagna.
- Koden extraherar formulärfält och förslagsväljare enligt uppgiften; skärmen behåller top bar, spara, ta bort och DatePickerDialog.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Synlig text via stringResource; valideringsfel enligt UppgiftFel.
- Angivna tester (UppgiftValideringTest, StringsNycklarTest) gröna lokalt; appmodultester använder org.junit.

## Rättat av Cursor
- Flyttade implementation från reporoten till husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/ (tomma målfiler och orefaktorerad skärm kvar i modulen).
- Tog bort UppgiftFormularFalt.kt, UppgiftFormularSkarm.kt och UppgiftForslagValjare.kt i reporoten.
- UppgiftForslagValjare: hjälptext efter valt förslag (inte sista i listan); senasteValt-state.
- UppgiftFormularSkarm: context.getString i onValj-lambda (inte stringResource utanför @Composable).
- UppgiftStartdatumFalt: OutlinedButton och saknade ikonimporter.

## Vid underkänd: försök 2
(inget)

## Frågor till Claude
- inga
