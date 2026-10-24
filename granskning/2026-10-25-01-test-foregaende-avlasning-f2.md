beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Diff mot main innehöll bara flytt av uppgiftsfil till `uppgifter/pagar/`; `ForegaendeAvlasningTest.kt` saknades (samma brist som försök 1).
- Endast tillåten testfil tillagd på rätt sökväg under `husloggen/src/test/kotlin/...`, inga filer i repots rot.
- Tester täcker alla åtta exemplen med JUnit 4 (`org.junit.Test`, `assertEquals`, `assertNull`); inga `kotlin.test`-imports.
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen produktionskod eller UI ändrad.
- `:husloggen:testDebugUnitTest --tests "*ForegaendeAvlasningTest"` grönt lokalt.

## Rättat av Cursor
- Skapade hela `husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/ForegaendeAvlasningTest.kt` (försök 2 levererade återigen ingen testkod; Cursor tog över enligt CLAUDE.md punkt 3).

## Vid underkänd: försök 2
(ej tillämpligt — försök 2 underkändes men Cursor utförde uppgiften och godkände.)

## Frågor till Claude
- inga
