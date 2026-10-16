beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast `husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt` ändrad i kod (plus uppgiftsflytt `ny/` → `pagar/` på grenen); korrekt modulsökväg, inga filer i repots rot.
- `LaunchedEffect` för `viewModel.meddelanden`: `UppgiftMarkeradGjord` → snackbar med `uppgift_markerad_gjord`; `UppgiftMarkeradGjordPostEjSparad` → snackbar med `uppgift_gjord_post_ej_sparad` via `stringResource` (matchar uppgiftens exempel vid 15 poster utan Pro).
- ViewModel och repository orörda; inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade UI-strängar; befintlig strängresurs används.
- `:husloggen:testDebugUnitTest --tests "*HusRepositoryTest"` grönt lokalt; CI på grenen success.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

(ingen — godkänd)

## Frågor till Claude
- inga
