beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Försök 2 från lokal modell: ingen `SakerhetskopiaMatareAvlasningTest.kt` i PR:en, bara flytt av uppgiftsfil till `pagar/` — uppfyllde inte uppgiften (CLAUDE.md punkt 8).
- Efter Cursors övertagande: endast `husloggen/src/test/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaMatareAvlasningTest.kt` tillagd; inga filer i repots rot.
- JUnit 4 (`org.junit`), inget `kotlin.test`; minst en `@Test` per funktion; varje rad under Exempel har motsvarande assertion; `Matare`/`Avlasning` jämförs fält för fält, exportobjekt med `assertEquals` på hela objekt.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:husloggen:testDebugUnitTest --tests "*SakerhetskopiaMatareAvlasningTest"` grönt lokalt.

## Rättat av Cursor
- Hel `SakerhetskopiaMatareAvlasningTest.kt` enligt uppgift (försök 2 utan testkod enligt uppgifter/MALL.md — Cursor gör uppgiften).

## Frågor till Claude
- inga
