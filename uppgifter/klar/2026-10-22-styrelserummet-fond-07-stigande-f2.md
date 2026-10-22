# Uppgift: Avsättning som ökar varje år

## Vad som saknades (försök 1)
- Ingen Kotlin-kod committades. Bara uppgiftsfilen flyttades från `uppgifter/ny/` till `uppgifter/pagar/`.
- Skapa `FondStigande.kt` med `fondUtvecklingStigande` och `FondStigandeTest.kt` med JUnit 4-tester som jämför mot exemplen nedan.

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondStigande.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondStigandeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondIndexering.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanTest.kt (mönster för `fondUtveckling` och `FondAr`)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondIndexeringTest.kt (JUnit 4)

## Uppgift
Många föreningar höjer avsättningen i takt med avgiften.

fun fondUtvecklingStigande(
    atgarder: List<PlaneradAtgard>,
    franAr: Int,
    antalAr: Int,
    forstaAvsattning: Long,
    okningTiondelsProcent: Int,
    ingaendeFond: Long,
): List<FondAr>

Som `fondUtveckling`, men avsättningen år `franAr + i` är `indexeratBelopp(forstaAvsattning, franAr + i, franAr, okningTiondelsProcent)` (samma avrundning som i FondIndexering.kt). Samma kontroller som `fondUtveckling` och `indexeratBelopp`.

## Exempel
Åtgärder: (2028, 900 000). franAr 2026, 3 år, första avsättning 300 000, ökning 20 (2 %), fond 0:
- (2026, 300 000, 0, 300 000)
- (2027, 306 000, 0, 606 000)
- (2028, 312 120, 900 000, 18 120)

Ökning 0 ger samma resultat som `fondUtveckling(..., 300 000, 0)`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondStigandeTest"

Använd `org.junit.Test` och `org.junit.Assert.assertEquals` (inte `kotlin.test`). Minst ett test med uppgiftens tre `FondAr`-rader; ett test som jämför ökning 0 med `fondUtveckling` för samma indata.

## Gör inte
- Ändra andra filer.
- Använda flyttal.

utfall: godkänd 2026-10-22 (Cursor implementerade FondStigande.kt och FondStigandeTest.kt efter underkänt försök 2 utan kod)
