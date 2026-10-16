# Uppgift: formulärrader och förslagsväljare

uppdrag: uppdrag/2026-10-16-husloggen-uppgifter.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/UppgiftFormularFalt.kt (ny)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/UppgiftForslagValjare.kt (ny)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/UppgiftFormularSkarm.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/UppgiftFormularSkarm.kt (Cursors skärm och tillstånd)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt (datumfält och chips)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftsForslag.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftValidering.kt

## Uppgift
Flytta och strukturera UI från `UppgiftFormularSkarm.kt` till återanvändbara composables. **Ändra inte** ViewModel-signaturer eller navigering i `MainActivity`.

### `UppgiftForslagValjare.kt`
- `@Composable fun UppgiftForslagValjare(onValj: (UppgiftsForslag) -> Unit, modifier: Modifier = Modifier)`
- Visar rubrik `R.string.forslag_rubrik` och valbara rader/chips för varje post i `UPPGIFTSFORSLAG`.
- Vid val anropas `onValj` med hela förslaget.
- Hjälptext under namnet när `bestamdAv == KOMMUNEN` → `R.string.enligt_kommunen`, när `TILLVERKAREN` → `R.string.enligt_tillverkaren`.
- Minsta tryckyta 48 dp.

### `UppgiftFormularFalt.kt`
Composable-funktioner (signaturer kan justeras om de matchar skärmens tillstånd):

- `UppgiftNamnFalt(namn, onNamnAndras, fel: UppgiftFel?, ...)`
- `UppgiftIntervallFalt(intervallText, onIntervallAndras, fel: UppgiftFel?, ...)`
- `UppgiftStartdatumFalt(datum, onValjDatum, locale, ...)`
- `UppgiftPaminnelseFalt(paminnelsePa, onPaminnelseAndras, ...)`
- `UppgiftAnteckningFalt(anteckning, onAnteckningAndras, ...)`

Visa felfält enligt `UppgiftFel` med strängarna `fel_namn_saknas`, `fel_intervall_saknas`, `fel_intervall_ogiltigt` under rätt fält. Datumväljare som Material 3 `DatePickerDialog` (samma mönster som i förebilden).

### `UppgiftFormularSkarm.kt`
Använd composables ovan i stället för inlined fält. Behåll top bar, spara, ta bort med bekräftelse och `UppgiftForslagValjare` överst.

## Exempel
- Tryck på "Sotning" fyller namn och intervall 12 via `onValj` (skärmen uppdaterar tillstånd som idag).
- Tomt namn + Spara visar `fel_namn_saknas` under namnfältet.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*UppgiftValideringTest" --tests "*StringsNycklarTest"

## Gör inte
- Ändra `HusViewModel`, `HusRepository`, `MainActivity` eller databas.
- Lägga till beroenden.
- Hårdkoda synlig text (använd `stringResource`).
