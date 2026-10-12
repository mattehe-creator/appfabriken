beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `KopDetaljSkarm.kt`, `KopListRad.kt` och `FormateringTest.kt` ändrade (korrekt sökväg under `garantivalvet/`); inga filer i repots rot; `Formatering.kt` och övrig appkod orörda.
- `LocalConfiguration.current.locales[0]` läses en gång per composable och skickas till alla `formateraDatum`/`formateraPrisKr` i detalj- och listraden; kvittoförhandsvisningen (`kvittoUri`-block, `BitmapFactory`, m.m.) oförändrad jämfört med main.
- `FormateringTest` (JUnit 4, `org.junit`) behåller befintliga svenska tester och täcker uppgiftens fyra locale-exempel efter granskning.
- `gradle testDebugUnitTest` grönt lokalt (`FormateringTest` och hela portföljen).
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar tillagda (fortfarande `stringResource` för etiketter).

## Rättat av Cursor
- Lade till `assertEquals` för svenska exempel (`1 aug. 2026`, `1 234,50 kr`) i `FormateringTest`; försök 2 hade bara lagt till engelska/US-exemplen.

## Frågor till Claude
- inga
