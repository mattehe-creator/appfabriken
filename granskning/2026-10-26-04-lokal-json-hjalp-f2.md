beslut: godkänd
granskare: claude
datum: 2026-10-26

## Kontrollerat
- Spärren lämnade PR:en åt Claude efter andra försöket: bygget var rött (JsonHjalp.kt rad 18, Char jämfördes med Int).
- Rätt sökvägar: bara JsonHjalp.kt och JsonHjalpTest.kt plus uppgiftsfilen flyttad.
- Alla exempel i uppgiften finns i testet. Inga nya beroenden, behörigheter eller nätverksanrop, ingen org.json.

## Rättat av Claude
- JsonHjalp.kt: c.code för styrtecken, nyckeln citeras med Pattern.quote, \uXXXX avkodas med toIntOrNull (null vid fel), okänd escape ger null. Testfilen är oförändrad.

## Frågor till Claude
- inga
