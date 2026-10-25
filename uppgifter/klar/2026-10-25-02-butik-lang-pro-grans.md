# Uppgift: butik lang.txt utan bostadsgräns

uppdrag: uppdrag/2026-10-25-husloggen-pro-forklaring-bostadsgrans.md
försök: 1
beror-på: 2026-10-25-01-pro-forklaring-strangar.md

## Ändra bara dessa filer
- husloggen/butik/sv/lang.txt
- husloggen/butik/en/lang.txt

## Läs som förebild (ändra inte)
- husloggen/APP.md
- husloggen/src/main/res/values-sv/strings.xml (`pro_forklaring` efter uppgift 01)

## Uppgift
I den långa butiksbeskrivningen (`lang.txt`), ta bort formuleringar om gräns på antal bostäder (t.ex. "3 bostäder" / "3 homes"). Beskriv gratisversionen med **15 poster i loggen** som enda Pro-relaterade gräns, i linje med `APP.md` och appsträngarna.

Justera "tar bort gränserna" / "removes the limits" till singular där det bara gäller loggpostgränsen. Behåll övrigt innehåll (funktioner, export gratis, m.m.) så kort och sakligt som idag.

## Exempel
- `sv/lang.txt` innehåller inte "3 bostäder" eller liknande.
- `en/lang.txt` innehåller inte "3 homes".
- Båda filer ≤ 4 000 tecken och utan http/www.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra andra butiksfiler, appsträngar eller kod.
- Lägga till beroenden.

## Utfall
Klar. Gjord av Cursor direkt i PR #715 (strängar och Roborazzi). Butikstexten hade redan rättats på main; Claude löste konflikten 2026-10-25.
