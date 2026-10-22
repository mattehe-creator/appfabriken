# Uppgift: Behövda årsavgifter

## Vad som saknades (försök 1)
- Båda Kotlin-filerna skapades inte alls — grenen innehåller bara flytt av uppgiftsfilen till `uppgifter/pagar/`.
- Skapa `Avgiftskalkyl.kt` med dataclass och funktioner enligt nedan, och `AvgiftskalkylTest.kt` med JUnit 4-tester som assertar alla exempel (värden och `IllegalArgumentException`).

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-avgiftskalkyl.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avgiftskalkyl.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvgiftskalkylTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
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

- `behovdaArsavgifter` = driftkostnader + kapitalkostnader + avsattning − ovrigaIntakter, men aldrig under 0. Negativa driftkostnader, kapitalkostnader, avsattning, ovrigaIntakter eller nuvarandeArsavgifter ger `IllegalArgumentException`.
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
gradle :styrelserummet:testDebugUnitTest --tests "*AvgiftskalkylTest"

Använd `org.junit.Test` och `org.junit.Assert` (inte `kotlin.test`). Minst ett test som kör huvudexemplet (behov, per kvm/månad, förändring), ett med höga övriga intäkter (behov 0) och `@Test(expected = IllegalArgumentException::class)` för negativ driftkostnad.

## Gör inte
- Ändra andra filer.
- Använda flyttal.
- Lämna filerna tomma eller uteblivna.

utfall: godkänd 2026-10-22 (Cursor skapade Avgiftskalkyl.kt och AvgiftskalkylTest.kt; lokala modellen levererade ingen kod)
