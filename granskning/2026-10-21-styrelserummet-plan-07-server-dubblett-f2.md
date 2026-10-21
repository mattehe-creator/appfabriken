beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Uppgiftens commits ändrar endast `CsvExport.kt` och `CsvExportServerTest.kt` (grenen innehåller även tidigare mergade flextid-filer som inte hör till denna uppgift).
- Privat `bokfortVarde` borttagen; anropar `se.tmconnect.styrelserummet.regler.bokfortVarde` som i uppgiften.
- Inga kolumn- eller rubrikändringar i `CsvExport.kt` utöver import/bytet av beräkningsfunktion.
- Tester använder JUnit 4 (`org.junit`) och kontrollerar `;120000;`, `;300000;` och `;0;` enligt exemplen.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen hårdkodad UI-text (servermodul, CSV-export).
- `:styrelserummet-server:test` grönt lokalt efter rättelse.

## Rättat av Cursor
- Import av `UTKAST_RAD` hade tagits bort tillsammans med `avskrivningFor` — återställd (CI/kompilering failade annars).
- Tog bort oanvänd import `Komponent`.
- Test: assert att sista raden är `UTKAST_RAD`, enligt uppgiftens exempel.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
