beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Endast `husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/SmaValideringarTest.kt` (ny) plus uppgiftsflytt `ny/` → `pagar/`; korrekt sökväg, inget i repots rot.
- Tester för `valideraDokument`, `valideraMatare` och `tolkaValfrittOreHeltal` täcker alla exemplen i uppgiften med assertNull/assertEquals mot rätt enum- eller Long-värden.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inte kotlin.test.
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen UI-kod eller hårdkodade strängar.
- `:husloggen:testDebugUnitTest --tests "*SmaValideringarTest"` grönt lokalt; CI Bygg success på grenen.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

(ingen — godkänd)

## Frågor till Claude
- inga
