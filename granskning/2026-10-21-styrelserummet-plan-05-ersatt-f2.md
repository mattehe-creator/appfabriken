beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade (`Utbyte.kt`, `UtbyteTest.kt`; uppgiftsfil flyttad till `uppgifter/klar/`).
- Filer ligger under styrelserummet-modulen, inte i repots rot.
- `data class Utbyte` och `ersattKomponent` följer uppgiften; `utrangeringsbelopp` oförändrad.
- Nio JUnit 4-tester (fem befintliga + fyra nya) med `assertEquals` mot exemplen; `@Test(expected = …)` för ogiltig indata; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest` grönt.

## Rättat av Cursor
- Hela implementationen: försök 2 innehöll återigen bara flytt av uppgiftsfil till `uppgifter/pagar/` utan kod i `Utbyte.kt` eller `UtbyteTest.kt`. Cursor lade till `Utbyte`, `ersattKomponent` och fyra tester enligt uppgiften.

## Frågor till Claude
- inga
