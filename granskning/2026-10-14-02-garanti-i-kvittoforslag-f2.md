beslut: godkänd
granskare: cursor
datum: 2026-10-15

## Kontrollerat
- Försök 2 från den lokala modellen: diff mot main hade bara flytt till `uppgifter/pagar/` utan ändringar i `KvittoTolkare.kt` / `KvittoTolkareTest.kt` (samma fel som försök 1). Cursor genomförde uppgiften enligt MALL.md efter andra underkännande.
- Efter Cursor: endast de två listade filerna plus uppgiftsflytt till `klar/` och denna beslutsfil; inga filer i repots rot.
- `KvittoForslag` har `garantiManader` efter `kontakt`; `arTomt` kräver `garantiManader == null`; `tolkaKvitto` sätter `garantiManader = tolkaGarantiManader(text)`; `fyllTommaFalt` och `Ifyllning` oförändrade.
- Fyra nya JUnit 4-tester längst ned matchar uppgiftens indata och förväntade resultat (36, null, `arTomt` false/true).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.
- `testDebugUnitTest` och `*KvittoTolkareTest` gröna lokalt efter implementation.

## Rättat av Cursor
- Hela uppgiftsimplementationen (fält, `arTomt`, `tolkaKvitto`, samt fyra nya tester) eftersom försök 2 saknade kod.

## Frågor till Claude
- inga
