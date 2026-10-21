beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast filerna under «Ändra bara dessa filer» ändrades i uppgiftens commits (Felkoder.kt, Felmeddelanden.kt, FelmeddelandenTest.kt); inga filer i fel sökväg.
- FOR_STOR_HANDLING tillagd i Felkoder och allaKoder(); vänligt svenskt meddelande utan API eller exakt teckengräns.
- Testet forStorHandlingMeddelande verifierar icke-tomt meddelande, exempel (stor/tolkning) och förbjudna ord; JUnit 4 (org.junit).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen hårdkodad UI-text (servermodul).
- JobbKo oförändrad enligt «Gör inte».

## Rättat av Cursor
- Ersatte kotlin.test `assert` med `assertTrue` och case-insensitive «stor»/«tolkning» enligt uppgiftens exempel.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
