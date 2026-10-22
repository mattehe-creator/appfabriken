# Uppgift: Prognos för årets förbrukning

uppdrag: uppdrag/claude/2026-10-22-husloggen-sok-forbrukning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForbrukningPrognos.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/ForbrukningPrognosTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareForbrukning.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Avlasning.kt

## Uppgift
```kotlin
fun arsprognos(avlasningar: List<Avlasning>, ar: Int, idag: LocalDate): Long?
```

Skatta hela årets förbrukning (tusendelar av enheten) utifrån avläsningarna hittills i år, linjärt per dag.

- Ta avläsningarna sorterade på datum. Första punkten är den senaste avläsningen **före** 1 januari `ar` om den finns, annars den första avläsningen i året. Sista punkten är den senaste avläsningen till och med `idag` inom året.
- Saknas två olika punkter, eller är perioden mellan dem kortare än 14 dagar → `null`.
- Förbrukning per dag = (sista värdet − första värdet) / antal dagar mellan dem. Årets prognos = förbrukning per dag × antal dagar i året (365 eller 366). Räkna i heltal: `(diff * dagarIAret * 2 + dagar) / (2 * dagar)`.
- Negativ diff (mätarbyte) → `null`.

Läs `Avlasning.kt` för fältnamnen (datum och värde i tusendelar).

## Exempel
- Avläsningar 2025-12-31: 10 000 000, 2026-07-01: 15 000 000; år 2026, idag 2026-10-22 → 182 dagar, diff 5 000 000 → (5 000 000 × 365 × 2 + 182) / 364 = 10 027 473
- Bara en avläsning → null
- 2026-01-01: 0 och 2026-01-10: 100 → null (för kort)
- 2025-12-31: 500, 2026-03-01: 400 → null

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*ForbrukningPrognosTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.

utfall: godkänd 2026-10-22 (JUnit-import, Long-literal, rättat förväntat värde i testForstaAvlasningInomAr)
