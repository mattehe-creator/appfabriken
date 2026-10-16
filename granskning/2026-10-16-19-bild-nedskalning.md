beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast tillåten testfil plus flytt av uppgiftsfilen `ny/` → `pagar/` (lokalt workflow); korrekt sökväg under `karna/src/test/...`.
- Ingen ändring utanför uppgiftens avsikt: enhetstester för `beraknaNedskaladStorlek` och `beraknaInSampleSize` mot exemplen i uppgiften.
- Testerna jämför förväntade mått och inSampleSize mot resultat; extra fall `500×0` är rimligt komplement till ogiltiga mått.
- JUnit 4 (`org.junit.Test`); inget kotlin.test efter Cursor-rättning.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade användarsträngar.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- Bytte `kotlin.test.assertEquals` till `org.junit.Assert.assertEquals` i linje med övriga `:karna`-tester.

## Frågor till Claude
- inga
