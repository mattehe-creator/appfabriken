# Uppgift: Anmärkningar för utkast

uppdrag: uppdrag/vantar/2026-10-19-styrelserummet-underlag.md
försök: 1
beror-på: 2026-10-19-styrelserummet-11-hitta-byggnadens-varde.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt (ny)

## Uppgift
`fun anmarkningarFor(utkast: Utkast, tabell: List<NyttjandeperiodRad>): List<String>`

Lägg till svenska anmärkningar om: differens mot byggnadens värde, komponenter med `LAG` eller `UPPSKATTNING`, nyttjandeperiod utanför tabellens intervall.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AnmarkningarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
