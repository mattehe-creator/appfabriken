# Uppgift: sortera uppgifter

uppdrag: uppdrag/2026-10-16-husloggen-grund.md
försök: 1
beror-på: cursor:2026-10-16-husloggen-grund

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftSortering.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftSorteringTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Uppgift.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopSortering.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`.

```kotlin
fun sorteraUppgifter(uppgifter: List<Uppgift>, idag: LocalDate): List<Uppgift>
```

Sortera efter **nästa datum** (stigande, beräknat med `nastaDatum` för varje uppgift), sedan `namn` utan hänsyn till stora och små bokstäver (`String.CASE_INSENSITIVE_ORDER`).

## Exempel
Med `idag = 2026-10-16`:
- Uppgift A: namn "Tak", start 2026-01-01, intervall 12, senast gjord null → nästa 2026-01-01 (försenad).
- Uppgift B: namn "Fönster", start 2026-06-01, intervall 6, senast gjord 2026-06-01 → nästa 2026-12-01.
- Uppgift C: namn "altan", start 2026-11-01, intervall 12, senast gjord null → nästa 2026-11-01.
- Ordning: A (2026-01-01), C (2026-11-01), B (2026-12-01).
- Två uppgifter med samma nästa datum sorteras på namn: "Gärdes" före "gärdes" räknas lika; "B" före "a" med case-insensitive → "a" före "B".

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*UppgiftSorteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
