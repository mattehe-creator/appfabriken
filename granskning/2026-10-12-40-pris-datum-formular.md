beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `KopFormularSkarm.kt`, `values/strings.xml` och `values-sv/strings.xml` ändrade (korrekt sökväg under `garantivalvet/`); inga filer i repots rot; `Formatering.kt` och valideringslogik orörda.
- Köpdatum: read-only `OutlinedTextField` med `formateraDatum`, kalenderikon i `trailingIcon` öppnar befintlig `DatePickerDialog`; etikett `falt_datum`.
- Pris: `OutlinedTextField` med `KeyboardType.Decimal`, suffix från `falt_pris_suffix`, etikett `falt_pris`; sparflöde via `valideraKopFormular` / `tolkaPrisTillOre` oförändrat.
- `FormularValideringTest` (JUnit 4, `org.junit`) täcker uppgiftens prisexempel (`1299,50` m.fl.) via befintliga assertEquals; inga nya testfiler.
- `:garantivalvet:testDebugUnitTest --tests "*FormularValideringTest"` grönt lokalt efter granskning.
- Inga nya beroenden, behörigheter eller nätverk.

## Rättat av Cursor
- Ersatte hårdkodad `" kr"` i prisfältet med `suffix` + `stringResource(R.string.falt_pris_suffix)` enligt uppgiften.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
