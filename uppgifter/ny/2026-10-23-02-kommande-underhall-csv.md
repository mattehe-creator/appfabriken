# Uppgift: CSV för kommande underhåll

uppdrag: uppdrag/2026-10-23-husloggen-kommande-underhall.md
försök: 1
beror-på: 2026-10-23-01-kommande-underhall.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/KommandeUnderhallCsv.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/KommandeUnderhallCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/KommandeUnderhall.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/HusHistorikCsv.kt
- karna/src/main/kotlin/se/tmconnect/karna/export/Csv.kt (csvRad, csvDokument, prisSomDecimal)

## Uppgift

```kotlin
data class KommandeUnderhallCsvTexter(
    val kolumnAr: String,
    val kolumnUppgift: String,
    val kolumnDatum: String,
    val kolumnKostnad: String,
    val arSummaRubrik: (Int) -> String,
    val kostnadOkand: String,
)

fun kommandeUnderhallCsv(
    grupper: List<KommandeUnderhallArGrupp>,
    texter: KommandeUnderhallCsvTexter,
    datumEtikett: (LocalDate) -> String,
): String

fun kommandeUnderhallCsvFilnamn(idag: LocalDate = LocalDate.now()): String
```

- Första raden: rubrikerna (fyra kolumner).
- För varje årsgrupp: en rad per `KommandeUnderhallRad` (år som siffra, namn, formaterat datum, pris som decimal eller `kostnadOkand` om null).
- Efter varje års rader: en sumrad med `arSummaRubrik(ar)` i första kolumnen, tom uppgift/datum, summa som decimal om `summaOre > 0` annars tom kostnadskolumn.
- Använd `csvDokument` / `csvRad` från `:karna`.
- Filnamn: `kommande-underhall-YYYY-MM-DD.csv` (datum = `idag`).

## Exempel

En grupp 2027 med en rad "Filter", datum 2027-03-15, 120000 öre, summa 120000. Rubriker på svenska. CSV innehåller `2027`, `Filter`, datumet, `1200.00` och en sumrad.

## Test som ska gå igenom

gradle :husloggen:testDebugUnitTest --tests "*KommandeUnderhallCsvTest"

## Gör inte

- Ändra andra filer.
- Lägga till beroenden.
