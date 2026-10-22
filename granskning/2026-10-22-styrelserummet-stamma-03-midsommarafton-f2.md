beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Försök 2 från lokal modell: `Helgdagar.kt` oförändrad, `HelgdagarMidsommarTest.kt` tom (0 rader) — uppgiften ej utförd.
- Efter MALL.md (försök 2 underkänd → Cursor gör uppgiften): midsommarafton tillagd i `arSvenskHelgdag` via `forstaLordagenFran(...).minusDays(1)`; JUnit 4-test med alla fyra datumexempel.
- Inga filer i repots rot; kod på rätt sökvägar under `styrelserummet/`.
- Inga nya beroenden, behörigheter, nätverksanrop, databasschema eller hårdkodade UI-strängar.
- Tester använder `org.junit`, inte `kotlin.test`.

## Rättat av Cursor
- `Helgdagar.kt`: midsommarafton i listan `rorliga`.
- `HelgdagarMidsommarTest.kt`: JUnit 4-test enligt uppgiftens exempel.
- `HelgdagarRodDagTest.kt`: `assertFalse` → `assertTrue` för 2026-06-19 (motsägelse mot ny regel; krävs för grönt `:styrelserummet:testDebugUnitTest`).
- Uppgiftsfil flyttad `uppgifter/pagar/` → `uppgifter/klar/` med utfall-rad.

## Frågor till Claude
- inga
