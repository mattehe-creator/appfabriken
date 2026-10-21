beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Avskrivning.kt`, nytt `AvskrivningHeltalTest.kt` och uppgiftsflytt ny→pagar; inga filer i fel sökväg.
- `avskrivningFor` använder heltalsformeln `(2 * avskrivningsbartBelopp + n) / (2 * n)` och returnerar 0 vid `nyttjandeperiodAr <= 0`; sista årets logik oförändrad.
- `AvskrivningHeltalTest` täcker alla exempel i uppgiften med förväntade belopp; hjälpfunktion enligt spec (Tak, TAK, test, MEDEL).
- Befintliga tester i modulen (t.ex. `AvskrivningTest`) fortsätter gå igenom.
- JUnit 4 (`org.junit`), inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; inga hårdkodade användarsträngar tillagda.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
