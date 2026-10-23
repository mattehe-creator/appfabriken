beslut: godkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- Grenen hade endast commit «Uppgift startad» utan kod (samma som vid underkänd försök 1); uppgiften var `försök: 2`.
- Efter Cursors implementation: endast `ExempelData.kt`, `ExempelDataAtgarderGrupp1Test.kt`, nya `ExempelDataAtgarderGrupp3Test.kt` enligt uppgift, plus en rad i `ExempelDataAtgarderGrupp2Test.kt` för grön modultest; inga filer i repots rot.
- `EXEMPEL_ATGARDER` har tolv poster; grupp 1 och 2 oförändrade; grupp 3 enligt tabellen.
- `ExempelDataAtgarderGrupp3Test` kontrollerar storlek, fyra poster och totalsumma `26_800_000L`; JUnit 4 (`org.junit`).
- `ExempelDataAtgarderGrupp1Test` förväntar 12 poster.
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hela uppgiftsimplementationen (försök 2 utan kod från lokal modell): fyra nya `ExempelAtgard`, nytt `ExempelDataAtgarderGrupp3Test`, storlek 12 i grupp 1-test.
- `ExempelDataAtgarderGrupp2Test`: totalsumma via `take(8)` så kumulativ kontroll 23_300_000L inte bryts när grupp 3 finns.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
