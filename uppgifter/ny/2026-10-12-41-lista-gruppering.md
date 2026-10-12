# Uppgift: gruppering av listan

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: cursor:2026-10-12-ny-design-f2

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/ListaGrupp.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/ListaGruppTest.kt (ny)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopStatus.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopSortering.kt

## Uppgift
1. **`ListaGrupp.kt`**: enum `ListaGrupp { GAR_UT_SNART, GALLER, UTGANGNA }` och funktion  
   `fun listaGrupp(kop: Kop, idag: LocalDate): ListaGrupp`  
   där `GAR_UT_SNART` = `kopStatus` är `GARANTI_SNART`, `GALLER` = `GARANTI_GALLER` eller `REKLAMATION_GALLER`, `UTGANGNA` = `UTGANGEN`.

2. **`KopSkarmar.kt`**: Gruppera `kopLista` i LazyColumn med rubriker `lista_grupp_snart`, `lista_grupp_galler`, `lista_grupp_utgangna` (strängar från del A). Tomma grupper visas inte. Ordning: snart → gäller → utgångna.

3. Tester med minst tre köp i olika grupper givet fast `idag`.

## Exempel
- Garanti utgår om 10 dagar → `GAR_UT_SNART`.
- Reklamationsrätt aktiv efter garantin → `GALLER`.
- Allt utgånget → `UTGANGNA`.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ListaGruppTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
