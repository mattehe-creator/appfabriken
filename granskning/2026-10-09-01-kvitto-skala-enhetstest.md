beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast tillåten testfil plus felplacerad dubblett i rot (borttagen); uppgiftsflytt till `pagar/` följer lokalt workflow.
- Ingen ändring utanför uppgiftens avsikt (enbart enhetstest för `beraknaNedskaladStorlek`).
- Testerna täcker alla fyra exemplen i uppgiften och jämför förväntade mått mot resultat.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod eller hårdkodade användarsträngar.

## Rättat av Cursor
- Tog bort `KvittoSkalningTest.kt` i repots rot (fel sökväg; korrekt fil ligger under `garantivalvet/src/test/...`).

## Frågor till Claude
- inga
