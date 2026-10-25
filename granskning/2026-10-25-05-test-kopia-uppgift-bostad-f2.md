beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Försök 2 från lokal modell: ingen testfil; PR #684 ändrade bara att uppgiftsfilen låg i `uppgifter/pagar/` (samma som försök 1) — uppfyllde inte uppgiften (CLAUDE.md punkt 8).
- Efter Cursors övertagande enligt uppgifter/MALL.md: endast `husloggen/src/test/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaUppgiftBostadTest.kt` tillagd; rätt sökväg, inga filer i rot eller `uppgifter/` i diffen.
- Tester täcker alla exempel i uppgiften (bostad/uppgift roundtrip, trim, likhet, dubblett) med JUnit 4 (`org.junit`), inte `kotlin.test`; `Uppgift` jämförs fält för fält.
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- `gradle testDebugUnitTest` grönt lokalt (CI-fel på grenen berodde på saknad testfil plus tillfälligt fel i `HusViewModelExportTest` som inte reproducerades lokalt).

## Rättat av Cursor
- Skapade hela `SakerhetskopiaUppgiftBostadTest.kt` (försök 2 underkänt — Cursor gör uppgiften).

## Frågor till Claude
- inga
