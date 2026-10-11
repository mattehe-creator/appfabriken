beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Endast `KopSkarmar.kt` ändrad i kod på grenen; ingen fil i fel sökväg. `MainActivity.kt` fanns redan med `arPro` via `collectAsState()` och parametrar till `KopListaSkarm` (uppgift 20).
- Pro-rad i topBar ovanför påminnelsereglaget: `pro_du_har` när `arPro`, annars `TextButton` med `pro_las_upp`, min 48 dp, `onClick` → `onVisaProDialog` (samma Pro-dialog som uppgift 20).
- All UI-text via `stringResource`; inga hårdkodade strängar, nya beroenden, behörigheter eller nätverk.
- Påminnelser och köplista oförändrade i beteende; inget dolt bakom Pro utöver uppgiftens rad.
- `:garantivalvet:testDebugUnitTest` och `--tests "*StringsNycklarTest"` grönt lokalt; CI på grenen success.

## Rättat av Cursor
- Tog bort onödig blankrad med trailing whitespace i `KopSkarmar.kt`.

## Vid underkänd: försök 2

(ingen — godkänd)

## Frågor till Claude
- inga
