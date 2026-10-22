# Uppgift: Föregående år i nyckeltal-formuläret

uppdrag: uppdrag/2026-10-22-styrelserummet-verktyg-indexering-forandring.md
försök: 1
beror-på: 2026-10-22-styrelserummet-nyckeltal-07-koppla-forandring.md

cursor:2026-10-22-styrelserummet-verktyg-indexering-forandring

## Ändra bara dessa filer
- styrelserummet-server/src/main/resources/static/nyckeltal.html
- styrelserummet-server/src/main/resources/static/nyckeltal.js
- styrelserummet-server/src/main/resources/static/gemensam.css

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/resources/static/fondplan.js

## Uppgift
Valfri sektion med underlag för föregående år. Skicka `foregaendeAr` när användaren fyllt i data. Visa `forandring` från API bredvid värdet för det nyaste året. Svenska texter.

## Exempel
- Sektion stängd → endast `{ ar: [...] }`.
- Sparande med förändring → cell visar värde och "+10,4 %" i `.forandring`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*VerktygApiTest.rutterna*"

## Gör inte
- Ändra VerktygApi.kt.
- Lägga till beroenden.
