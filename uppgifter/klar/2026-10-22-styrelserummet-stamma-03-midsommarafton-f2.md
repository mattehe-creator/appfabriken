# Uppgift: midsommarafton som helgdag

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

utfall: godkänd 2026-10-22 (lokal modell levererade tom testfil; Cursor implementerade Helgdagar, HelgdagarMidsommarTest och uppdaterade assert i HelgdagarRodDagTest)
