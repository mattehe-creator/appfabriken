# Uppgift: Repository — koppla genvägsregler

uppdrag: uppdrag/2026-10-23-flextid-genvag-stampla.md
försök: 1
beror-på: 2026-10-23-01-genvag-stampla-regler.md

**OBS:** Implementeras på grenen `cursor/flextid-appgenv-gar-f731` tillsammans med genvägar och UI. Hoppa över när Cursor-PR är mergad.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/data/FlextidRepository.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/data/FlextidRepositoryStamplaTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/test/kotlin/se/tmconnect/flextid/data/FlextidRepositoryGransTest.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/GenvagStampla.kt

## Uppgift
Utöka `SparaDagResult` med `RedanInstamplad(start)`, `IngenInstampling` och `RedanUtstamplad(slut)`.

`stamplaIn` ska använda `planeraStamplaIn` och spara bara vid `Spara`; returnera övriga varianter utan DB-ändring.

`stamplaUt` ska använda `planeraStamplaUt`; vid `Spara` anropa befintlig `sparaDag`.

## Exempel (Room in-memory, som GransTest)
- `stamplaIn` två gånger samma dag → andra anropet `RedanInstamplad` och start oförändrad.
- `stamplaUt` utan föregående in → `IngenInstampling`, inga rader.
- `stamplaIn` + `stamplaUt` → `Ok` och slut satt.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*FlextidRepositoryStamplaTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
