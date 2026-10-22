# Uppgift: Behövda årsavgifter

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-avgiftskalkyl.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avgiftskalkyl.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvgiftskalkylTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
```kotlin
data class AvgiftUnderlag(
    val driftkostnader: Long,
    val kapitalkostnader: Long,
    val avsattning: Long,
    val ovrigaIntakter: Long,
    val nuvarandeArsavgifter: Long,
    val kvmBostadsratt: Long,
)

fun behovdaArsavgifter(u: AvgiftUnderlag): Long
fun avgiftPerKvmOchManad(arsavgifter: Long, kvmBostadsratt: Long): Long?
fun avgiftsforandringTiondelsProcent(u: AvgiftUnderlag): Long?
```

- `behovdaArsavgifter` = driftkostnader + kapitalkostnader + avsattning − ovrigaIntakter, men aldrig under 0. Negativa kostnader, avsättning, intäkter eller avgifter ger `IllegalArgumentException`.
- `avgiftPerKvmOchManad` = `avrundaKvot(arsavgifter, kvmBostadsratt * 12)`, `null` om `kvmBostadsratt <= 0`.
- `avgiftsforandringTiondelsProcent` = `avrundaKvot((behovdaArsavgifter(u) - u.nuvarandeArsavgifter) * 1000, u.nuvarandeArsavgifter)`, `null` om nuvarande är 0.

## Exempel
Drift 3 100 000, kapital 1 520 000, avsättning 600 000, övriga intäkter 250 000, nuvarande avgifter 4 200 000, 5 200 kvm:
- behov 4 970 000
- per kvm och månad 80
- förändring 183 (18,3 %)
- Med övriga intäkter 10 000 000 → behov 0
- driftkostnader −1 → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvgiftskalkylTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
