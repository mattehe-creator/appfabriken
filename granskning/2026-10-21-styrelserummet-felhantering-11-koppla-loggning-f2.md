beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `JobbKo.kt` och `JobbKoTest.kt` ändrades (lokal modell levererade ingen kod; Cursor implementerade enligt uppgifter/MALL.md efter underkänt försök 2).
- Beteende: vid `FEL` anropas `loggJobbFel` efter sparad meta; injicerbar `loggSkriv` med standard `::println`.
- Tester (JUnit 4) verifierar jobb-id, felkod och avsaknad av personnummer, PDF-text och filnamn enligt exemplen.
- Inga nya beroenden, behörigheter eller nätverk.
- Inga hårdkodade UI-strängar (servermodul).

## Rättat av Cursor
- Hela uppgiftsimplementationen: `loggSkriv` i `JobbKo.fel`, två nya tester i `JobbKoTest`.

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
