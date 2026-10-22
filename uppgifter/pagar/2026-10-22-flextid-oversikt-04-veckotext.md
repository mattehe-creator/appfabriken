# Uppgift: Veckan som text att dela

uppdrag: uppdrag/claude/2026-10-22-flextid-oversikt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/VeckoText.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/VeckoTextTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidFormatering.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt

## Uppgift
Användaren vill kunna skicka veckans tider till sin chef via valfri app (appen har ingen internetbehörighet; delningen går via systemets dela-meny, som Claude kopplar in).

```kotlin
data class VeckoTextTexter(
    val rubrik: String,          // till exempel "Vecka 42"
    val veckodagar: Map<DayOfWeek, String>,
    val typEtiketter: Map<DagTyp, String>,
    val summaEtikett: String,    // till exempel "Summa"
)

fun veckoText(dagar: List<Dag>, mandag: LocalDate, texter: VeckoTextTexter, locale: Locale): String
```

- Första raden: `texter.rubrik`.
- Sedan en rad per dag måndag–söndag som har en post i `dagar` (dagar utan post hoppas över), i datumordning:
  - Arbete med start och slut: `"<veckodag> <start>–<slut> (<arbetad tid>)"`, tider med `formateraTid`, arbetad tid med `formateraMinuterSomTid(arbetadeMinuter(...))`.
  - Arbete med bara start: `"<veckodag> <start>–"`.
  - Annan typ: `"<veckodag> <typetikett>"`.
- Sista raden: `"<summaEtikett> <total arbetad tid>"` (summan av arbetad tid för hela dagar).
- Rader skiljs med `\n`, ingen avslutande radbrytning.

## Exempel
Måndag 2026-10-12; dagar: mån 08:00–16:30 rast 30, tis SEMESTER, ons 07:45–16:45 rast 30, tors 08:00 utan slut.
Texter: rubrik "Vecka 42", veckodagar "Mån", "Tis", …, typer SEMESTER → "Semester", summa "Summa". Locale sv-SE.
→
```
Vecka 42
Mån 08:00–16:30 (8:00)
Tis Semester
Ons 07:45–16:45 (8:30)
Tors 08:00–
Summa 16:30
```
(Kontrollera formatet på tider och timmar mot `formateraTid` och `formateraMinuterSomTid` och skriv testet efter vad de faktiskt ger.)

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*VeckoTextTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
