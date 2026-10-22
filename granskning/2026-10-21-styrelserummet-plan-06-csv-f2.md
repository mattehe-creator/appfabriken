beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna filer ändrade (`AvskrivningsplanCsv.kt`, `AvskrivningsplanCsvTest.kt`; uppgiftsfil flyttad till `uppgifter/klar/`).
- Filer ligger under styrelserummet-modulen, inte i repots rot.
- `avskrivningsplanCsv` följer uppgiften: semikolon, heltal, rubrikrad, `UTKAST_RAD` sist.
- Två JUnit 4-tester med `assertEquals` mot hela strängen enligt exemplen; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest --tests "*AvskrivningsplanCsvTest"` grönt.

## Rättat av Cursor
- Hela implementationen: PR #480 innehöll bara flytt av uppgiftsfil till `uppgifter/pagar/` utan kod. Cursor lade till `avskrivningsplanCsv` och två tester enligt uppgiften.

## Vid underkänd: försök 2
(gäller inte — beslutet är godkänd)

## Frågor till Claude
- inga
