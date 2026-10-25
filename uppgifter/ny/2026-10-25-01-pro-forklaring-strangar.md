# Uppgift: Pro-förklaring utan bostadsgräns

uppdrag: uppdrag/2026-10-25-husloggen-pro-forklaring-bostadsgrans.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/APP.md (gratisgräns och flera bostäder)
- uppgifter/klar/2026-10-16-25-pro-strangar.md

## Uppgift
Uppdatera `pro_forklaring` på engelska och svenska så att texten stämmer med `APP.md` och Pro-logiken: gratisversionen har alla funktioner med gräns på **15 poster i loggen** (totalt över alla bostäder). Nämn **inte** någon gräns på antal bostäder.

Behåll övrigt innehåll: Pro är engångsköp utan prenumeration; uppgifter, påminnelser, mätare och dokument är obegränsade; export, PDF och säkerhetskopia/backup är alltid gratis. Använd singular **gränsen** (inte "gränserna") när bara loggpostgränsen avses.

Sök i samma två filer efter andra strängar som nämner en bostadsgräns (t.ex. "3 bostäder", "3 homes") och rätta dem. Om inga fler finns, ändra bara `pro_forklaring`.

## Exempel
- Svenska `pro_forklaring` innehåller "15 poster i loggen", nämner inte bostadsgräns, nämner engångsköp utan prenumeration.
- Engelska `pro_forklaring` innehåller "15 log entries", nämner inte "3 homes" eller home limit.
- `pro_grans_nadd` ska fortfarande nämna 15 poster (oförändrat om redan rätt).

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra Pro-logik eller butikstexter.
