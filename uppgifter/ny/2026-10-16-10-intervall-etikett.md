# Uppgift: intervalltext för listrad

uppdrag: uppdrag/2026-10-16-husloggen-uppgifter.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/IntervallEtikett.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/IntervallEtikettTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`.

1. `enum class IntervallEnhet { MANAD, AR }`
2. `fun intervallEtikett(manader: Int): Pair<IntervallEnhet, Int>`
   - Om `manader` är ett positivt heltal som är delbart med 12 **och** antalet hela år är minst 1, returnera `(AR, manader / 12)`.
   - Annars returnera `(MANAD, manader)`.

UI mappar paret till pluralresurserna `intervall_ar` respektive `intervall_manader` (uppgift 09).

## Exempel
- `intervallEtikett(1)` → `(MANAD, 1)`
- `intervallEtikett(6)` → `(MANAD, 6)`
- `intervallEtikett(12)` → `(AR, 1)`
- `intervallEtikett(18)` → `(MANAD, 18)`
- `intervallEtikett(24)` → `(AR, 2)`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*IntervallEtikettTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
