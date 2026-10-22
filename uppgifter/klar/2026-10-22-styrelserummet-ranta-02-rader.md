# Uppgift: Räntescenario som rader

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-rantescenario.md
försök: 1
beror-på: 2026-10-22-styrelserummet-ranta-01-hojning.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/RantescenarioRader.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/RantescenarioRaderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Rantescenario.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRader.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt

## Uppgift
```kotlin
data class RantescenarioRad(val hojning: String, val perAr: String, val perKvmManad: String, val avgiftshojning: String)

val STANDARDSTEG = listOf(5, 10, 20, 30)

fun rantescenarioRader(skulder: Long, arsavgifter: Long, kvmBostadsratt: Long, steg: List<Int> = STANDARDSTEG): List<RantescenarioRad>
```

En rad per steg, i stegens ordning:
- `hojning`: "+0,5 procentenhet", "+1,0 procentenhet", "+2,0 procentenheter", "+3,0 procentenheter" (tiondelar med decimalkomma; "procentenhet" när värdet är högst 10, annars "procentenheter").
- `perAr`: `kronor(extra)` (till exempel "380 000 kr").
- `perKvmManad`: "6 kr/kvm/mån", eller "–" vid null.
- `avgiftshojning`: "+9,0 %", eller "–" vid null.

## Exempel
Skulder 38 000 000, årsavgifter 4 200 000, 5 200 kvm:
- rad 1: ("+0,5 procentenhet", "190 000 kr", "3 kr/kvm/mån", "+4,5 %")
- rad 2: ("+1,0 procentenhet", "380 000 kr", "6 kr/kvm/mån", "+9,0 %")
- rad 4: ("+3,0 procentenheter", "1 140 000 kr", "18 kr/kvm/mån", "+27,1 %")
- årsavgifter 0 → avgiftshojning "–"

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*RantescenarioRaderTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.

utfall: utförd av Claude 2026-10-22 (datorn användes)
