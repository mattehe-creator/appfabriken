beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Lokala modellens diff ändrar bara `MatareTolkningTest.kt` (ny) plus flytt av uppgiftsfilen till `uppgifter/pagar/`; korrekt sökväg under `husloggen/`, inget i repots rot.
- Testerna täcker alla exempel i uppgiften med `assertEquals`/`assertNull` mot angivna indata och förväntade värden.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverksanrop; `MatareTolkning.kt` oförändrad.
- Ingen UI-kod eller hårdkodade användarsträngar utanför testdata.
- `:husloggen:testDebugUnitTest --tests "*MatareTolkningTest"` grönt lokalt.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

(ingen — godkänd)

## Frågor till Claude
- inga
