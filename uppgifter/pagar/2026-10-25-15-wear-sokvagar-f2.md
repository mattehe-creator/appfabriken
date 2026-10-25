# Uppgift: Wear Data Layer-sökvägar

## Vad som saknades
- Bygget eller testerna är röda. Kör testet i uppgiften och rätta tills det är grönt. Testerna i appmodulerna använder JUnit 4: import org.junit.Test och import org.junit.Assert.assertEquals (eller assertTrue, assertFalse), inte kotlin.test.

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/wear/WearSokvagar.kt (ny)
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/wear/WearSokvagarTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/ForslagTyp.kt

## Uppgift
Skapa objektet `WearSokvagar` med konstanter:
- `FANGST` = `/rostfangst/fangst`
- `FORSLAG` = `/rostfangst/forslag`
- `UTFOR` = `/rostfangst/utfor`
- `KLART` = `/rostfangst/klart`

Implementera `fun arKandSokvag(s: String): Boolean` som returnerar `true` bara för dessa fyra exakta strängar.

## Exempel
- `arKandSokvag("/rostfangst/fangst")` → `true`
- `arKandSokvag("/rostfangst/okand")` → `false`
- `arKandSokvag("rostfangst/fangst")` → `false` (saknar inledande `/`)

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*WearSokvagarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
