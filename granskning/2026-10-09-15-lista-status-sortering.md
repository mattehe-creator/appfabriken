beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Endast tillåtna källfiler ändrade (`KopListRad.kt`, `KopViewModel.kt`); inga filer i fel sökväg.
- Uppgiften: `KopListRad` använder `kopStatus` med rätt sträng- och färgmappning inkl. `REKLAMATION_GALLER`; garantislutrad oförändrad.
- Uppgiften: `KopViewModel` sorterar med `sorteraEfterNarmasteFrist(lista, idag)`.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- UI-text via `stringResource`, inga hårdkodade strängar.
- Angivna tester (`KopSorteringNarmasteFristTest`, `KopStatusTest`) och hela `testDebugUnitTest` gröna efter rättning.
- Testerna använder JUnit 4 (befintliga regeltester).

## Rättat av Cursor
- `KopViewModelFactory` hade raderats i slutet av `KopViewModel.kt` (orsakade CI-kompileringsfel); klassen återställd.
- Oanvända importer (`GarantiStatus`, `garantiStatus`, `sorteraEfterGaranti`) borttagna.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
