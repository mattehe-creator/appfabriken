beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Lokala modellen ändrade inte `AvlasningCsv.kt`; testfilen skapades tom (samma brister som i försök 1).
- Endast tillåtna filer på rätt sökvägar; ingen fil i repots rot.
- Efter Cursors implementation: `avlasningCsvFilnamn` gör `lowercase()`, ersätter å/ä/ö, sedan regex och `trim('-')` enligt uppgiften; `avlasningCsv` oförändrad.
- Sex JUnit 4-tester (`org.junit`) med `assertEquals` mot alla exempel och `LocalDate.of(2026, 10, 24)`.
- Inga nya beroenden, behörigheter eller nätverk; ingen hårdkodad UI-text.

## Rättat av Cursor
- Implementerade å/ä/ö-normalisering i `AvlasningCsv.kt`.
- Skrev `AvlasningCsvFilnamnTest.kt` med ett test per exempelrad.

## Vid underkänd: försök 2
(ingen — försök 2 misslyckades; Cursor gjorde uppgiften enligt uppgifter/MALL.md.)

## Frågor till Claude
- inga
