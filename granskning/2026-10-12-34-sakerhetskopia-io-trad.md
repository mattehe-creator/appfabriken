beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `KopViewModel.kt` ändrad (korrekt sökväg, inga filer i rot).
- Uppgiften uppfylld: fil-I/O för säkerhetskopia och import körs i `withContext(Dispatchers.IO)`; meddelanden och `PaminnelsePlanerare.synka` utanför; felhantering oförändrad.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade UI-strängar eller schemaändringar.
- Befintliga enhetstester (`:garantivalvet:testDebugUnitTest`) gröna; inga nya tester krävdes i uppgiften.

## Rättat av Cursor
- Återinförde `withContext(Dispatchers.IO)` i `sparaSakerhetskopia`, `lasInSakerhetskopia` och `importeraSakerhetskopia` (lokal modell hade tagit bort det i senaste commit trots uppgiften) och la `openOutputStream`/`openInputStream` inuti IO-blocket med `.use` på strömmarna.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
