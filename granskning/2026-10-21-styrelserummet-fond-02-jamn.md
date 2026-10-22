beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Fondplan.kt`, `FondplanTest.kt` och uppgiftsflytt ny→pagar på grenen; inga filer i fel sökväg eller utanför listan.
- `jamnAvsattning` beräknar `behov` som summan av `summaPerAr` minus `ingaendeFond`, returnerar 0 vid `behov <= 0`, annars takavrundning `(behov + antalAr - 1) / antalAr` utan flyttal.
- `FondplanTest` jämför alla fyra exempel i uppgiften med samma åtgärdslista som uppgift 01.
- JUnit 4 (`org.junit`), inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; endast domänlogik i `:styrelserummet`.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
