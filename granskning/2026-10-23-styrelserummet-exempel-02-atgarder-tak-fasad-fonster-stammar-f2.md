beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- Endast ExempelData.kt och ExempelDataAtgarderGrupp1Test.kt ändrade; inga filer i fel sökväg.
- EXEMPEL_ATGARDER har exakt fyra åtgärder med namn, år och kostnad enligt tabellen; föreningskonstanter oförändrade.
- Testet grupp1TackerTakFasadFonsterStammar använder org.junit (JUnit 4) och assertEquals mot tabellens värden inklusive liststorlek 4.
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- Bygge på grenen grönt före granskning; testDebugUnitTest grönt efter Cursors implementation.

## Rättat av Cursor
- Lokal modell (försök 2) lämnade bara uppgiftsflytt pagar→pagar utan kod: EXEMPEL_ATGARDER var fortfarande emptyList() och testfilen saknades. Cursor fyllde listan med fyra ExempelAtgard-poster (val i stället för var) och skrev ExempelDataAtgarderGrupp1Test.kt enligt uppgiften.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
