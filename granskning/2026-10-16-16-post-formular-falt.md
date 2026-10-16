beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast PostFormularFalt.kt (ny), PostFormularSkarm.kt och uppgiftsflytt ändrades; inga filer i reporoten; PostDetaljSkarm.kt oförändrad men redan read-only med PostDetaljRad och postTypEtikett (samma paket).
- Formulärfält extraherade enligt uppgiften; valideringsfel under rätt fält; typchips med post_typ_* och tryckyta 48 dp; skärmen behåller top bar, spara, DatePickerDialog och ta bort med bekräftelse.
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen hårdkodad synlig text.
- PostValideringTest och StringsNycklarTest gröna lokalt; testDebugUnitTest grönt; appmodultester använder org.junit.
- ViewModel, MainActivity och repository oförändrade.

## Rättat av Cursor
- PostTypValjare: FlowRow i stället för Row (som före refaktoreringen).
- Tog bort oanvänd privat PostUppgiftMeny och kommentarer i PostFormularSkarm.kt; städade oanvända importer i PostFormularFalt.kt.

## Vid underkänd: försök 2
(inget)

## Frågor till Claude
- inga
