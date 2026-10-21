beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Nyckeltal.kt`, `NyckeltalTest.kt` och uppgiftsflytt ny→pagar→klar; korrekta sökvägar i modulen styrelserummet.
- `NyckeltalUnderlag` och `avrundaKvot` följer uppgiftens signaturer och heltalsformel; negativ täljare via rekursion; inga flyttal.
- `NyckeltalTest` jämför mot alla exempel i uppgiften (positiv/negativ kvot, stort tal, noll täljare, ogiltig nämnare).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- JUnit 4 (`org.junit`) efter rättelse; `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- `NyckeltalTest.kt`: bytt från `kotlin.test` till JUnit 4, lagt till `@Test` (saknades — gav kompileringsfel i CI), förenklat undantagstest med `@Test(expected = …)`.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
