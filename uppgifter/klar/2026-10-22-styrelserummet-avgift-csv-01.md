# Uppgift: CSV med avgift per lägenhet och trappa

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-avgift-csv.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/AvgiftCsv.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvgiftCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsv.kt

## Uppgift
```kotlin
fun avgiftCsv(perLagenhet: List<Long>, trappa: List<Long>): String
```

Belopp i hela kronor. Semikolon som avgränsare, `\n` som radslut, även efter sista raden. Belopp skrivs som heltal utan mellanslag.

- Om `perLagenhet` inte är tom: rubrikraden `Lägenhet;Per år;Per månad`, sedan en rad per lägenhet: radnummer (1, 2, …), årsbeloppet och månadsbeloppet = `(år * 2 + 12) / 24`.
- Om `trappa` inte är tom: (tom rad först om lägenhetsdelen finns) rubrikraden `År;Årsavgifter`, sedan en rad per år: `1`, `2`, … och beloppet.
- Båda tomma → tom sträng.

## Exempel
`avgiftCsv(listOf(2_485_000, 2_485_000), listOf(4_456_667, 4_970_000))` →
```
Lägenhet;Per år;Per månad
1;2485000;207083
2;2485000;207083

År;Årsavgifter
1;4456667
2;4970000
```
`avgiftCsv(emptyList(), listOf(100))` → `"År;Årsavgifter\n1;100\n"`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvgiftCsvTest"

## Gör inte
- Ändra andra filer.

utfall: godkänd 2026-10-22 (JUnit 4 i test; rätt månadsbelopp i extra testfall)
