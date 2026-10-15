beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast `UppgiftSortering.kt`, `UppgiftSorteringTest.kt` plus uppgiftsflytt `ny/` → `pagar/` (lokalt workflow); rätt paket och sökvägar under `husloggen/`.
- `sorteraUppgifter` sorterar stigande på `nastaDatum(senastGjord, startDatum, intervallManader)` och därefter `namn` med `String.CASE_INSENSITIVE_ORDER`; signaturen behåller `idag` enligt uppgiften.
- Tester jämför hela listordningen: huvuexemplet A → C → B (`idag = 2026-10-16`) och tie-break `"a"` före `"B"` vid samma nästa datum.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade användarsträngar.
- `:husloggen:testDebugUnitTest --tests "*UppgiftSorteringTest"` grönt lokalt.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
