# Uppgift: Prisuppräkning i fondplan-formuläret

uppdrag: uppdrag/2026-10-22-styrelserummet-verktyg-indexering-forandring.md
försök: 1
beror-på: 2026-10-22-styrelserummet-fond-07-koppla-indexering.md

cursor:2026-10-22-styrelserummet-verktyg-indexering-forandring

## Ändra bara dessa filer
- styrelserummet-server/src/main/resources/static/fondplan.html
- styrelserummet-server/src/main/resources/static/fondplan.js

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/resources/static/nyckeltal.js

## Uppgift
Valfritt fält för antagen årlig prisuppräkning (0–10 %, en decimal). Skicka som `arligPrisupprakningProcent` i POST `/api/fondplan`. Svenska etiketter och hjälptext.

## Exempel
- Tomt fält → null i JSON.
- "2,5" → `2.5`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*VerktygApiTest.rutterna*"

## Gör inte
- Ändra VerktygApi.kt.
- Lägga till beroenden.
