beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna filer ändrade (ForbrukningPrognos.kt, ForbrukningPrognosTest.kt); inga filer i fel sökväg.
- `arsprognos` följer uppgiften: första/sista punkt, minst 14 dagar, negativ diff → null, heltalsformeln utan flyttal.
- Tester täcker uppgiftens exempel (normalfall, en avläsning, för kort period, negativ diff) plus rimliga tillägg.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Testerna använder JUnit 4 (`org.junit`).

## Rättat av Cursor
- Test: `kotlin.test`-assertioner → `org.junit.Assert`; Long-literal i `assertEquals` för normalfallet.
- Test: fel förväntat värde i `testForstaAvlasningInomAr` (26071 enligt formeln, inte 2469388).

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
