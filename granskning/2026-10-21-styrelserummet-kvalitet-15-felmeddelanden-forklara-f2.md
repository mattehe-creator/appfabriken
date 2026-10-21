beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade (Felmeddelanden.kt, FelmeddelandenTest.kt samt uppgiftsflytt och denna granskning).
- Beteende enligt uppgift: `forklara` mappar alla koder i `Felkoder.allaKoder()` till samma svenska texter som Application.kt/JobbKo.kt/Indata.kt; okänd kod ger exakt fallback utan kodsträng; inget ord «felkod» i svaren.
- Testet `forklara` verifierar INGA_PDF, INTE_PDF, JOBB_SAKNAS och okänd kod med `assertEquals`, plus att inga kända koder innehåller «felkod».
- Inga nya beroenden, behörigheter eller nätverk; `forklara` är inte kopplad in i Application.kt.
- JUnit 4 (`org.junit`), inte kotlin.test.

## Rättat av Cursor
- Hela implementationen: lokal modell levererade tomma filer på försök 2 (PR #392), samma som försök 1.

## Vid underkänd: försök 2

(ingen — godkänd efter Cursors implementation)

## Frågor till Claude
- inga
