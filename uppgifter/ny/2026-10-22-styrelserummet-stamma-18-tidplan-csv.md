# Uppgift: tidplanCsv

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-17-tidplan-rader.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaUtskrift.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaTidplanCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsv.kt
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till:

```kotlin
const val STAMMA_RADGIVNING_RAD = "Allmän information, inte juridisk rådgivning. Kontrollera era stadgar."

fun tidplanCsv(rader: List<StammaTabellRad>): String
```

CSV med semikolon, rubrikrad `etikett;datum;anmärkning;källa`, en rad per tabellrad, **sista raden** exakt `STAMMA_RADGIVNING_RAD` (hela raden, en kolumn utan semikolon i innehållet). Avsluta med `\n`.

Fält som innehåller semikolon eller citattecken ska citeras enligt samma regel som `csvFalt` i `NyckeltalCsv.kt` (kopiera som privat funktion eller duplicera minimalt).

## Exempel
- En rad med etikett "Test" → första raden `etikett;datum;anmärkning;källa`, sista raden radgivningstexten.
- Tom lista → bara rubrik + radgivning + newline.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammaTidplanCsvTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
