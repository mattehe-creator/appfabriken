# Uppgift: Röda dagar räknas inte som semesterdagar

uppdrag: uppdrag/claude/2026-10-24-flextid-semester-statistik.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Semester.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterRodaDagarTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Ledighet.kt (funktionen `arArbetsdagMedHelger`)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterSammanfattningTest.kt

## Uppgift
En semesterdag registrerad på en röd dag (till exempel Kristi himmelfärdsdag) eller på en afton när aftnar är lediga ska inte räknas som uttagen semesterdag.

1. Lägg till parametern `aftnarLediga: Boolean = true` sist i `raknaDagarAvTyp`. Byt villkoret `arArbetsdag(d.datum, arbetsdagarMask)` mot `arArbetsdagMedHelger(d.datum, arbetsdagarMask, aftnarLediga)`.
2. Lägg till parametern `aftnarLediga: Boolean = true` sist i `semesterSammanfattning` och skicka den vidare till båda anropen av `raknaDagarAvTyp`.

## Exempel
Arbetsdagar måndag–fredag (`ARBETSDAGAR_MANDAG_FREDAG`), semesterår april (startManad 4).
- Semester 2026-05-13, 2026-05-14 (Kristi himmelfärdsdag), 2026-05-15. `semesterSammanfattning(dagar, idag = 2026-05-20, mask)` → `uttagna = 2`, `kvar = 23`.
- Semester 2026-06-19 (midsommarafton). `raknaDagarAvTyp(dagar, DagTyp.SEMESTER, 2026-06-01, 2026-06-30, mask)` → 0. Samma anrop med `aftnarLediga = false` → 1.
- Semester 2026-06-18 (torsdag) → 1 i båda fallen.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SemesterRodaDagarTest" --tests "*SemesterSammanfattningTest"

## Gör inte
- Ändra andra filer.
- Ändra befintliga tester.
- Lägga till beroenden.

utfall: underkänd 2026-10-24 av Cursor (PR #637). Ersatt av 2026-10-24-01-semester-roda-dagar-f2.md
