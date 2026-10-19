# Uppgift: Sammanställningsskärm

uppdrag: uppdrag/2026-10-19-prov-aviseringar-skolinbox.md
försök: 1
beror-på: 2026-10-19-04-sammanstallning-berakning.md

## Ändra bara dessa filer
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/ui/AviseringSkarmar.kt
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/ui/AviseringViewModel.kt
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/ui/SammanstallningSkarm.kt (ny)

## Läs som förebild (ändra inte)
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/regler/Sammanstallning.kt

## Uppgift
Lägg till skärmen **Sammanställning** (tredje val i appen) som visar `beraknaSammanstallningPerApp` per app: procent med datum, procent med tid, fördelning per `Typ`. Strängar på svenska och engelska.

## Test som ska gå igenom
gradle :prov-aviseringar:testDebugUnitTest --tests "*AviseringDaoTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
