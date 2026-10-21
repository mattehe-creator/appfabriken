beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #369 ändrade bara flytt `ny/` → `pagar/` och en tom `BokfortVardeGransfallArsskifteTest.kt` (0 rader); ingen testlogik från den lokala modellen i försök 2 (enligt uppgifter/MALL.md tar Cursor över).
- Efter granskning: endast tillåten ny testfil under `styrelserummet/…/regler/` plus uppgiftsflytt; inga filer i repots rot; inga ändringar i `BokfortVarde.kt` eller `Avskrivning.kt`.
- Tester använder JUnit 4 (`org.junit.Test`, `assertEquals` / `assertTrue`), inte `kotlin.test`.
- Assert jämför mot uppgiftens exempel (90_000 / 60_000 / 30_000 vid årsskifte 2024→2025), årsskifte vid anskaffningsår och loop över på varandra följande år med avskrivningsskillnad.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI; inget databasschema.
- `:styrelserummet:testDebugUnitTest` och `:styrelserummet:test --tests "*BokfortVardeGransfallArsskifteTest"` grönt lokalt.

## Rättat av Cursor
- Hela `BokfortVardeGransfallArsskifteTest.kt` (lokal modell lämnade tom fil).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
