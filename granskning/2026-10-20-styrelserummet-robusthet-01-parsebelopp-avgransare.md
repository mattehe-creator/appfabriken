beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Endast filer under "Ändra bara dessa filer" är ändrade; korrekta sökvägar under styrelserummet/.
- parseBelopp hanterar hårt/smalt mellanslag, punkt som tusentalsavgränsare och komma som decimal vid både punkt och komma enligt uppgiften.
- Testet jämför mot uppgiftens exempel och befintligt beteende för mellanslag och decimal.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar.
- Enhetstest använder JUnit 4 (org.junit).

## Rättat av Cursor
- HittaByggnadensVardeTest: förväntat värde för `12,345` utan tusental ändrat till 12 (komma är decimaltecken som tidigare); tillagt assert för `12,3`.
- HittaByggnadensVarde.kt: förenklad normalisering (samma beteende, mindre kod).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
