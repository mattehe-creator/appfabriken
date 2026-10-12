# Uppgift: tidslinje — beräkning

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Tidslinje.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/TidslinjeTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Konsumentkoplagen.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt

## Uppgift
Implementera `data class TidslinjeMarkorer(
    val garantiSlutAndel: Float,
    val presumtionSlutAndel: Float,
    val idagAndel: Float,
)` och  

`fun beraknaTidslinje(kopdatum: LocalDate, garantiManader: Int, idag: LocalDate): TidslinjeMarkorer?`

- Tidslinjens start = köpdatum, slut = `reklamationSlut(kopdatum)`; returnera `null` om reklamation inte tillämpas.
- Andelar är `0f..1f` längs intervallet köpdatum → reklamationsslut för: garantins slut, `presumtionSlut`, och `idag` (klampat till intervallet).

## Exempel
- Köp 2024-01-01, garanti 12 mån, idag 2024-06-01 → `idagAndel` mellan 0 och 1, `garantiSlutAndel` ≈ 0,33.
- Köp före 2022-05-01 → `null`.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*TidslinjeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-12 (test mot exempel ≈0,33; null via reklamationSlut; ChronoUnit-import)
