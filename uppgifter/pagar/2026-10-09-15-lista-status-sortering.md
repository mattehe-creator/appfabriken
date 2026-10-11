# Uppgift: lista med samlad status och ny sortering

uppdrag: uppdrag/2026-10-09-reklamationsratt.md
försök: 1
beror-på: 2026-10-09-12-reklamation-strangar.md, 2026-10-09-13-kop-status.md, 2026-10-09-14-sortera-narmaste-frist.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopStatus.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopSortering.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt (nuvarande garanti-status)

## Uppgift
1. **`KopListRad`**: använd `kopStatus(...)` i stället för enbart `garantiStatus`. Statusetikett och färg:
   - `GARANTI_GALLER` → `status_galler`, `primary`
   - `GARANTI_SNART` → `status_gar_ut_snart`, `tertiary`
   - `REKLAMATION_GALLER` → `status_reklamation_galler`, `primary`
   - `UTGANGEN` → `status_utgangen`, `error`
   Raden med garantislutdatum (`lista_garanti_slut`) behålls oförändrad.
2. **`KopViewModel`**: sortera med `sorteraEfterNarmasteFrist(..., LocalDate.now())` i stället för `sorteraEfterGaranti`.

## Exempel
- Köp där garantin gått ut men reklamation gäller visar «Reklamationsrätt gäller» (eller motsvarande engelska sträng).
- Listans ordning följer `sorteraEfterNarmasteFrist` för dagens datum.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopSorteringNarmasteFristTest" --tests "*KopStatusTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
