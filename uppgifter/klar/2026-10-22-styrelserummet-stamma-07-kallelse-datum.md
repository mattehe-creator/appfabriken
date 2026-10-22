# Uppgift: tidigaste och senaste kallelsedag

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-06-senaste-stammodag.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Stammotidplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammotidplanKallelseTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till:

```kotlin
fun tidigasteKallelsedag(stammodatum: LocalDate): LocalDate
fun senasteKallelsedag(stammodatum: LocalDate, veckorForeStamma: Int): LocalDate
```

- Tidigaste: stämmodatum minus **6 veckor** (EFL 6 kap. 17 §).
- Senaste: stämmodatum minus `veckorForeStamma` veckor. Parametern är antal veckor före stämman enligt stadgar/lag (**4** normalt, **2** om stadgar tillåter). Värde utanför **2..6** → `IllegalArgumentException`.

## Exempel
- stamma 2026-05-15, veckor 4 → tidigaste 2026-04-03, senaste 2026-04-17
- stamma 2026-05-15, veckor 2 → senaste 2026-05-01
- veckor 1 → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammotidplanKallelseTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (StammotidplanKallelseTest: kotlin.test → JUnit 4)
