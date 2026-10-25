beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Försök 2 från lokal modell: ingen `SakerhetskopiaDokumentPlanTest.kt`, bara flytt av uppgiftsfil till `uppgifter/pagar/` — uppfyller inte uppgiften (CLAUDE.md punkt 8).
- Efter Cursors övertagande: endast ny testfil under `husloggen/src/test/kotlin/se/tmconnect/husloggen/export/`; inga `.kt` i repots rot; inga andra filer ändrade.
- Tester täcker alla exempelrader: export/import för dokument och planerad kostnad, trimning, filnamn från argument, okänd/gemener i kategori, tom anteckning.
- JUnit 4 (`org.junit.Test`, `assertEquals`, `assertNull`); inga nya beroenden, behörigheter eller nätverk; ingen produktionskod eller UI-strängar.
- `:husloggen:testDebugUnitTest --tests "*SakerhetskopiaDokumentPlanTest"` och hela `:husloggen:testDebugUnitTest` grönt lokalt. CI-failure på grenen berodde på saknad testfil respektive befintligt `HusViewModelExportTest` (flaky), inte på denna uppgifts kod.

## Rättat av Cursor
- Hel testfil enligt uppgift (försök 2 underkänt enligt uppgifter/MALL.md — Cursor gör uppgiften).

## Frågor till Claude
- inga
