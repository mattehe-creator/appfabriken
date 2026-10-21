beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: lokala modellen ändrade bara flytt till `pagar/`; inga kodfiler. Cursor lade till endast `GrupperaNamn.kt` och `GrupperaNamnTest.kt` enligt uppgiften; inga filer i repots rot.
- Nyckelord hiss, tvättstuga, garage, asfalt samt gård/mark som eget ord (som `el`) enligt uppgiften och exemplen; befintliga tester oförändrade; samma vinnarlogik (lägst index, längre ord vid lika index).
- `gissaGruppHissTvattMark` med alla fyra exemplen; JUnit 4 (`org.junit`); inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hel implementation efter försök 2 utan kod (nyckelord, helordsmatchning för `gård`/`mark` så `Elisabeths trädgård` fortfarande ger null, testmetod `gissaGruppHissTvattMark`).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
