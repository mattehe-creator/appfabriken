# Uppgift: avgiftarGiltig och felmeddelande

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-14-avgift-berakna.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Overlatelseavgift.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/OverlatelseavgiftGiltigTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till:

```kotlin
fun avgiftarGiltig(avgift: Long, prisbasbelopp: Long, typ: Avgiftstyp, data: Map<String, StammaRad>): Boolean

fun avgiftFelmeddelande(avgift: Long, prisbasbelopp: Long, typ: Avgiftstyp, data: Map<String, StammaRad>): String?
```

- Returnerar true om `avgift <=` respektive högsta gräns (OVERLATELSE/PANTSATTNING via referens i data, ANDRAHAND via `hogstaAndrahandsavgift`).
- `avgiftFelmeddelande` returnerar null om giltig, annars en kort svensk mening på klarspråk som nämner typ (överlåtelse/pantsättning/andrahand), angiven avgift, gränsen i kronor, och för överlåtelse/pantsättning att **stadgar** styr den bindande nivån (referensvärde, inte lagtak). Ingen juridisk rådgivning i texten.

## Exempel
- avgift 1480, pbb 59200, OVERLATELSE, standarddata → true, null
- avgift 2000, samma → false, meddelande innehåller "1480" och "överlåtelse"
- avgift 6000, ANDRAHAND, pbb 59200 → false (gräns 5920)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*OverlatelseavgiftGiltigTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
