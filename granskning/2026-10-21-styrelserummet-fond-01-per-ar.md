beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Fondplan.kt`, `FondplanTest.kt` och uppgiftsflytt ny→pagar på grenen; inga filer i fel sökväg eller utanför listan.
- `summaPerAr` returnerar `antalAr` summor för år `franAr..franAr+antalAr-1`, ignorerar åtgärder utanför perioden, kastar vid `antalAr` utanför 1–100 och vid negativt belopp.
- `FondplanTest` jämför mot alla exempel i uppgiften inklusive tom lista och gränsfall (0, 101, belopp −1).
- JUnit 4 (`org.junit`), inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; undantagsmeddelanden i domänlogik, inga strängresurser krävdes.

## Rättat av Cursor
- Tog bort oanvänd import `assertThrows` i `FondplanTest.kt`.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
