# Uppgift: data class och tidplan()

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-08-revision-arsredovisning.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Stammotidplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammotidplanTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till:

```kotlin
data class Stammotidplan(
    val arsslut: LocalDate,
    val stammodatum: LocalDate,
    val senasteStammodag: LocalDate,
    val tidigasteKallelsedag: LocalDate,
    val senasteKallelsedag: LocalDate,
    val senasteRevisionTillStyrelsen: LocalDate,
    val forstaDagArsredovisningTillganglig: LocalDate,
    val stammaInomLagligFrist: Boolean,
)

fun tidplan(
    arsslut: LocalDate,
    stammodatum: LocalDate,
    kallelsefristVeckor: IntRange,
): Stammotidplan
```

`kallelsefristVeckor` är tillåtna värden för antal veckor före stämman som kallelsen senast får utfärdas (t.ex. `2..4` om stadgar tillåter 2 veckor, `4..4` om bara lagens normalfrist). Använd det **lägsta** talet i intervallet (`kallelsefristVeckor.first`) som `veckorForeStamma` till `senasteKallelsedag`, eftersom färre veckor före stämman ger senaste möjliga utskicksdag. `stammaInomLagligFrist` = `!stammodatum.isAfter(senasteStammodag(arsslut))`.

`kallelsefristVeckor` utanför 2..6 → `IllegalArgumentException`. Tomt intervall → `IllegalArgumentException`.

## Exempel
- arsslut 2025-12-31, stamma 2026-05-15, kallelse 4..6 → lägsta är 4 veckor, senaste kallelse 2026-04-17, stammaInomLagligFrist true
- stamma 2026-05-15, kallelse 2..4 → senaste kallelse 2026-05-01
- stamma 2026-08-01, samma arsslut → stammaInomLagligFrist false

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammotidplanTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
