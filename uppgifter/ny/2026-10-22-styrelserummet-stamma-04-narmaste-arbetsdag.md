# Uppgift: narmasteArbetsdag

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-03-midsommarafton.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarArbetsdagTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till:

```kotlin
fun narmasteArbetsdag(d: LocalDate, riktning: Int): LocalDate
```

**Arbetsdag** = vardag (måndag–fredag) som inte är `arSvenskHelgdag`. `riktning` ska vara `1` (framåt, första arbetsdag på eller efter `d`) eller `-1` (bakåt). Annat värde → `IllegalArgumentException`. Om `d` redan är arbetsdag, returnera `d`.

## Exempel
- d=2026-04-03 (långfredag), riktning 1 → 2026-04-07 (tisdag)
- d=2026-04-05 (påskdagen, söndag), riktning 1 → 2026-04-07
- d=2026-04-07, riktning -1 → 2026-04-07
- d=2026-06-19 (midsommarafton fredag), riktning 1 → 2026-06-22 (måndag)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HelgdagarArbetsdagTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
