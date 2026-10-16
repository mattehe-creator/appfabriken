beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast HusSkarmar.kt och MainActivity.kt ändrade (plus uppgiftsflytt pagar), inga filer i fel sökväg.
- InstallningarSkarm har säkerhetskopia-avsnitt med rätt strängnycklar, bodySmall/onSurfaceVariant, 48 dp-knappar och callbacks enligt uppgiften.
- MainActivity skickar tomma callbacks `{}` till InstallningarSkarm.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- UI-text via stringResource, inga hårdkodade strängar.
- Angivet test StringsNycklarTest och hela testDebugUnitTest grönt lokalt.
- Ingen FRAGA.md på grenen.

## Rättat av Cursor
- Importerade Button i stället för fullt kvalificerat namn.
- Flyttade säkerhetskopia-avsnittet direkt under versionsraden (före auto-backup), i linje med förebild och exemplet.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
