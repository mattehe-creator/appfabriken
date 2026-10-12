beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast angivna filer plus uppgiftsflytt och denna beslutsfil ändras; korrekt sökväg under `garantivalvet/`; inga filer i repots rot.
- `GarantitidVal` har `FORVALD_MANADER`, `CHIP_MANADER` och `manaderFranVal` enligt uppgiften och exemplen (index 1 → 24, index 3 med «0» → null).
- `KopFormularSkarm` använder M3 `FilterChip` för 12/24/36 och «Annat», standard 24 vid nytt köp, «Annat»-fält med `falt_garanti_manader`, sparlogik via `manaderFranVal`; `KvittoKort` oförändrad mot grenens bas (sampling, `AppfabrikKortForm`, knappar).
- Chip-etiketter via `strings.xml` / `values-sv`; inga hårdkodade UI-strängar för chips.
- `GarantitidValTest` (JUnit 4, `org.junit`) täcker uppgiftens exempel och fasta värden.
- Inga nya beroenden, behörigheter eller nätverk.
- `:garantivalvet:testDebugUnitTest` grönt lokalt; CI på grenen success.

## Rättat av Cursor
- Återställde `KvittoKort` (försök 2 hade skrivit om kvittokortet: fel sampling, knappar och synlighet).
- Rättade chip-val vid redigering (ogiltigt/icke-standard antal månader → index 3 «Annat» med text, inte felaktigt 24).
- Bytte chip-etiketter från `R.string.garanti_chip_12 + index` (ogiltig resurs-aritmetik) till explicit `when` per månad.
- Behöll datum/pris-fält som på grenens bas (inte main efter locale-uppgift).

## Vid underkänd: försök 2

(ingen — godkänd efter Cursors rättningar enligt MALL.md för underkänt försök 2)

## Frågor till Claude
- inga
