beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- Endast `MainActivity.kt` och `KopViewModel.kt` ändrade enligt uppgiften (plus uppgiftsflytt till `pagar/` från lokala modellen).
- `CreateDocument("application/zip")` med `sakerhetskopiaFilnamn(LocalDate.now())`, export via `Sakerhetskopia.skriv` och `allaKopForExport()`.
- Snackbar med `sakerhetskopia_sparad` / `sakerhetskopia_kunde_inte_lasas` vid lyckad resp. misslyckad sparning (även när `openOutputStream` returnerar null).
- `onSparaSakerhetskopia` startar kontraktet; Pro-gate lämnas till list-UI som i uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- `StringsNycklarTest` (JUnit 4) körd och grönt.

## Rättat av Cursor
- Implementerade saknade användarfeedback (StateFlow + snackbar) och felhantering i `sparaSakerhetskopia`.
- Tog bort dubbel Pro-gate i `MainActivity`.
- Tog bort oanvänd import; `this@MainActivity` i `FileProvider` efter omslag med `Box`.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
