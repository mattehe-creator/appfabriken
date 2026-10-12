# Uppgift: «Läs mer» i reklamationskortet

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 2
beror-på: ingen

## Vad som saknades
- Ingen kod levererades: `KopDetaljSkarm.kt` och strängfilerna ändrades inte. PR:en innehöll bara att uppgiftsfilen flyttades till `pagar/`.
- `ReklamationsKort` (rad ~370 i `KopDetaljSkarm.kt`) måste uppdateras enligt uppgiften nedan.

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- befintliga strängar `detalj_reklamation_*`

## Uppgift
I reklamationskortet på detaljsidan (`ReklamationsKort`):

1. Rubrik: `detalj_reklamation_kort_rubrik` (redan korrekt i strängfilerna).
2. Behåll raden med `detalj_reklamation_till` när `reklamationSlutDatum != null`.
3. Visa **inte** `detalj_reklamation_forklaring` direkt. Lägg en `TextButton` med texten från `detalj_reklamation_las_mer` («Läs mer» / «Read more»).
4. Håll expanderat tillstånd i `remember { mutableStateOf(false) }`. Vid tryck på knappen: växla tillståndet.
5. När expanderat: visa `detalj_reklamation_forklaring` och under den `detalj_reklamation_vagledning` (t.ex. i `AnimatedVisibility(visible = expanderad)`).
6. Behåll `detalj_reklamation_aldre_lag` för köp före 2022-05-01 (`kop.kopdatum.isBefore(KONSUMENTKOP_IKRAFT)`) utan «Läs mer»-flöde om det är enda innehållet.

Behövs import: `androidx.compose.animation.AnimatedVisibility` (eller motsvarande från Compose animation).

## Exempel
- Detalj med reklamation (slutdatum finns) → kort med rubrik, datumrad, «Läs mer» synlig, förklaring och vägledning dolda tills användaren tryckt «Läs mer»; nytt tryck fäller ihop.
- Köp före 2022-05-01 → endast äldre-lag-text som idag.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
