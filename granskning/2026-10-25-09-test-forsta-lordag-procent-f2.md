beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- PR #689 ändrade bara flytt av uppgiftsfil till `pagar/`; ingen testkod från den lokala modellen (samma brist som försök 1).
- Efter Cursors komplettering: endast tillåten testfil ändrad, korrekt sökväg under `styrelserummet/src/test/`.
- Testerna anropar `forstaLordagenFran` och `procent` och jämför mot alla exempel i uppgiften.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen produktionskod eller hårdkodad UI-text.

## Rättat av Cursor
- Skapade `ForstaLordagenProcentTest.kt` med två `@Test`-metoder enligt uppgiften och `FormateringTest.kt` som förebild (försök 2 utan implementation — Cursor gjorde uppgiften enligt uppgifter/MALL.md och CLAUDE.md punkt 3).

## Vid underkänd: försök 2
(ej tillämpligt — beslutet är godkänd efter Cursors komplettering)

## Frågor till Claude
- inga
