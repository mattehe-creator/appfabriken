beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #473 innehöll ingen Kotlin-kod (endast flytt av uppgiftsfil till pagar/); implementation saknades helt vid granskning.
- Efter andra misslyckade försöket implementerade Cursor uppgiften enligt CLAUDE.md punkt 3 och uppgifter/MALL.md.
- Endast `Fondplan.kt` och `FondplanTest.kt` ändrade; korrekta sökvägar under styrelserummet/.
- `forstaUnderskottsar` returnerar första året med negativt `utgaende`, annars `null`.
- `minstaAvsattningUtanUnderskott` bygger kumulativ uttagssumma och max av heltalskrav enligt uppgiften; anropar inte `fondUtveckling`.
- Befintliga funktioner (`summaPerAr`, `jamnAvsattning`, `fondUtveckling`) oförändrade.
- Tester jämför alla exempel i uppgiften inklusive 233_334/233_333-kontroll; JUnit 4 (`org.junit`).
- Inga nya beroenden, behörigheter eller nätverksanrop; ingen UI-kod.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Hela uppgiftslösningen: `forstaUnderskottsar`, `minstaAvsattningUtanUnderskott` och testerna `test forstaUnderskottsar` / `test minstaAvsattningUtanUnderskott` (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
(ingen — lokal modell levererade ingen kod; Cursor tog över efter försök 2 enligt direktiv.)

## Frågor till Claude
- inga
