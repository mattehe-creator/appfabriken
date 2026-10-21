# Uppgift: Årsavgift och skuldsättning per kvm

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: 2026-10-22-styrelserummet-nyckeltal-01-avrunda.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

## Uppgift
Lägg till, alla med `avrundaKvot`:

```kotlin
fun totalYta(u: NyckeltalUnderlag): Long = u.kvmBostadsratt + u.kvmHyresratt
fun arsavgiftPerKvmBostadsratt(u: NyckeltalUnderlag): Long?   // arsavgifter / kvmBostadsratt
fun skuldsattningPerKvm(u: NyckeltalUnderlag): Long?          // rantebarandeSkulder / totalYta
fun skuldsattningPerKvmBostadsratt(u: NyckeltalUnderlag): Long? // rantebarandeSkulder / kvmBostadsratt
```

Är nämnaren 0 eller mindre returneras `null` (inget undantag).

## Exempel
Underlag: årsavgifter 4 200 000, skulder 38 000 000, resultat −350 000, avskrivningar 1 600 000, utrangeringar 0, planerat underhåll 450 000, justeringar 0, energi 1 150 000, rörelseintäkter 4 600 000, kvm bostadsrätt 5 200, kvm hyresrätt 300.
- `totalYta` → 5 500
- `arsavgiftPerKvmBostadsratt` → 808
- `skuldsattningPerKvm` → 6 909
- `skuldsattningPerKvmBostadsratt` → 7 308
- Samma underlag med kvm bostadsrätt 0 och hyresrätt 0 → alla tre `null`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalTest"

## Gör inte
- Ändra andra filer eller `avrundaKvot`.
- Använda flyttal.
