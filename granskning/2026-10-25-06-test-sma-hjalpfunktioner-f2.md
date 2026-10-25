beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- PR #686 (försök 2) ändrade bara uppgiftsfilen (flytt till `pagar/`); ingen kod i tillåten testfil — samma brist som försök 1 (tom fil på annan gren).
- Ingen ändring utanför listan i uppgiften efter Cursors komplettering; inga filer i repots rot.
- Testerna anropar alla fyra funktioner med `LocalDate` där det krävs och jämför mot samtliga exempel i uppgiften.
- JUnit 4 (`org.junit.Test`, `assertEquals`, `assertTrue`, `assertFalse`); inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverksanrop i PR:en.
- Ingen produktionskod eller UI ändrad.

## Rättat av Cursor
- Skapade `husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/HusSmaHjalpfunktionerTest.kt` med fyra `@Test`-metoder enligt uppgiften och `AvlasningUrvalTest.kt` som förebild (försök 2 utan implementation — Cursor gjorde uppgiften enligt CLAUDE.md punkt 3).

## Vid underkänd: försök 2
(ej tillämpligt — beslutet är godkänd efter Cursors komplettering)

## Frågor till Claude
- inga
