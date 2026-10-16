# Uppgift: test för filnamnslista för postbilder

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/BildTolkningTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BildTolkning.kt (finns redan från del A, ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FelBilder.kt

## Uppgift
Funktionerna `tolkaBilder(text: String?): List<String>` och `formateraBilder(lista: List<String>): String?` finns redan i `BildTolkning.kt`. Skriv enhetstester för dem. Hittar du ett fel i implementationen: rätta det inte, skriv felet i PR-beskrivningen.

## Exempel
- Indata: `"a.jpg,b.jpg"` → Förväntat: `[a.jpg, b.jpg]`
- Indata: `" a.jpg , ,b.jpg"` → Förväntat: `[a.jpg, b.jpg]`
- Indata: `null` → Förväntat: `[]`
- Indata: `""` → Förväntat: `[]`
- Indata: `[]` med `formateraBilder` → Förväntat: `null`
- Indata: `[a.jpg, b.jpg]` → Förväntat: `"a.jpg,b.jpg"`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*BildTolkningTest"

## Gör inte
- Ändra andra filer än testfilen.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-16 (inget)
