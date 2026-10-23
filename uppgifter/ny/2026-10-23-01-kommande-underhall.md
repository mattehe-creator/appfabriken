# Uppgift: Kommande underhåll per år

uppdrag: uppdrag/2026-10-23-husloggen-kommande-underhall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/KommandeUnderhall.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/KommandeUnderhallTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/Kommande.kt (`forekomster`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnad.kt (`senasteKostnadPerUppgift`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Uppgift.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Post.kt

## Uppgift

```kotlin
data class KommandeUnderhallRad(
    val uppgiftId: Long,
    val namn: String,
    val datum: LocalDate,
    val kostnadOre: Long?,
)

data class KommandeUnderhallArGrupp(
    val ar: Int,
    val rader: List<KommandeUnderhallRad>,
    val summaOre: Long,
    val saknarKostnad: Boolean,
)

fun kommandeUnderhall(
    uppgifter: List<Uppgift>,
    loggposter: List<Post>,
    idag: LocalDate,
    antalAr: Int = 5,
): List<KommandeUnderhallArGrupp>
```

- `senasteKostnad = senasteKostnadPerUppgift(loggposter)`.
- `slut = idag.plusYears(antalAr.toLong())`. För varje uppgift med `intervallManader >= 1`: anropa `forekomster(senastGjord, startDatum, intervallManader, idag, slut)`. Uppgifter med intervall under 1 hoppas över (inga undantag).
- Varje förfallodatum blir en rad; `kostnadOre = senasteKostnad[uppgiftId]` (null om okänd, aldrig gissad).
- Gruppera rader på `datum.year`. Inom året: sortera på datum, sedan namn.
- `summaOre` = summan av icke-null `kostnadOre` i året. `saknarKostnad` = sant om minst en rad i året har null kostnad.
- Returnera år sorterade stigande; år utan rader finns inte med.

## Exempel

Idag 2026-10-23, `antalAr = 1` (slut 2027-10-23). Uppgift A: senast 2026-09-01, intervall 3, id 1, kostnad 50 000 öre i loggen.
- 2026-12-01 och 2027-03-01 och 2027-06-01 och 2027-09-01 (fyra tillfällen inom spannet).

Uppgift B: intervall 0 → inga rader.

Uppgift C: ingen loggpost med kostnad → `kostnadOre = null`, årets `saknarKostnad = true`, `summaOre` räknar bara A.

## Test som ska gå igenom

gradle :husloggen:testDebugUnitTest --tests "*KommandeUnderhallTest"

Testfall som ska finnas:

- månadsintervall (flera tillfällen inom ett år)
- årsintervall (12 månader)
- uppgift utan kostnad (null på rad och `saknarKostnad`)
- uppgift utan intervall (intervall 0, inga rader)
- skottdag (senast 2026-01-31, intervall 1 → 2026-02-28)
- `antalAr = 1` begränsar sista datum

## Gör inte

- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
