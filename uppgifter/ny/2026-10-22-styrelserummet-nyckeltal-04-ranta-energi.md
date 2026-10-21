# Uppgift: Räntekänslighet, energikostnad och avgiftsandel

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: 2026-10-22-styrelserummet-nyckeltal-03-sparande-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
Lägg till:

```kotlin
fun rantekanslighetTiondelar(u: NyckeltalUnderlag): Long?        // avrundaKvot(skulder * 10, arsavgifter)
fun energikostnadPerKvm(u: NyckeltalUnderlag): Long?             // avrundaKvot(energikostnad, totalYta)
fun arsavgiftsandelTiondelsProcent(u: NyckeltalUnderlag): Long?  // avrundaKvot(arsavgifter * 1000, rorelseintakter)
```

Nämnare 0 eller mindre → `null`.

## Exempel
Samma underlag som uppgift 02:
- `rantekanslighetTiondelar` → 90 (det vill säga 9,0)
- `energikostnadPerKvm` → 209
- `arsavgiftsandelTiondelsProcent` → 913 (det vill säga 91,3 %)
- årsavgifter 0 → `rantekanslighetTiondelar` null

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalTest"

## Gör inte
- Ändra andra filer eller tidigare funktioner.
- Använda flyttal.
