beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: lokal modell ändrade bara flytt av uppgiftsfil till `pagar/`; `GrupperaNamn.kt` och `GrupperaNamnTest.kt` saknades (test enligt PR #329 rött).
- Efter granskning: endast de två tillåtna käll-/testfilerna under `styrelserummet/…/underlag/` plus `granskning/` och flytt av uppgiftsfil; inga filer i repots rot.
- `gissaGrupp` matchar TAK/FASAD/FONSTER_DORRAR enligt nyckelord i gemener, lägst index vid flera träffar; exemplen i uppgiften täcks av testet.
- `GrupperaNamnTest.gissaGruppTakFasadFonster` använder JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar i appen.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hela implementationen i `GrupperaNamn.kt` och `GrupperaNamnTest.kt` (lokal modell levererade ingen produktionskod i försök 2, enligt MALL.md).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
