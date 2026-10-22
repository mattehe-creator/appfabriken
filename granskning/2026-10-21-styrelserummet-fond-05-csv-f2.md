beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna filer ändrade (`FondplanCsv.kt`, `FondplanCsvTest.kt`; uppgiftsfil flyttad till `uppgifter/klar/`).
- Filer ligger under styrelserummet-modulen, inte i repots rot.
- `fondplanCsv` följer uppgiften: semikolon, heltal, rubrikrad, negativt utgående med minustecken, `UTKAST_RAD` sist.
- Tre JUnit 4-tester med `assertEquals` mot hela strängen enligt exemplen; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest --tests "*FondplanCsvTest"` grönt.

## Rättat av Cursor
- Hela implementationen: PR #485 innehöll bara flytt av uppgiftsfil till `uppgifter/pagar/` utan kod (samma som försök 1). Cursor lade till `fondplanCsv` och tre tester enligt uppgiften.

## Vid underkänd: försök 2
(gäller inte — beslutet är godkänd)

## Frågor till Claude
- inga
