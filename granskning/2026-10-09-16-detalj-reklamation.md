beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Endast tillåten fil ändrad (`KopDetaljSkarm.kt`) plus uppgiftsflytt `ny/` → `pagar/`; inga filer i fel sökväg.
- Reklamationsblocket efter garantiraden följer uppgiften: datum + rubrik/förklaring/vägledning när `reklamationSlut` finns; `detalj_reklamation_aldre_lag` för köp före 2022-05-01; inget extra när slutdatum saknas trots ny lag.
- UI-text via `stringResource`; inga nya beroenden, behörigheter eller nätverk.
- Angivna tester (`KopStatusTest`, `StringsNycklarTest`) gröna lokalt efter granskning.
- Garanti-statusraden saknade `kopStatus` enligt uppgiften (fanns kvar som `garantiStatus`).

## Rättat av Cursor
- Garantiraden använder `kopStatus` med samma etiketter och färger som i uppgift 15 (inkl. `status_reklamation_galler`).
- Import av `KONSUMENTKOP_IKRAFT` i stället för fullt kvalificerat namn.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
