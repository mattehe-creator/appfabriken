# Uppgift: loggen per år

uppdrag: uppdrag/2026-10-16-husloggen-grund.md
försök: 1
beror-på: cursor:2026-10-16-husloggen-grund

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/LoggGrupp.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/LoggGruppTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Post.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/ListaGrupp.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`.

```kotlin
data class ArGrupp(
    val ar: Int,
    val poster: List<Post>,
    val summaOre: Long,
)

fun grupperaPerAr(poster: List<Post>): List<ArGrupp>
```

- Gruppera efter `post.datum.year`.
- År i **fallande** ordning (nyaste året först).
- Inom varje år: behåll posternas ordning från indata.
- `summaOre` = summan av `beloppOre` för poster där `beloppOre != null`; poster utan belopp räknas inte i summan men räknas i antalet poster.

## Exempel
Poster:
- 2025-05-01, belopp 100_000 öre (1 000 kr)
- 2026-03-01, belopp 50_000 öre (500 kr)
- 2026-04-01, belopp null

Resultat:
- `[2026, summaOre = 50_000, 2 poster]`
- `[2025, summaOre = 100_000, 1 post]`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*LoggGruppTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
