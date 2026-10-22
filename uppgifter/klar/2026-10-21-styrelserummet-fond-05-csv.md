# Uppgift: Fondplanen som CSV

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-fond-04-underskott-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondplanCsv.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt

## Uppgift
```kotlin
fun fondplanCsv(utveckling: List<FondAr>): String
```

Samma format som `tillCsv` i `CsvExport.kt`: semikolon, `\n` mellan rader, heltal utan mellanslag, sist `UTKAST_RAD` och `\n`. Rubrikrad: `år;avsättning;uttag;fond vid årets slut`.

## Exempel
- `[FondAr(2026, 180000, 100000, 380000)]` → `"år;avsättning;uttag;fond vid årets slut\n2026;180000;100000;380000\n" + UTKAST_RAD + "\n"`
- Negativt utgående skrivs med minustecken: `2028;180000;900000;-160000`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondplanCsvTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-22 av Cursor (PR #482). Ersatt av 2026-10-21-styrelserummet-fond-05-csv-f2.md
