# Uppgift: validera uppgiftsformulär

uppdrag: uppdrag/2026-10-16-husloggen-uppgifter.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftValidering.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftValideringTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FormularValidering.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`.

1. `enum class UppgiftFel { NAMN_SAKNAS, INTERVALL_SAKNAS, INTERVALL_UTANFOR }`
2. `fun valideraUppgift(namn: String, intervallManader: Int?): UppgiftFel?`
   - Returnera **ett** fel i taget (early return): kontrollera namn före intervall.
   - `NAMN_SAKNAS` om `namn` är tomt eller blankt efter trim.
   - `INTERVALL_SAKNAS` om `intervallManader` är `null`.
   - `INTERVALL_UTANFOR` om värdet inte är 1–120 (inklusive).
   - `null` om allt är giltigt.

## Exempel
- `("", 12)` → `NAMN_SAKNAS`
- `("  ", null)` → `NAMN_SAKNAS`
- `("Hängrännor", null)` → `INTERVALL_SAKNAS`
- `("Hängrännor", 0)` → `INTERVALL_UTANFOR`
- `("Hängrännor", 121)` → `INTERVALL_UTANFOR`
- `("Hängrännor", 120)` → `null`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*UppgiftValideringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Android-klasser i valideringslogiken.
