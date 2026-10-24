# Uppgift: Test av sortering och senaste avläsning

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/AvlasningUrvalTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareForbrukning.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Avlasning.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/MatareForbrukningTest.kt

## Uppgift
JUnit 4-tester för `sorteraAvlasningar(avlasningar)` och `senasteAvlasningForeDatum(datum, avlasningar)`. Skapa avläsningar med `Avlasning(id = …, matareId = 1, datum = LocalDate.of(…), varde = …)`.

Avläsningar i exemplen (i denna ordning i listan):
- a: id 3, 2026-03-01, värde 300
- b: id 1, 2026-01-01, värde 100
- c: id 2, 2026-03-01, värde 290

## Exempel
- `sorteraAvlasningar(listOf(a, b, c)).map { it.id }` → `[1, 2, 3]` (datum först, sedan id)
- `sorteraAvlasningar(emptyList())` → tom lista
- `senasteAvlasningForeDatum(LocalDate.of(2026, 2, 15), listOf(a, b, c))?.id` → `1`
- `senasteAvlasningForeDatum(LocalDate.of(2026, 3, 1), listOf(a, b, c))?.id` → `3` (samma dag räknas, högsta id sist)
- `senasteAvlasningForeDatum(LocalDate.of(2025, 12, 31), listOf(a, b, c))` → `null`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*AvlasningUrvalTest"

## Gör inte
- Ändra andra filer.

utfall: godkänd 2026-10-24 (Long-literaler i assertEquals mot Avlasning.id)
