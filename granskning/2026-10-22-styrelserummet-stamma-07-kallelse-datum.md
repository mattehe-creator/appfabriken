beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast `Stammotidplan.kt`, `StammotidplanKallelseTest.kt` och uppgiftsflytt ny→pagar→klar; korrekta sökvägar i modulen styrelserummet.
- `tidigasteKallelsedag` och `senasteKallelsedag` följer uppgiftens exempel (6 veckor respektive 2..6 veckor med IllegalArgumentException utanför).
- `StammotidplanKallelseTest` jämför mot alla exempel i uppgiften inklusive ogiltiga veckor 1 och 7.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod (felmeddelande i regler-kod är acceptabelt enligt motsvarande filer i modulen).
- JUnit 4 (`org.junit`) efter rättelse; `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- `StammotidplanKallelseTest.kt`: bytt från `kotlin.test` till JUnit 4 (orsakade kompileringsfel i CI).

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
