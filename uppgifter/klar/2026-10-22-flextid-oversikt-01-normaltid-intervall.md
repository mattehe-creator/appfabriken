# Uppgift: Normaltid för ett intervall

uppdrag: uppdrag/claude/2026-10-22-flextid-oversikt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsNormaltid.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/NormaltidIntervallTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt

## Uppgift
Lägg till i `ManadsNormaltid.kt`:

```kotlin
fun arbetsdagarIIntervall(fran: LocalDate, till: LocalDate, arbetsdagarMask: Int, aftnarLediga: Boolean): Int
fun normaltidForIntervallMin(fran: LocalDate, till: LocalDate, arbetsdagarMask: Int, normaltidMinPerDag: Int, aftnarLediga: Boolean): Int
```

- Räkna varje datum från och med `fran` till och med `till` där `arArbetsdag(datum, mask)` är sann och `arLedigDag(datum, aftnarLediga)` är falsk. Röda dagar räknas alltså alltid bort.
- `fran` efter `till` → 0.
- `normaltidForIntervallMin` = antalet gånger `normaltidMinPerDag`.
- Importera `java.time.LocalDate`.

## Exempel
Mask `ARBETSDAGAR_MANDAG_FREDAG`:
- 2026-10-12 – 2026-10-18 → 5
- 2026-03-30 – 2026-04-05 (påskveckan) → 4 (långfredagen 3/4 räknas bort, annandag påsk 6/4 ligger utanför)
- 2026-12-21 – 2026-12-27, aftnar lediga → 3 (24/12 afton och 25/12 juldagen räknas bort); aftnar inte lediga → 4
- `normaltidForIntervallMin(2026-10-12, 2026-10-18, mask, 480, true)` → 2400
- `fran` 2026-10-18, `till` 2026-10-12 → 0

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*NormaltidIntervallTest"

## Gör inte
- Ändra andra filer eller befintliga funktioner.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (testimport kotlin.test → org.junit.Assert)
