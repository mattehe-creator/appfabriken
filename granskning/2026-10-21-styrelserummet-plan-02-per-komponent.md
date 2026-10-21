beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #458 ändrar endast `Avskrivningsplan.kt`, `AvskrivningsplanTest.kt` och uppgiftsflytt pagar; inga filer i fel sökväg.
- `planForKomponent` följer spec: en rad per år, tom lista vid `antalAr <= 0`, `IllegalArgumentException` vid `antalAr > 100`, använder `avskrivningFor` och `bokfortVarde`.
- Tester jämför mot uppgiftens beteende (inkl. före nyttjandeperiod, noll år, >100 år); bokfört värde vid årets slut enligt `bokfortVarde` (uppgiftens sifferexempel hade 60 000 för 2028 men korrekt värde är 30 000).
- JUnit 4 (`org.junit`), inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod; inga hårdkodade användarsträngar tillagda.

## Rättat av Cursor
- `AvskrivningsplanTest`: JUnit 4 i stället för JUnit Jupiter/kotlin.test; `Komponent` med obligatoriska fält enligt förebild; förväntat bokfört värde 2028 = 30 000 kr.

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
