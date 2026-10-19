# Uppgift: Strängar för sammanställning

uppdrag: uppdrag/2026-10-19-prov-aviseringar-skolinbox.md
försök: 1
beror-på: 2026-10-19-05-sammanstallning-skarm.md

## Ändra bara dessa filer
- prov-aviseringar/src/main/res/values/strings.xml
- prov-aviseringar/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- prov-aviseringar/src/main/kotlin/se/tmconnect/prov/aviseringar/ui/AviseringSkarmar.kt

## Uppgift
Lägg till strängresurser som sammanställningsskärmen använder (fliknamn, rubriker, procentetiketter, typnamn). Engelska i `values/`, svenska i `values-sv/`.

## Test som ska gå igenom
gradle :prov-aviseringar:testDebugUnitTest --tests "*IngenNatverkTest"

## Gör inte
- Ändra andra filer.
