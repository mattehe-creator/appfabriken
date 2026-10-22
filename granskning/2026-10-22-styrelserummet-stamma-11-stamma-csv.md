beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast angivna filer ändrade (`stamma.csv`, `StammaCsvTest.kt`) plus flytt av uppgiftsfil till `pagar/` (lokal modell); inga filer i fel sökväg.
- `stamma.csv` följer uppgiften: semikolonavgränsare, rubrikrad `typ;ar;varde;enhet;kalla`, sju prisbasbelopp 2020–2026 med angivna belopp, två referensrader enligt tabellen.
- Test läser `/stamma.csv` från classpath och kontrollerar minst sju prisbasbelopp-rader och att 2026 har värde 59200.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod eller hårdkodade användarsträngar i appen.
- Ingen databasändring.

## Rättat av Cursor
- `StammaCsvTest.kt`: `org.junit` / `org.junit.Assert.assertTrue` i stället för JUnit Jupiter och `kotlin.test` (orsak till CI-fel).

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
