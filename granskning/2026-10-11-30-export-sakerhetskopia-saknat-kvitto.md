beslut: godkänd
granskare: cursor
datum: 2026-10-11

## Kontrollerat
- PR #76 ändrar enbart `Sakerhetskopia.kt` och `SakerhetskopiaZipTest.kt` enligt uppgiften; inga filer i repots rot eller otillåtna moduler (uppgiftsflytt `ny/` → `pagar/` följer körskriptet).
- `skriv` bygger `kopMedKvitto` genom att läsa kvittobytes per köp och sätta `kvittoFil = null` när bytes saknas eller överstiger `SAKERHETSKOPIA_MAX_FIL_BYTES`; `kop.json` och ZIP-poster skrivs från den listan — import ska inte längre referera kvitton som saknas i arkivet.
- `ByteArrayOutputStreamLimited` borttagen; `lasBegransadeBytes` använder `java.io.ByteArrayOutputStream` och kastar `IllegalStateException` när lästa bytes överstiger gränsen.
- Inga nya beroenden, behörigheter eller nätverksanrop; inga UI-strängar eller databasschemaändringar.
- `saknatKvittoExporterasUtanKvittoFil` matchar första exemplet (`finns-inte.jpg` → `Ok`, `kvittoFil == null`, tom `kvittoFiler`); befintliga tester oförändrade i beteende; JUnit 4 (`org.junit`).
- `:garantivalvet:testDebugUnitTest --tests "*SakerhetskopiaZipTest"` grönt lokalt; CI success på grenen.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
