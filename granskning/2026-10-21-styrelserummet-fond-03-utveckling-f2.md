beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #467 innehöll ingen kodändring (endast flytt av uppgiftsfil till pagar/); implementation saknades helt vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3.
- Endast `Fondplan.kt` och `FondplanTest.kt` ändrade för uppgiftslösningen; korrekta sökvägar under styrelserummet/.
- `fondUtveckling` anropar `summaPerAr`, sätter `avsattning` till `arligAvsattning`, räknar `utgaende` enligt uppgiften; negativ avsättning ger `IllegalArgumentException`.
- `summaPerAr` och `jamnAvsattning` oförändrade.
- Tester jämför alla fem exempelrader och negativ avsättning; JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hela uppgiftslösningen: `FondAr`, `fondUtveckling` och testerna `test fondUtveckling` / `test fondUtveckling negativ avsattning` (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
