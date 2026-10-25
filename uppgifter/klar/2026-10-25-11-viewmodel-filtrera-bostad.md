# Uppgift: Använd filtreraPaBostad i ViewModel

uppdrag: uppdrag/2026-10-25-husloggen-flera-bostader.md
försök: 1
beror-på: cursor:2026-10-25-husloggen-flera-bostader

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/ui/HusViewModelExportTest.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BostadFilter.kt

## Uppgift
I `HusViewModel`, där export (PDF, CSV, kommande underhåll) hämtar poster/uppgifter via `repository.allaPoster()` eller liknande och sedan filtrerar manuellt, använd i stället `filtreraPaBostad(lista, bostadId) { it.bostadId }` från `regler/BostadFilter.kt`.

Behåll samma beteende: export ska bara omfatta vald bostad.

Lägg ett enhetstest i `HusViewModelExportTest` som verifierar att export med två bostäder i databasen bara tar med den valda bostadens poster (mocka eller använd befintlig testsetup).

## Exempel
- Bostad A har post "A1", bostad B har "B1", vald bostad A: CSV/PDF-data innehåller bara "A1".

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusViewModelExportTest*"

## Gör inte
- Ändra databas eller repository.
- Lägga till beroenden.
- Ändra andra filer.

utfall: underkänd 2026-10-25 av Cursor (PR #701). Ersatt av 2026-10-25-11-viewmodel-filtrera-bostad-f2.md
