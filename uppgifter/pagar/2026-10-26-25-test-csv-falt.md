# Uppgift: Test av CSV-fältets citering (Röstfångst)

uppdrag: uppdrag/claude/2026-10-26-testtackning-dag.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/export/CsvFaltTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/export/HistorikCsv.kt (funktionen `csvFalt`, `internal`, nås från modulens test)

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) i paketet `se.tmconnect.rostfangst.export` för `csvFalt(text: String): String`. Text utan komma, citattecken eller radbrytning returneras oförändrad. I övriga fall omges den av citattecken och inre citattecken dubbleras.

## Exempel
- "" ger "" och "köp mjölk" ger "köp mjölk".
- "a,b" ger texten omgiven av citattecken: "a,b" med citattecken runt.
- text med ett inre citattecken, till exempel säg "hej", ger texten omgiven av citattecken där det inre citattecknet är dubblerat.
- "rad1" + radbrytning + "rad2" ger texten omgiven av citattecken.
- "rad1" + vagnretur + "rad2" ger texten omgiven av citattecken.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*CsvFaltTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
- Ändra databasschemat.
