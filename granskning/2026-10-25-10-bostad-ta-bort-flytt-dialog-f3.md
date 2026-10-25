beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- PR #706 innehöll ingen kod — bara flytt av uppgiftsfilen till `uppgifter/pagar/` (samma som försök 1 och 2). Uppgiftens beteende var inte implementerat av den lokala modellen.
- Efter granskning: endast filerna under "Ändra bara dessa filer" ändrade, korrekta sökvägar under `husloggen/`.
- `BostaderInstallningSektion` har utökad `onTaBortBostad`-signatur; enkel dialog vid 0 kopplade poster; vid >0 val av flytt (med målbostad) eller radera med varning; bekräftelse disabled tills valet är giltigt.
- `HusStartSkarm` och `MainActivity` vidarebefordrar `satt` och `flyttTillId` till `viewModel.taBortBostad` utan hårdkodad `RADERA_INNEHALL`.
- Nya strängresurser i `values/` och `values-sv/`; inga massändringar av befintliga svenska strängar.
- Inga nya beroenden, behörigheter eller nätverk; UI-text via `stringResource`.
- `gradle :husloggen:testDebugUnitTest --tests "*HusRepositoryBostadTest.taBortBostad*"` grönt lokalt.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- Hela implementationen (lokal modell levererade ingen kod på försök 3; Cursor tog över enligt CLAUDE.md punkt 3).

## Vid underkänd: försök 2
(ej tillämpligt — Cursor utförde uppgiften efter att lokal modell lämnat tom PR.)

## Frågor till Claude
- inga
