# Uppgift: Semesterår för ett datum

uppdrag: uppdrag/claude/2026-10-21-flextid-gahem-semester.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Semester.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt

## Uppgift
Skapa `Semester.kt` i paketet `se.tmconnect.flextid.regler`:

```kotlin
data class SemesterAr(val fran: LocalDate, val till: LocalDate)

const val SEMESTERAR_STARTMANAD_STANDARD = 4

fun semesterArFor(datum: LocalDate, startManad: Int = SEMESTERAR_STARTMANAD_STANDARD): SemesterAr
```

Semesteråret börjar den 1:a i `startManad` och slutar dagen före samma datum nästa år. `startManad` 1 betyder kalenderår.

- Om `datum.monthValue >= startManad`: `fran = LocalDate.of(datum.year, startManad, 1)`.
- Annars: `fran = LocalDate.of(datum.year - 1, startManad, 1)`.
- `till = fran.plusYears(1).minusDays(1)`.
- `startManad` utanför 1..12: `IllegalArgumentException`.

## Exempel
- 2026-10-21, start 4 → 2026-04-01 till 2027-03-31
- 2026-03-31, start 4 → 2025-04-01 till 2026-03-31
- 2026-04-01, start 4 → 2026-04-01 till 2027-03-31
- 2026-10-21, start 1 → 2026-01-01 till 2026-12-31
- 2028-02-29, start 4 → 2027-04-01 till 2028-03-31
- start 13 → IllegalArgumentException

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SemesterTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (JUnit 4 i SemesterTest i stället för kotlin.test)
