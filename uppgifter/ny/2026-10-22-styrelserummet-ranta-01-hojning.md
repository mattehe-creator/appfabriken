# Uppgift: Kostnad och avgiftshöjning vid högre ränta

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-rantescenario.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Rantescenario.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/RantescenarioTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
```kotlin
fun extraRantekostnadPerAr(skulder: Long, hojningTiondelar: Int): Long
fun extraPerKvmOchManad(extraPerAr: Long, kvmBostadsratt: Long): Long?
fun avgiftshojningTiondelsProcent(skulder: Long, arsavgifter: Long, hojningTiondelar: Int): Long?
```

- `extraRantekostnadPerAr` = `avrundaKvot(skulder * hojningTiondelar, 1000)`. Negativa `skulder` eller `hojningTiondelar` utanför 0..200 → `IllegalArgumentException`.
- `extraPerKvmOchManad` = `avrundaKvot(extraPerAr, kvmBostadsratt * 12)`, `null` om `kvmBostadsratt <= 0`.
- `avgiftshojningTiondelsProcent` = `avrundaKvot(skulder * hojningTiondelar, arsavgifter)`, `null` om `arsavgifter <= 0`.

## Exempel
Skulder 38 000 000, årsavgifter 4 200 000, 5 200 kvm bostadsrätt:
- höjning 5 → 190 000 kr/år, 3 kr/kvm/mån, 45 (4,5 %)
- höjning 10 → 380 000, 6, 90
- höjning 20 → 760 000, 12, 181
- höjning 30 → 1 140 000, 18, 271
- höjning 201 → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*RantescenarioTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
