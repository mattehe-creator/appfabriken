beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast angivna filer ändrade (FondIndexering.kt, FondIndexeringTest.kt; uppgiftsfilen flyttas av granskningen).
- Koden implementerar `indexeratBelopp` och `indexeradeAtgarder` enligt uppgiften, heltalsformeln utan flyttal.
- Tester jämför mot uppgiftens exempelvärden; inga nya beroenden, behörigheter eller nätverk.
- Inga hårdkodade UI-strängar; undantagsmeddelande i regellager följer övriga reglerfiler.
- Databasschema oförändrat.
- Tester använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- `FondIndexeringTest`: bytte från `kotlin.test` (orsakade kompileringsfel i CI) till JUnit 4 som övriga styrelserummet-tester.
- Ersatte tester med fel förväntade belopp (enkel procentuell uppräkning i stället för uppgiftens årsvisa avrundning) med test som följer exemplen och verifierar `ar`/`beskrivning`.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
