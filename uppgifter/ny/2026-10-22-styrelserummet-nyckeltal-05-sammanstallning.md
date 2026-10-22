# Uppgift: Alla nyckeltal med etiketter

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: 2026-10-22-styrelserummet-nyckeltal-04-ranta-energi-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRader.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRaderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt

## Uppgift
```kotlin
data class NyckeltalRad(val etikett: String, val varde: String)

fun nyckeltalRader(u: NyckeltalUnderlag): List<NyckeltalRad>
```

Sju rader i denna ordning. `null` visas som `"–"` (tankstreck). Tusental grupperas med hårt mellanslag (` `) som i `kronor`; minus som `−`.

1. "Årsavgift per kvm upplåten med bostadsrätt" → "808 kr/kvm"
2. "Skuldsättning per kvm" → "6 909 kr/kvm"
3. "Skuldsättning per kvm upplåten med bostadsrätt" → "7 308 kr/kvm"
4. "Sparande per kvm" → "309 kr/kvm"
5. "Räntekänslighet" → "9,0" (tiondelar med decimalkomma)
6. "Energikostnad per kvm" → "209 kr/kvm"
7. "Årsavgifternas andel av totala rörelseintäkter" → "91,3 %"

Tips: `kronor(x)` ger "808 kr"; byt slutet " kr" mot " kr/kvm". Tiondelar `t`: heltalsdel `t / 10` och decimal `abs(t % 10)`, med `−` om negativt.

## Exempel
Samma underlag som uppgift 02 → raderna ovan.
Underlag med alla ytor 0 och årsavgifter 0, rörelseintäkter 0 → alla värden "–".

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalRaderTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
