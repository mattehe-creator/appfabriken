# Uppgift: revisionsberättelse och årsredovisning

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-07-kallelse-datum.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Stammotidplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammotidplanHandlingarTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till:

```kotlin
fun senasteRevisionTillStyrelsen(stammodatum: LocalDate): LocalDate
fun forstaDagArsredovisningTillganglig(stammodatum: LocalDate): LocalDate
```

- Revisionsberättelse till styrelsen: senast **3 veckor** före stämman (EFL 8 kap. 32 §).
- Årsredovisning tillgänglig: **första dagen** i perioden **2 veckor** närmast före stämman (EFL 6 kap. 23 §) = stämmodatum minus 2 veckor.

## Exempel
- stamma 2026-05-15 → revision senast 2026-04-24, årsredovisning från 2026-05-01

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammotidplanHandlingarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
