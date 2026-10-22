beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast tillåtna filer i diff mot main: tom `HelgdagarMidsommarTest.kt` och uppgiftsflytt `ny/` → `pagar/`; `Helgdagar.kt` saknas i diff — inte ändrad.
- Uppgiften (midsommarafton ska ge `true` i `arSvenskHelgdag`) är inte implementerad; befintlig kod listar bara midsommardagen via `forstaLordagenFran`.
- `HelgdagarMidsommarTest.kt` är en tom fil utan klass, tester eller JUnit-imports; `:styrelserummet:test --tests "*HelgdagarMidsommarTest"` ger "No tests found".
- Inga nya beroenden, behörigheter, nätverk eller UI-strängar (ingen produktkod ändrad).
- JUnit 4: ej tillämpligt — inga tester tillagda. `:styrelserummet:testDebugUnitTest` grönt lokalt men testar inte uppgiftens beteende.

## Rättat av Cursor
- inget (saknad implementation — försök 2)

## Vid underkänd: försök 2

```
# Uppgift: midsommarafton som helgdag

## Vad som saknades
- `Helgdagar.kt` ändrades inte: midsommarafton ingår inte i `arSvenskHelgdag`.
- `HelgdagarMidsommarTest.kt` committades tom (0 rader kod) — inga JUnit-tester mot exemplen i uppgiften.
- Bygget kan bli grönt utan att uppgiften är utförd.

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-02-helgdag-roda-dagar.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarMidsommarTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md (avsnitt Osäkert om midsommarafton)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarRodDagTest.kt (JUnit 4-stil)

## Uppgift
Utöka `arSvenskHelgdag` så att **midsommarafton** (dagen före midsommardagen) också ger `true`.

Tips: midsommardagen finns redan som `forstaLordagenFran(LocalDate.of(ar, 6, 20))`. Midsommarafton är **en kalenderdag före** den dagen (`minusDays(1)`).

## Exempel
- 2026-06-19 → true (midsommarafton)
- 2026-06-20 → true (midsommardagen)
- 2025-06-20 → true (midsommarafton 2025)
- 2025-06-21 → true (midsommardagen 2025)

## Test som ska gå igenom
Skriv `HelgdagarMidsommarTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertTrue`) — **inte** `kotlin.test`. Minst ett test per datum ovan (eller ett test med alla fyra).

Kör:
`gradle :styrelserummet:test --tests "*HelgdagarMidsommarTest"`

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
```

## Frågor till Claude
- När midsommarafton blir helgdag faller `HelgdagarRodDagTest` (rad med `assertFalse` för 2026-06-19) om den filen inte uppdateras, men uppgiften tillåter bara två filer. Ska försök 2 utökas med `HelgdagarRodDagTest.kt`, eller ska Cursor rätta den raden vid godkännande av försök 2?
