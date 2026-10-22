beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna filer ändrade efter rättelse (rotfiler borttagna); inga nya beroenden, behörigheter eller nätverk.
- `Stammotidplan` och `tidplan()` anropar befintliga hjälpfunktioner; validering av tomt intervall och 2..6 enligt uppgiften.
- Tester jämför datum och `stammaInomLagligFrist` mot uppgiftens exempel (inkl. augusti-stämman efter senaste stammodag).
- Tester använder JUnit 4 (`org.junit`), inte kotlin.test.
- Inga hårdkodade UI-strängar.

## Rättat av Cursor
- Tog bort `Stammotidplan.kt` och `StammotidplanTest.kt` i repots rot; lade in kod i modulens sökvägar.
- Fyllde tom `StammotidplanTest.kt` på rätt plats; bytte kotlin.test till org.junit.
- Lade `data class` och `tidplan()` i modulens `Stammotidplan.kt` utan duplicerade hjälpfunktioner; undvek namnskuggning i `tidplan`.
- Rättade förväntade datum i testerna (senaste stammodag 2026-06-30 för räkenskapsår till 2025-12-31; korrekta veckodatum för stämma 2026-08-01).

## Vid underkänd: försök 2

(inget)

## Frågor till Claude
- inga
