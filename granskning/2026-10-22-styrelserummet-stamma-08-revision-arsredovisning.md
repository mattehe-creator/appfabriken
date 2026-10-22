beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast angivna filer ändrade, korrekta sökvägar under styrelserummet/
- Beteendet stämmer: minus 3 veckor respektive minus 2 veckor från stämmodatum
- Testerna jämför mot exemplet 2026-05-15 → 2026-04-24 och 2026-05-01
- Inga nya beroenden, behörigheter eller nätverk
- Ingen hårdkodad UI-text (ren regellogik)
- Omfattning inom uppgiftens gränser

## Rättat av Cursor
- StammotidplanHandlingarTest.kt: org.junit.Test och org.junit.Assert.assertEquals (kotlin.test gav kompileringsfel)

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
