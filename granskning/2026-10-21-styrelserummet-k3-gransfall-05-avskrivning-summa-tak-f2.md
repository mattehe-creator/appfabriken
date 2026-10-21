beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR:en innehöll bara flytt av uppgiftsfil till `pagar/`; ingen testkod från den lokala modellen (försök 2 i uppgiften var därmed olöst).
- Efter Cursors implementation: endast tillåtna sökvägar ändrade (`AvskrivningGransfallSummatakTest.kt` ny; `Avskrivning.kt` oförändrad).
- Testerna summerar `avskrivningFor` över hela nyttjandeperioden och jämför mot exemplen 1_000_000 kr / 30 år och 100_000 kr / 3 år; enskilda årsbelopp kontrolleras som icke-negativa `Long`.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inget `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:test --tests "*AvskrivningGransfallSummatakTest"` och `:styrelserummet:testDebugUnitTest` gröna.

## Rättat av Cursor
- Skapade `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallSummatakTest.kt` enligt uppgiften (ARBETSFLODE: Cursor gör om efter underkänt försök 2).

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
