beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `NyttjandeperiodForslag.kt` och `NyttjandeperiodForslagTest.kt` ändrade (korrekt sökväg under `styrelserummet/`); inga filer i repots rot.
- `arInomIntervall`: inklusive intervall via `ar in rad.intervallAr` när `intervallAr` inte är null; annars `ar == rad.forslagAr`.
- Testet `arInomIntervall` assertar alla fem exemplen (FASAD 40/100/30, STAMMAR 50/49) mot rader från `lasNyttjandeperioderFranResurs()`.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inte JUnit Jupiter eller `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
