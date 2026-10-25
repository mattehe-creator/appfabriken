# Uppgift: Påminnelsens fördröjning

## Vad som saknades
- Filen `rostfangst/src/test/kotlin/se/tmconnect/rostfangst/regler/FangsterPerDagTest.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).

uppdrag: uppdrag/2026-10-19-rostfangst-minsta-version.md
försök: 2
beror-på: cursor:r-stf-ngst-modul-344d

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/PaminnelseFordrojning.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/regler/PaminnelseFordrojningTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/paminnelse/PaminnelsePlanerare.kt

## Uppgift
Implementera `fun fordrojning(nu: Instant, start: Instant): Duration?`.
Returnera `Duration.between(nu, start)` när start ligger efter nu, annars null.

## Exempel
- nu 10:00, start 11:00 → 1 timme.
- nu 11:00, start 10:00 → null.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*PaminnelseFordrojningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-25
