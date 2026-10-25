beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Försök 2 från lokal modell: ingen `NormaliseringTest.kt` i PR:en, bara flytt av uppgiftsfil till `pagar/` — uppfyllde inte uppgiften (CLAUDE.md punkt 8).
- Efter Cursors övertagande: endast `flextid/src/test/kotlin/se/tmconnect/flextid/regler/NormaliseringTest.kt` tillagd; inga filer i repots rot.
- JUnit 4 (`org.junit`), inget `kotlin.test`; minst en `@Test` per funktion; varje rad under Exempel har motsvarande assertion.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:flextid:testDebugUnitTest --tests "*NormaliseringTest"` grönt lokalt.

## Rättat av Cursor
- Hel `NormaliseringTest.kt` enligt uppgift (försök 2 utan testkod enligt uppgifter/MALL.md — Cursor gör uppgiften).

## Frågor till Claude
- inga
