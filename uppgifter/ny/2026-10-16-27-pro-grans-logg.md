# Uppgift: gräns i loggen (ny post)

uppdrag: uppdrag/2026-10-16-husloggen-pro-gratisgrans.md
försök: 1
beror-på: 2026-10-16-25-pro-strangar.md, 2026-10-16-26-pro-dialog-f2.md, cursor:2026-10-16-husloggen-pro-gratisgrans-del-a

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt (FAB / `kanLaggaTillNy`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt

## Uppgift
När användaren vill skapa en **ny loggpost** (FAB på loggfliken, tom-logg-knappen, etc.):

1. Anropa `viewModel.kanLaggaTillNyPost()` (eller motsvarande som Cursor exponerat).
2. Om **true**: befintligt beteende (`visaNyPost()`).
3. Om **false**: `viewModel.visaProDialog()` i stället för att öppna postformuläret.

Redigering av befintliga poster (`visaAndraPost`) ska **inte** blockeras av gränsen.

Skicka in `arPro` eller callbacks från `MainActivity` till `HusStartSkarm` vid behov för Pro-raden i senare uppgift — håll signaturen enkel.

## Exempel
- 15 poster, inte Pro, tryck «Ny post» → Pro-dialog, inget formulär.
- 14 poster → formulär öppnas som tidigare.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra repository eller databasschema.
- Blockera visning eller redigering av befintliga poster.
