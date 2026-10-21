beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade (Felkoder.kt, FelkoderTest.kt samt uppgiftsflytt och denna granskning).
- Beteende enligt uppgift: 19 `const val`-koder, värde = namn, `allaKoder()` returnerar alla.
- Testet `kartlaggning` verifierar exemplen (icke-tom FOR_MANGA_JOBB, minst 15 koder, storlek matchar konstanter via reflektion).
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen svensk användartext i Felkoder.kt; JUnit 4 (`org.junit`).

## Rättat av Cursor
- Hela implementationen: lokal modell levererade ingen kod på försök 2 (PR #339), samma som försök 1.

## Vid underkänd: försök 2

(ingen — godkänd efter Cursors implementation)

## Frågor till Claude
- inga
