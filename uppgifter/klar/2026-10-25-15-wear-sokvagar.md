# Uppgift: Wear Data Layer-sökvägar

uppdrag: uppdrag/vantar/2026-10-25-rostfangst-klocka.md
försök: 1
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

utfall: underkänd 2026-10-25 av Cursor (PR #721). Ersatt av 2026-10-25-15-wear-sokvagar-f2.md
