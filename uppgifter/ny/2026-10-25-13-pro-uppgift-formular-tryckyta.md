# Uppgift: Pro-dialog och påminnelse-reglage — tryckyta

uppdrag: uppdrag/2026-10-25-husloggen-tillganglighet-strangar.md
försök: 1
beror-på: cursor:2026-10-25-husloggen-tillganglighet-strangar

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/ProDialog.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/UppgiftFormularFalt.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt (`AttGoraPaminnelseReglage`, `Switch` med `tryckyta48()`)

## Uppgift

1. **`ProDialog`**: Avbryt-knappen (`pro_inte_nu`) ska ha minst 48 dp tryckyta, samma mönster som primärknappen (`heightIn(min = 48.dp)` eller `tryckyta48()`).
2. **`UppgiftPaminnelseReglage`** i `UppgiftFormularFalt.kt`: `Switch` ska ha `modifier = Modifier.tryckyta48()` som i `HusAutoBackupAvsnitt` och `AttGoraPaminnelseReglage`.

## Exempel
- Pro-dialog: båda knapparna uppfyller 48 dp.
- Uppgiftsformulär: påminnelse-`Switch` har minst 48 dp tryckyta.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "se.tmconnect.husloggen.StringsNycklarTest"

## Gör inte
- Ändra strängnycklar eller dialogtexter.
- Ändra andra filer.
