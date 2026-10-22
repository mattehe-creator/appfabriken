# Uppgift: arSvenskHelgdag utan aftnar

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-01-paskdagen.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarRodDagTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till i `Helgdagar.kt`:

```kotlin
fun arSvenskHelgdag(d: LocalDate): Boolean
```

Ska vara sant för lag (1989:253) **1–2 §§**: nyårsdagen, trettondedag jul, långfredag, påskdagen, annandag påsk, första maj, Kristi himmelsfärdsdag, nationaldagen, midsommardagen, alla helgons dag, juldagen, annandag jul. Rörliga dagar från `paskdagen(d.year)`; midsommardagen = lördag 20–26 juni; alla helgons dag = lördag 31 okt–6 nov (samma logik som flextid `forstaLordagenFran`).

**Inte** midsommarafton i detta steg.

## Exempel
- 2026-04-03 → true (långfredag)
- 2026-04-07 → false
- 2026-06-06 → true (nationaldag)
- 2026-06-19 → false (midsommarafton)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HelgdagarRodDagTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (JUnit-import i HelgdagarRodDagTest)
