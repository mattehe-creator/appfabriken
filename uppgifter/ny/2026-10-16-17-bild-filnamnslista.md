# Uppgift: filnamnslista för postbilder

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BildTolkning.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/BildTolkningTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FelBilder.kt

## Uppgift
Skapa rena funktioner för kolumnen `post.bilder` (kommaseparerade filnamn):

```kotlin
fun tolkaBilder(text: String?): List<String>
fun formateraBilder(lista: List<String>): String?
```

- `null` eller tom text → tom lista.
- Dela på komma, trimma varje del, hoppa över tomma element.
- Tom lista → `null` vid formatering.
- Behåll ordning; inga dubblettfilter (det sköter lagret/ViewModel).

## Exempel
- Indata: `"a.jpg,b.jpg"` → Förväntat: `[a.jpg, b.jpg]`
- Indata: `" a.jpg , ,b.jpg"` → Förväntat: `[a.jpg, b.jpg]`
- Indata: `null` → Förväntat: `[]`
- Indata: `[]` med `formateraBilder` → Förväntat: `null`
- Indata: `[a.jpg, b.jpg]` → Förväntat: `"a.jpg,b.jpg"`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*BildTolkningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
