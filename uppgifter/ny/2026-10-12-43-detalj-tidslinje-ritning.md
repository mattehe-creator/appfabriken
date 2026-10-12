# Uppgift: tidslinje — ritning på detaljsidan

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: 2026-10-12-42-detalj-tidslinje-berakning.md, cursor:2026-10-12-ny-design-f2

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/TidslinjeStapel.kt (ny)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Tidslinje.kt

## Uppgift
1. **`TidslinjeStapel.kt`**: Composable `TidslinjeStapel(markorer: TidslinjeMarkorer, modifier: Modifier = Modifier)` — en enkel horisontell stapel (t.ex. `LinearProgressIndicator`-liknande custom `Box`) med tre vertikala markörer vid andelarna garantislut, presumtion, idag. Använd temafärger, minst 48 dp höjd för tryckyta om markörer är interaktiva (annars dekorativ med `contentDescription` för skärmläsare).

2. **`KopDetaljSkarm.kt`**: I statuskortet, anropa `beraknaTidslinje` och rita `TidslinjeStapel` när resultatet inte är null.

## Exempel
- Köp under KKL → stapel syns i statuskortet med tre markörer.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*TidslinjeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
