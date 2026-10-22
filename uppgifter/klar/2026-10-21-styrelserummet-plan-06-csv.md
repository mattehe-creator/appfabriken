# Uppgift: Avskrivningsplan som CSV

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-05-ersatt-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanCsv.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt

## Uppgift
Skapa `AvskrivningsplanCsv.kt`:

```kotlin
fun avskrivningsplanCsv(plan: List<PlanAr>): String
```

Samma format som `tillCsv` i `CsvExport.kt`: semikolon som avgränsare, `\n` mellan rader, belopp som heltal utan mellanslag, och sist raden `UTKAST_RAD` följd av `\n`.

Rubrikrad: `år;avskrivning;bokfört värde vid årets slut`. Därefter en rad per `PlanAr`.

## Exempel
- `[PlanAr(2026, 60000, 780000), PlanAr(2027, 60000, 720000)]` →
  `"år;avskrivning;bokfört värde vid årets slut\n2026;60000;780000\n2027;60000;720000\n" + UTKAST_RAD + "\n"`
- Tom lista → rubrikraden, `\n`, `UTKAST_RAD`, `\n`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningsplanCsvTest"

## Gör inte
- Ändra andra filer.
- Ändra `CsvExport.kt`.
- Lägga till beroenden.

utfall: underkänd 2026-10-22 av Cursor (PR #479). Ersatt av 2026-10-21-styrelserummet-plan-06-csv-f2.md
