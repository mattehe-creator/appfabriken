# Uppgift: Avsättning som ökar varje år

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondStigande.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondStigandeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondIndexering.kt

## Uppgift
Många föreningar höjer avsättningen i takt med avgiften. 

```kotlin
fun fondUtvecklingStigande(
    atgarder: List<PlaneradAtgard>,
    franAr: Int,
    antalAr: Int,
    forstaAvsattning: Long,
    okningTiondelsProcent: Int,
    ingaendeFond: Long,
): List<FondAr>
```

Som `fondUtveckling`, men avsättningen år `franAr + i` är `indexeratBelopp(forstaAvsattning, franAr + i, franAr, okningTiondelsProcent)` (samma avrundning som i FondIndexering.kt). Samma kontroller som `fondUtveckling` och `indexeratBelopp`.

## Exempel
Åtgärder: (2028, 900 000). franAr 2026, 3 år, första avsättning 300 000, ökning 20 (2 %), fond 0:
- (2026, 300 000, 0, 300 000)
- (2027, 306 000, 0, 606 000)
- (2028, 312 120, 900 000, 18 120)
Ökning 0 ger samma resultat som `fondUtveckling(..., 300 000, 0)`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondStigandeTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.

utfall: utförd av Claude 2026-10-22 (på grenen som kopplar in den)
