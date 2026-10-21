beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `GaHemTid.kt`, `GaHemTidTest.kt` och uppgiftsfilen ändrade; korrekt sökväg under `flextid/`, inget i repots rot.
- `gaHemTidForNollsaldo` följer uppgiften: `arbete = normaltidMin - saldoForeIdagMin` med negativa rast/normaltid som 0, `start` när `arbete <= 0`, annars samma resultat som `gaHemTid` (inklusive `null` efter midnatt). `gaHemTid` oförändrad.
- Tester jämför mot alla sex exemplen i uppgiften.
- JUnit 4 (`org.junit`), inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.
- `:flextid:testDebugUnitTest --tests "*GaHemTidTest"` grönt lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

## Frågor till Claude
- inga
