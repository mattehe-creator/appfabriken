# Uppgift: Stegvis höjning i lika steg

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-stegvis.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StegvisHojning.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StegvisHojningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avgiftskalkyl.kt

## Uppgift
```kotlin
fun stegvisHojning(nuvarande: Long, mal: Long, antalAr: Int): List<Long>?
```

Årsavgifterna år 1 till `antalAr` när avgiften går från `nuvarande` till `mal` i lika stora steg i kronor. Sista året är exakt `mal`.

- År k (1..antalAr): `nuvarande + (mal − nuvarande) × k / antalAr`, avrundat till närmaste krona, halva bort från noll.
- `antalAr` utanför 1..10, eller negativt `nuvarande` eller `mal` → `null`.
- Sänkning (mal < nuvarande) räknas på samma sätt.

## Exempel
- 4 200 000 → 4 970 000 på 3 år → [4 456 667, 4 713 333, 4 970 000]
- 100 → 200 på 1 år → [200]
- 100 → 101 på 2 år → [101, 101] (100,5 avrundas till 101)
- 200 → 100 på 4 år → [175, 150, 125, 100]
- 100 → 99 på 2 år → [99, 99] (99,5 avrundas bort från noll i ändringen: −0,5 → −1)
- antalAr 0 → null; antalAr 11 → null; nuvarande −1 → null

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StegvisHojningTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
utfall: godkänd 2026-10-22 (avrundning via avrundaKvot på stegförändring, JUnit 4 i test)
