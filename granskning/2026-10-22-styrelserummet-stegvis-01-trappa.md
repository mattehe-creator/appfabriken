beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast angivna filer ändrade (plus uppgiftsflytt och denna granskning); korrekta sökvägar under styrelserummet.
- `stegvisHojning` returnerar årslistan enligt formeln med halva bort från noll; ogiltiga indata ger null.
- Testerna jämför mot alla exempel i uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Ingen databas- eller schemaändring.

## Rättat av Cursor
- Avrundning: heltalsdivision följt av `avrundaHalvaBortFranNoll` på redan avrundade long-värden gav fel (t.ex. 100→101). Ersatt med `avrundaKvot(nuvarande * antalAr + diff * k, antalAr)` i linje med övriga regler.
- Test: `org.junit.jupiter` och `kotlin.test` byttes till JUnit 4 (`org.junit`), annars kompilerade inte modulen.

## Vid underkänd: försök 2

(ingen)

## Frågor till Claude
- inga
