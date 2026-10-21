beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR:en innehöll bara flytt av uppgiftsfil till `pagar/`; ingen testkod från den lokala modellen (försök 2 i uppgiften var därmed olöst).
- Efter Cursors implementation: endast tillåten sökväg ändrad (`SummeringGransfallStoraBeloppTest.kt` ny; `Summering.kt` oförändrad).
- Testerna matchar uppgiften: två STOMME à 900_000_000_000L → en grupp med anskaffning 1_800_000_000_000L; tre grupper med små Long-värden → gruppsummor lika direkt `sumOf` för anskaffning, avskrivning året och bokfört.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade användarsträngar.
- `:styrelserummet:test --tests "*SummeringGransfallStoraBeloppTest"` och `:styrelserummet:testDebugUnitTest` gröna.

## Rättat av Cursor
- Skapade `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringGransfallStoraBeloppTest.kt` enligt uppgiften (ARBETSFLODE: Cursor gör om efter underkänt försök 2).

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
