beslut: underkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Diff mot `main`: endast flytt `uppgifter/ny/` → `uppgifter/pagar/`; inga ändringar i `HusViewModel.kt` eller `HusViewModelExportTest.kt` (PR #701 bekräftar saknade filer och röda tester lokalt).
- Uppgiften kräver `filtreraPaBostad` i exportvägarna; `HusViewModel` använder fortfarande `repository.posterForBostad` / `repository.uppgifterForBostad` utan `filtreraPaBostad`.
- Inget nytt enhetstest för två bostäder (exemplet A1/B1) — befintliga `HusViewModelExportTest` oförändrade mot uppgiften.
- Inga filer utanför uppgiftslistan (för att ingen kod committades); ingen `FRAGA.md` på grenen.
- Befintliga tester i modulen använder JUnit 4; inga nya beroenden, behörigheter eller nätverk i diffen.

## Rättat av Cursor
- inget (saknad implementation — den lokala modellens uppgift att göra om)

## Vid underkänd: försök 2

```
# Uppgift: Använd filtreraPaBostad i ViewModel

## Vad som saknades (försök 1)
- Ingen kod committades: grenen innehåller bara att uppgiftsfilen flyttats till `pagar/`.
- `HusViewModel.kt` och `HusViewModelExportTest.kt` är oförändrade mot `main`.
- Exportvägarna anropar inte `filtreraPaBostad` från `BostadFilter.kt`.
- Inget test med två bostäder som verifierar att historik-CSV bara innehåller vald bostads poster (exemplet A1/B1).

Gör så här:
1. Importera `se.tmconnect.husloggen.regler.filtreraPaBostad`.
2. I `posterForExport(bostadId)`: returnera `filtreraPaBostad(repository.allaPoster(), bostadId) { it.bostadId }` (ersätt anropet till `repository.posterForBostad(...).first()`).
3. I `sparaKommandeUnderhallCsv`: hämta uppgifter med `filtreraPaBostad(repository.allaUppgifter(), bostadId) { it.bostadId }` i stället för `repository.uppgifterForBostad(bostadId).first()`.
4. Lägg till `@Test fun exportHistorikCsvTarBaraMedValdBostad()` (eller liknande namn): skapa bostad B via `repository.sparaBostad`, spara post `"A1"` på vald bostad A och `"B1"` på B, kör historik-CSV-export, assert att CSV innehåller `"A1"` och inte `"B1"`. Använd befintlig setup (`registreraFil`, `vantaPaMeddelande`, `sparaHistorikCsv`).

uppdrag: uppdrag/2026-10-25-husloggen-flera-bostader.md
försök: 2
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
```

## Frågor till Claude
- inga
