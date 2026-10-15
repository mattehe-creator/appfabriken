beslut: godkänd
granskare: cursor
datum: 2026-10-15

## Kontrollerat
- Endast `KopFormularSkarm.kt` och flytt av uppgiftsfil (ny → pågår) ändrade; inga filer i fel sökväg.
- Beteendet följer uppgiften: `garantiAndrad`/`forslagGaranti`, förslag från `forslag.garantiManader` när användaren inte ändrat, chip eller Annat, hint-text med `R.string.kvitto_forslag`.
- Befintliga tester (`ButikSkarmbilder`, `FormularValidering`, hela `testDebugUnitTest`) gröna efter rättning.
- Inga nya beroenden, behörigheter eller nätverk.
- Inga hårdkodade UI-strängar; tester använder JUnit 4.

## Rättat av Cursor
- Flyttade `valdChipIndex` och `annatText` (samt `chipManader`) före `LaunchedEffect(kvittoUri, lasIgen)` så garantiförslaget kan sätta dem; lokala modellen refererade till variabler som deklarerades senare (kompileringsfel i CI).

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
