# Uppgift: Semester tagna och kvar

uppdrag: uppdrag/claude/2026-10-21-flextid-gahem-semester.md
försök: 1
beror-på: 2026-10-21-flextid-gahem-04-rakna-typ.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Semester.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterTest.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/data/Dag.kt

## Uppgift
Lägg till i `Semester.kt`:

```kotlin
data class SemesterSammanfattning(
    val ar: SemesterAr,
    val uttagna: Int,
    val planerade: Int,
    val kvar: Int,
)

const val SEMESTERDAGAR_STANDARD = 25

fun semesterSammanfattning(
    dagar: List<Dag>,
    idag: LocalDate,
    arbetsdagarMask: Int,
    dagarPerAr: Int = SEMESTERDAGAR_STANDARD,
    startManad: Int = SEMESTERAR_STARTMANAD_STANDARD,
): SemesterSammanfattning
```

- `ar = semesterArFor(idag, startManad)`.
- `uttagna` = `raknaDagarAvTyp(dagar, DagTyp.SEMESTER, ar.fran, idag, mask)` (till och med idag).
- `planerade` = samma från `idag.plusDays(1)` till `ar.till`.
- `kvar` = `dagarPerAr - uttagna - planerade`. Kan bli negativt; begränsa inte.
- Negativt `dagarPerAr` räknas som 0.

## Exempel
Idag 2026-10-21, mask mån–fre, 25 dagar. Semester: 2026-07-06 till 2026-07-10 (fem vardagar), 2026-12-28 och 2026-12-29 (mån, tis), 2026-03-30 (förra semesteråret):
- ar 2026-04-01 till 2027-03-31, uttagna 5, planerade 2, kvar 18
- Med `dagarPerAr` 6 → kvar −1
- Tom lista → uttagna 0, planerade 0, kvar 25
- Idag 2026-03-31 med samma dagar → ar 2025-04-01 till 2026-03-31, uttagna 1, planerade 0, kvar 24

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SemesterTest"

## Gör inte
- Ändra andra filer.
- Ändra tidigare funktioner i `Semester.kt`.
- Lägga till beroenden.
