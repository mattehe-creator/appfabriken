# Uppgift: Räkna dagar av en typ

uppdrag: uppdrag/claude/2026-10-21-flextid-gahem-semester.md
försök: 1
beror-på: 2026-10-21-flextid-gahem-03-semesterar.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Semester.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterTest.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/data/Dag.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/DagTyp.kt

## Uppgift
Lägg till i `Semester.kt`:

```kotlin
fun raknaDagarAvTyp(
    dagar: List<Dag>,
    typ: DagTyp,
    fran: LocalDate,
    till: LocalDate,
    arbetsdagarMask: Int,
): Int
```

Antalet element i `dagar` där `typ` matchar, `datum` ligger från och med `fran` till och med `till`, och `arArbetsdag(datum, arbetsdagarMask)` är sann. Semester på en lördag räknas alltså inte som en semesterdag när masken är måndag–fredag. `fran` efter `till` ger 0.

Importera `se.tmconnect.flextid.data.Dag`.

## Exempel
Mask `ARBETSDAGAR_MANDAG_FREDAG`, intervall 2026-04-01 till 2027-03-31:
- Semester 2026-07-06 (mån), 2026-07-07 (tis), 2026-07-11 (lör) → 2
- Samma dagar men typ SJUK frågad → 0
- Semester 2026-03-31 (tis, före intervallet) → 0
- Semester 2027-03-31 (ons, sista dagen) → 1
- Tom lista → 0
- `fran` 2027-01-01, `till` 2026-01-01 → 0

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SemesterTest"

## Gör inte
- Ändra andra filer.
- Ändra `semesterArFor`.
- Lägga till beroenden.

utfall: utförd av Claude (gren claude/flextid-helgdagar-gahem), 2026-10-21
