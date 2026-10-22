# Uppgift: Fondens utveckling år för år

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-fond-02-jamn.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt

## Uppgift
Lägg till:

```kotlin
data class FondAr(val ar: Int, val avsattning: Long, val uttag: Long, val utgaende: Long)

fun fondUtveckling(
    atgarder: List<PlaneradAtgard>,
    franAr: Int,
    antalAr: Int,
    arligAvsattning: Long,
    ingaendeFond: Long,
): List<FondAr>
```

En rad per år. `uttag` = årets summa från `summaPerAr`. `utgaende` = föregående års utgående (första året `ingaendeFond`) plus `arligAvsattning` minus `uttag`. Utgående får bli negativt. Negativ `arligAvsattning` ger `IllegalArgumentException`.

## Exempel
Samma åtgärder, franAr 2026, antalAr 5, avsättning 180 000, fond 300 000:
- (2026, 180 000, 100 000, 380 000)
- (2027, 180 000, 0, 560 000)
- (2028, 180 000, 900 000, −160 000)
- (2029, 180 000, 0, 20 000)
- (2030, 180 000, 200 000, 0)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondplanTest"

## Gör inte
- Ändra andra filer eller tidigare funktioner.
- Använda flyttal.

utfall: underkänd 2026-10-21 av Cursor (PR #464). Ersatt av 2026-10-21-styrelserummet-fond-03-utveckling-f2.md
