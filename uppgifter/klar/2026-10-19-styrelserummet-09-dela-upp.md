# Uppgift: Dela upp underhållsplans-text

uppdrag: uppdrag/vantar/2026-10-19-styrelserummet-underlag.md
försök: 1
beror-på: cursor:2026-10-19-styrelserummet-underlag

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/DelaUpp.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/DelaUppTest.kt (ny)

## Uppgift
`data class Del(val sidnummer: List<Int>, val text: String)`

`fun delaUpp(sidor: List<String>, maxTecken: Int): List<Del>`

Dela på sidgräns. En sida som ryms får aldrig delas mitt i. En sida som är längre än `maxTecken` blir en egen `Del`.

## Exempel
- Sidor 100 + 100 tecken, max 150 → en `Del` med båda sidorna.
- Sidor 100 + 100, max 120 → två delar med en sida vardera.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*DelaUppTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
