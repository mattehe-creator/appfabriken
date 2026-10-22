# Uppgift: tidplan gränsfall

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-09-tidplan-samlad.md

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammotidplanGransfallTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Stammotidplan.kt
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till tester för `tidplan` / `senasteStammodag`:

1. Räkenskapsår slut **31 augusti 2024** → senaste stämma **28 februari 2025**.
2. Räkenskapsår slut **31 augusti 2023** → senaste stämma **29 februari 2024** (skottår).
3. Stämma på **exakt** senaste stammodag → `stammaInomLagligFrist` true.
4. Stämma dagen **efter** senaste stammodag → false.
5. `kallelsefristVeckor = 2..2` ger senaste kallelse två veckor före stämman (kontrollera ett datum).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammotidplanGransfallTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
