# Uppgift: Pro-rad i köplistan

uppdrag: uppdrag/2026-10-11-pro-gratisgrans.md
försök: 1
beror-på: 2026-10-11-18-pro-strangar.md, 2026-10-11-20-pro-dialog.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/res/values/strings.xml

## Uppgift
I köplistans toppområde (under appnamnet, ovanför eller under påminnelsereglaget — välj konsekvent layout):

1. Om `arPro` är **true**: visa `pro_du_har` (ingen köpknapp).
2. Om `arPro` är **false**: visa en rad med text `pro_las_upp` som knapp eller `TextButton` med minst 48 dp tryckyta; tryck öppnar samma Pro-dialog som uppgift 20 (`visaProDialog()`).

Använd `collectAsState()` på `arPro` från ViewModel i `MainActivity` och skicka in till `KopListaSkarm`.

## Exempel
- Gratisanvändare: rad «Lås upp Pro» syns; tryck → dialog.
- Pro-användare: rad «Du har Pro» syns; ingen köpknapp i raden.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Dölja befintliga köp eller påminnelser bakom Pro.
