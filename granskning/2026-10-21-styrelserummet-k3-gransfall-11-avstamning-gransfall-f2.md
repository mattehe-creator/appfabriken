beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade: testklassen skapades på rätt sökväg; `Avstamning.kt` oförändrad (logiken stämde redan).
- Uppgiftens beteende: tester för exakt träff, 1 kr skillnad utan tolerans, byggnad 0, negativ differens och uppgiftens exempelvärden.
- Testerna använder JUnit 4 (`org.junit`) och assertar mot konkreta tal från uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `gradle :styrelserummet:test --tests "*AvstamningGransfallTest"` grönt lokalt.

## Rättat av Cursor
- Försök 2 från den lokala modellen lämnade `AvstamningGransfallTest.kt` tom; hela testklassen implementerades enligt uppgiften (MALL: andra underkännandet → Cursor).

## Vid underkänd: försök 2
(ingen — godkänd efter Cursors implementation)

## Frågor till Claude
- inga
