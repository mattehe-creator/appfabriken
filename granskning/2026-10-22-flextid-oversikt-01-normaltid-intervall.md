beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `ManadsNormaltid.kt`, `NormaltidIntervallTest.kt` och uppgiftsflytt till `pagar/`; korrekt sökväg under `flextid/`, inget i repots rot.
- `arbetsdagarIIntervall` och `normaltidForIntervallMin` följer uppgiften: räknar arbetsdagar i intervallet som inte är lediga enligt `arLedigDag` (röda dagar alltid borta); `fran` efter `till` → 0; normaltid = antal × minuter per dag.
- Tester jämför mot exemplen i uppgiften (okt-vecka, påskvecka, julvecka med/utan lediga aftnar, omvänt intervall, normaltid 2400).
- JUnit 4 (`org.junit`), inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.
- `:flextid:testDebugUnitTest --tests "*NormaltidIntervallTest"` grönt.

## Rättat av Cursor
- Testimport: `org.junit.Assert.assertEquals` i stället för `kotlin.test.assertEquals` (CI kompileringsfel).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
