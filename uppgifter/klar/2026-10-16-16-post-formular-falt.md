# Uppgift: formulärrader och typväljare för post

uppdrag: uppdrag/2026-10-16-husloggen-loggen.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostFormularFalt.kt (ny)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostFormularSkarm.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostDetaljSkarm.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/UppgiftFormularFalt.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/UppgiftFormularSkarm.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostFormularSkarm.kt (Cursors skärm och tillstånd)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/PostValidering.kt (`PostFel`)

## Uppgift
Flytta fält-UI från Cursors postskärmar till återanvändbara composables. **Ändra inte** ViewModel-signaturer eller navigering i `MainActivity`.

### `PostFormularFalt.kt`
Composable-funktioner (signaturer får justeras så de matchar `PostFormularTillstand` i ViewModel):

- `PostVadFalt(vad, onVadAndras, fel: PostFel?, ...)`
- `PostDatumFalt(datum, onValjDatum, locale, ...)`
- `PostTypValjare(typ: PostTyp, onTypAndras, ...)`
- `PostBeloppFalt(beloppText, onBeloppAndras, fel: PostFel?, ...)`
- `PostUtforareFalt(utforare, onUtforareAndras, ...)`
- `PostUppgiftValjare(uppgifter: List<Uppgift>, valdUppgiftId: Long?, onValjUppgift: (Long?) -> Unit, ...)`
- `PostAnteckningFalt(anteckning, onAnteckningAndras, ...)`

Visa valideringsfel under rätt fält: `fel_vad_saknas`, `fel_datum_framtid`, `fel_belopp_ogiltigt`. Typväljaren ska visa `post_typ_*`-strängarna. Minsta tryckyta **48 dp** på valbara rader/chips.

### `PostFormularSkarm.kt` och `PostDetaljSkarm.kt`
Använd composables ovan i stället för inlined fält. Behåll top bar, spara, datumväljare (Material 3 `DatePickerDialog`, datum inte efter idag), ta bort med bekräftelse vid redigering.

Detaljvyn: visa alla fält (inklusive kopplad uppgifts namn om `uppgiftId` finns), knapparna `knapp_andra` och `ta_bort` med bekräftelse.

## Exempel
- Tomt vad + Spara → `fel_vad_saknas` under vad-fältet.
- Val av typ Reparation markerar rätt chip.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*PostValideringTest" --tests "*StringsNycklarTest"

## Gör inte
- Ändra `HusViewModel`, `HusRepository`, `MainActivity` eller databas.
- Lägga till beroenden.
- Hårdkoda synlig text.

utfall: godkänd 2026-10-16 (FlowRow i PostTypValjare, borttagen död PostUppgiftMeny och oanvända importer)
