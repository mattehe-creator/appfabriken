beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Försök 2 ändrar bara MainActivity.kt och HusViewModel.kt (plus granskning/uppgiftsflytt); inga filer i fel sökväg eller FRAGA.md.
- CreateDocument("application/zip") med `sakerhetskopiaFilnamn(LocalDate.now())`; vid URI anropas `sparaSakerhetskopia`; `onHamtaSakerhetskopia` lämnas tom.
- ViewModel hämtar uppgifter/poster och bildLager på IO-tråd och anropar `Sakerhetskopia.skriv`; snackbar via `HusUiMeddelande` och befintlig `meddelanden`-kanal med strängresurser.
- Inga nya beroenden, behörigheter eller nätverksanrop; inga hårdkodade UI-strängar.
- `:husloggen:testDebugUnitTest` (inkl. `StringsNycklarTest`) grönt efter kompilering.

## Rättat av Cursor
- Snackbar-texter för säkerhetskopia hämtas med `stringResource` utanför `LaunchedEffect` (kompileringsfel @Composable).
- Import av `Sakerhetskopia`; repository-anrop flyttade in i `withContext(Dispatchers.IO)`.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
