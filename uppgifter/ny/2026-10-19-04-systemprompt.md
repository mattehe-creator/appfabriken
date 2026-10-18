# Uppgift: Systemprompt

uppdrag: uppdrag/2026-10-19-rostfangst-minsta-version.md
försök: 1
beror-på: cursor:rostfangst-minsta-version

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/Systemprompt.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/regler/SystempromptTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ui/RostfangstViewModel.kt

## Uppgift
Implementera `fun byggSystemprompt(nu: ZonedDateTime): String`.
Texten ska innehålla dagens datum, veckodag på svenska och tidszonen (Europe/Stockholm när `nu` har den zonen).
Be om svenska svar och enbart JSON enligt uppdragets schema.

## Exempel
- `nu` = 2026-10-19 (måndag) i Europe/Stockholm. Förväntat: strängen innehåller "måndag", "2026-10-19" och "Europe/Stockholm".

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*SystempromptTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
