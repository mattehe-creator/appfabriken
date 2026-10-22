# Uppgift: prisbasbeloppFor

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-12-stamma-data-las-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaDataPrisbasbeloppTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/resources/stamma.csv

## Uppgift
Lägg till:

```kotlin
fun prisbasbeloppFor(ar: Int, data: Map<String, StammaRad>): Long?
fun lasStammaDataFranResurs(): Map<String, StammaRad>
```

`prisbasbeloppFor` returnerar `varde` för nyckeln `prisbasbelopp_$ar` om typ och enhet `kr`, annars null.

`lasStammaDataFranResurs` läser `/stamma.csv` från classpath och anropar `lasStammaData`.

## Exempel
- data med 2026 → 59200
- data utan 2019 → null
- data med fel enhet → null

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammaDataPrisbasbeloppTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
