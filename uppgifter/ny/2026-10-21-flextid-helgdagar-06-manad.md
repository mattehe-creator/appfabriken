# Uppgift: Arbetsdagar och normaltid för en månad

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: 2026-10-21-flextid-helgdagar-05-uppslag.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsNormaltid.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ManadsNormaltidTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt

## Uppgift
Skapa `ManadsNormaltid.kt` i paketet `se.tmconnect.flextid.regler`:

```kotlin
fun arbetsdagarIManad(
    ar: Int,
    manad: Int,
    arbetsdagarMask: Int,
    uteslutRodaDagar: Boolean,
    uteslutAftnar: Boolean,
): Int

fun normaltidForManadMin(
    ar: Int,
    manad: Int,
    arbetsdagarMask: Int,
    normaltidMinPerDag: Int,
    uteslutRodaDagar: Boolean,
    uteslutAftnar: Boolean,
): Int
```

- `arbetsdagarIManad` går igenom varje dag i månaden (`YearMonth.of(ar, manad)`). En dag räknas om `arArbetsdag(datum, arbetsdagarMask)` är sann och den inte utesluts. Röda dagar (`arRodDag`) utesluts när `uteslutRodaDagar` är sann. Aftnar utesluts när `uteslutAftnar` är sann; en afton är en dag i `svenskaAftnar(ar)`.
- `normaltidForManadMin` = `arbetsdagarIManad(...) * normaltidMinPerDag`.

## Exempel
Mask `ARBETSDAGAR_MANDAG_FREDAG`:
- April 2026, uteslut röda: true, aftnar: true → 20 (22 vardagar minus långfredagen 3/4 och annandag påsk 6/4)
- April 2026, uteslut röda: false → 22
- Maj 2026, röda: true → 19 (1 maj och Kristi himmelsfärd 14/5 är vardagar)
- Juni 2026, röda: true, aftnar: false → 22 (nationaldagen är en lördag)
- Juni 2026, röda: true, aftnar: true → 21 (midsommarafton 19/6)
- December 2026, röda: true, aftnar: false → 22
- December 2026, röda: true, aftnar: true → 20
- `normaltidForManadMin(2026, 4, mask, 480, true, true)` → 9600

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ManadsNormaltidTest"

## Gör inte
- Ändra andra filer.
- Ändra `Arbetsdagar.kt` eller `Helgdagar.kt`.
- Lägga till beroenden.
