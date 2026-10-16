beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast `husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/BildTolkningTest.kt` (ny) plus uppgiftsflytt `ny/` → `pagar/`; korrekt sökväg, inget i repots rot.
- `tolkaBilder` och `formateraBilder` i produktionskod oförändrade enligt uppgiften.
- Alla sex exemplen i uppgiften täcks med `assertEquals` / `assertNull` mot förväntade värden.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod eller hårdkodade användarsträngar.
- `:husloggen:testDebugUnitTest --tests "*BildTolkningTest"` grönt lokalt.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
