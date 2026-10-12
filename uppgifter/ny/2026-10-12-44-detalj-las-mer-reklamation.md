# Uppgift: «Läs mer» i reklamationskortet

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: cursor:2026-10-12-ny-design-f2

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- befintliga strängar `detalj_reklamation_*`

## Uppgift
I reklamationskortet på detaljsidan:

1. Rubrik: `detalj_reklamation_kort_rubrik` — sv «Din reklamationsrätt», en «Your right of complaint» (eller motsvarande naturlig en).
2. Visa **inte** hela `detalj_reklamation_forklaring` direkt. Visa en `TextButton` «Läs mer» / «Read more» (`detalj_reklamation_las_mer`).
3. När användaren trycker, expandera (t.ex. `AnimatedVisibility`) och visa `detalj_reklamation_forklaring` och `detalj_reklamation_vagledning`. Andra tryck kan fälla ihop.

Behåll hantering för köp före 2022-05-01 (`detalj_reklamation_aldre_lag`).

## Exempel
- Detalj med reklamation → kort med rubrik, «Läs mer» synlig, förklaring dold tills expanderad.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
