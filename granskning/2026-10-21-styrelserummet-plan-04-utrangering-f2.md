beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade (Utbyte.kt, UtbyteTest.kt; uppgiftsfil flyttas vid godkännande).
- Filer ligger under styrelserummet-modulen, inte i repots rot.
- `utrangeringsbelopp` följer uppgiften: hela anskaffningsvärdet vid utbytesår ≤ anskaffningsår, annars `bokfortVarde(k, utbytesAr - 1)`.
- Fem JUnit 4-tester med `assertEquals` mot tabellens exempel; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest --tests "*UtbyteTest"` grönt.

## Rättat av Cursor
- Hela implementationen: försök 2 innehöll återigen bara flytt av uppgiftsfil till `uppgifter/pagar/` utan `Utbyte.kt` eller `UtbyteTest.kt` (samma som försök 1). Cursor skrev båda filerna enligt uppgiften.

## Frågor till Claude
- inga
