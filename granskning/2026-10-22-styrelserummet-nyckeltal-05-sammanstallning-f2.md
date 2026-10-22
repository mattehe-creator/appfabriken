beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Grenen ändrar för denna uppgift enbart de två tillåtna filerna under `styrelserummet/.../regler/` (korrekt sökväg, inga filer i rot).
- `nyckeltalRader` returnerar sju rader i rätt ordning, anropar funktioner i `Nyckeltal.kt`, formaterar kr/kvm via `kronor`, tiondelar/procent enligt uppgiften, `null` som `\u2013`; inga flyttal.
- `NyckeltalRaderTest` använder JUnit 4 och jämför hela listan mot exempelsträngarna respektive alla `\u2013` vid noll-yta/intäkter.
- Inga nya beroenden, behörigheter eller nätverk; etiketter är uppgiftsdata i regler-lagret, inte Compose-UI.
- `:styrelserummet:testDebugUnitTest` grönt lokalt efter Cursors implementation.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- Hela innehållet i `NyckeltalRader.kt` och `NyckeltalRaderTest.kt` (lokal modell försök 2 lämnade båda filerna tomma).

## Vid underkänd: försök 2
( ej tillämpligt )

## Frågor till Claude
- inga
