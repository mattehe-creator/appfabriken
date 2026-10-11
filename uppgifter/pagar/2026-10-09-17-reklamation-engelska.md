# Uppgift: naturlig engelska för reklamationsrätt

uppdrag: uppdrag/2026-10-09-reklamationsratt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/main/res/values-sv/strings.xml
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt

## Uppgift
Uppgift 12 lade till engelska texter med "Reclaiming rights", som inte är naturlig engelska. Byt bara värdena (inte nycklarna) för dessa tre nycklar i den engelska filen:

| Nyckel | Nytt värde |
|--------|------------|
| `status_reklamation_galler` | `Consumer claim period active` |
| `detalj_reklamation_till` | `Right to claim until %1$s` |
| `detalj_reklamation_rubrik` | `Right to claim faults` |

Ändra också `detalj_reklamation_forklaring` till:
`You can claim faults that appear within three years of purchase. Faults that appear within two years are presumed to have existed at purchase. Counted from the purchase date and applies to goods bought from a business.`

## Exempel
- Engelska `status_reklamation_galler` blir exakt `Consumer claim period active`.
- `detalj_reklamation_till` innehåller fortfarande bara `%1$s`.
- Ingen nyckel läggs till eller tas bort; `StringsNycklarTest` passerar.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra den svenska filen eller andra filer.
- Lägga till eller ta bort nycklar.
- Lägga till beroenden.
