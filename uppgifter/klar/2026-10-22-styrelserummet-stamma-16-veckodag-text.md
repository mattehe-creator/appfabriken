# Uppgift: svensk veckodag i datumtext

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-09-tidplan-samlad.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaUtskrift.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaVeckodagTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Skapa `StammaUtskrift.kt` med:

```kotlin
fun datumMedVeckodag(d: LocalDate): String
```

Format: `2026-05-31 (söndag)` — ISO-datum, svensk veckodag i gemener med åäö (måndag … söndag). Använd `DayOfWeek` utan locale-beroende externa bibliotek.

## Exempel
- 2026-05-31 → `2026-05-31 (söndag)`
- 2026-05-15 → `2026-05-15 (fredag)`
- 2026-06-06 → `2026-06-06 (lördag)`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammaVeckodagTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-22 av Cursor (PR #580). Ersatt av 2026-10-22-styrelserummet-stamma-16-veckodag-text-f2.md
