# Uppgift: Sparade semesterdagar

uppdrag: uppdrag/claude/2026-10-24-flextid-semester-statistik.md
försök: 1
beror-på: 2026-10-24-01-semester-roda-dagar-f2.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Semester.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterSparadeTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterSammanfattningTest.kt

## Uppgift
Den som har sparat semesterdagar från tidigare år har fler dagar att ta ut.

1. Lägg till parametern `sparadeDagar: Int = 0` sist i `semesterSammanfattning`.
2. `kvar` blir `dagarPerAr.coerceAtLeast(0) + sparadeDagar.coerceAtLeast(0) - uttagna - planerade`.
3. Lägg till fältet `sparade: Int = 0` sist i `SemesterSammanfattning` och fyll det med `sparadeDagar.coerceAtLeast(0)`.

## Exempel
Arbetsdagar måndag–fredag, semesterår april, idag 2026-10-21, semester 2026-07-06 till 2026-07-10 (5 dagar), inga planerade.
- `sparadeDagar = 0`, 25 per år → `kvar = 20`, `sparade = 0`.
- `sparadeDagar = 7` → `kvar = 27`, `sparade = 7`.
- `sparadeDagar = -3` → räknas som 0: `kvar = 20`, `sparade = 0`.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SemesterSparadeTest" --tests "*SemesterSammanfattningTest"

## Gör inte
- Ändra andra filer.
- Ändra befintliga tester.

utfall: underkänd 2026-10-24 av Cursor (PR #647). Ersatt av 2026-10-24-02-semester-sparade-dagar-f2.md
