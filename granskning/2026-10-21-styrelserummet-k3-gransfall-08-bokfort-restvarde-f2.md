beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #370 innehöll bara commit «Uppgift startad» (flytt `ny/` → `pagar/`); ingen `BokfortVardeGransfallRestvardeTest.kt` från den lokala modellen (försök 2 utan leverans, enligt uppgifter/MALL.md).
- Efter granskning: endast tillåten testfil under `styrelserummet/src/test/kotlin/…/regler/` plus uppgiftsflytt till `klar/`; inga filer i repots rot.
- Tester assertar `bokfortVarde` med restvärde > 0: sista avskrivningsår (2019), efter perioden (2025) och mitt i perioden (2014) enligt uppgiftens exempel.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI; `BokfortVarde.kt` oförändrad (befintlig `maxOf(restvarde, …)` räcker).
- `:styrelserummet:test --tests "*BokfortVardeGransfallRestvardeTest"` och `:styrelserummet:testDebugUnitTest` gröna lokalt.

## Rättat av Cursor
- Hela `BokfortVardeGransfallRestvardeTest.kt` (lokal modell levererade inga tester i försök 2).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
