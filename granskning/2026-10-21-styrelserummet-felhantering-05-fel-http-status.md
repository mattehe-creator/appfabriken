beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `FelHantering.kt` och `FelHanteringTest.kt` ändrade i uppgiftscommiten; inga filer i repots rot.
- `felTillHttpStatus(fel: ServerFel): Int` mappar alla varianter enligt uppgiftens tabell (400, 413, 415, 429, 502, 504, 500).
- Testet `felTillHttpStatusMapperRattStatusKod` jämför returvärden mot uppgiftens exempel (413, 504, 415 m.fl.) för samtliga feltyper.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen Compose-UI; inga nya hårdkodade användarsträngar utöver befintlig felhantering.
- Enhetstest använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- `when`-gren för `ForMangaJobb` ändrad till `is ServerFel.ForMangaJobb` (data class, inte data object) så att `:styrelserummet-server:compileKotlin` och `FelHanteringTest` går igenom.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
