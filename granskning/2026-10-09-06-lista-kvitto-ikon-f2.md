beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Lokala modellens commit ändrade bara uppgiftsflytt (`ny/` → `pagar/`); `KopListRad.kt` oförändrad trots att den var enda tillåtna filen.
- Efter Cursors fix: endast `KopListRad.kt` plus denna beslutsfil och uppgiftsflytt till `klar/`; korrekt modulsökväg, inga filer i repots rot.
- När `kop.kvittoFil != null`: titel och `Icons.Filled.Check` i `Row` med `verticalAlignment = CenterVertically`; `contentDescription` från `R.string.kvitto_har_kvitto`; ingen egen `clickable` på ikonen; `Column`-radens `clickable` oförändrad.
- När `kvittoFil == null`: titelrad som tidigare, ingen ikon.
- `:garantivalvet:testDebugUnitTest` och `*KopSorteringTest` gröna lokalt; JUnit 4 i appmodulen.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.

## Rättat av Cursor
- Implementerade kvittoikon i `KopListRad.kt` (försök 2 utan produktionskod enligt MALL.md — Cursor tar över).

## Frågor till Claude
- inga
