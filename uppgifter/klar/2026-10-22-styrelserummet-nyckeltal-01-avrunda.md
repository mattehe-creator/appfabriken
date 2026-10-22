# Uppgift: Underlag och avrundad kvot

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt

## Uppgift
Skapa `Nyckeltal.kt` i paketet `se.tmconnect.styrelserummet.regler`:

```kotlin
data class NyckeltalUnderlag(
    val arsavgifter: Long,
    val rantebarandeSkulder: Long,
    val aretsResultat: Long,
    val avskrivningar: Long,
    val utrangeringar: Long,
    val planeratUnderhall: Long,
    val justeringar: Long,
    val energikostnad: Long,
    val rorelseintakter: Long,
    val kvmBostadsratt: Long,
    val kvmHyresratt: Long,
)

fun avrundaKvot(taljare: Long, namnare: Long): Long
```

Alla belopp i hela kronor, ytor i hela kvadratmeter.

`avrundaKvot` delar och avrundar till närmaste heltal, med halva bort från noll, utan flyttal:
- `namnare <= 0` → `IllegalArgumentException`.
- För `taljare >= 0`: `(2 * taljare + namnare) / (2 * namnare)`.
- För `taljare < 0`: `-avrundaKvot(-taljare, namnare)`.

## Exempel
- (7, 2) → 4
- (−7, 2) → −4
- (5, 3) → 2
- (1, 3) → 0
- (4 200 000, 5 200) → 808
- (0, 5) → 0
- (5, 0) → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.

utfall: godkänd 2026-10-21 (JUnit 4 och @Test i NyckeltalTest)
