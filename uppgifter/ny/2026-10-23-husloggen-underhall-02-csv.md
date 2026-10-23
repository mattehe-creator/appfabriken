# Uppgift: CSV för kommande underhåll

uppdrag: uppdrag/2026-10-23-husloggen-kommande-underhall-plan.md
försök: 1
beror-på: 2026-10-23-husloggen-underhall-01-kommande-underhall.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/KommandeUnderhallCsv.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/KommandeUnderhallCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/HusHistorikCsv.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/AvlasningCsv.kt

## Uppgift

```kotlin
data class KommandeUnderhallCsvTexter(
    val kolumnAr: String,
    val kolumnUppgift: String,
    val kolumnDatum: String,
    val kolumnKostnad: String,
    val arSummaRubrik: (Int) -> String,
    val okandKostnad: String,
)

fun kommandeUnderhallCsv(
    grupper: List<KommandeUnderhallArGrupp>,
    texter: KommandeUnderhallCsvTexter,
    datumEtikett: (LocalDate) -> String,
    kostnadEtikett: (Long) -> String,
): String

fun kommandeUnderhallCsvFilnamn(idag: LocalDate = LocalDate.now()): String
```

- En rubrikrad: År, Uppgift, Datum, Uppskattad kostnad (texter från parametern).
- För varje årsgrupp: en rad per `KommandeUnderhallRad`, sedan en sumrad med `arSummaRubrik(ar)` och summa som decimal (samma som `prisSomDecimal` i kärnan) om `summaOre > 0`.
- Okänd kostnad: tom cell eller `okandKostnad`-text i kostnadskolumnen (välj ett och testa det).
- Filnamn: `husloggen-kommande-underhall-yyyy-MM-dd.csv`.

## Exempel
- En grupp 2027 med en rad 50 000 öre → CSV innehåller uppgiftsnamn och `500.00` eller motsvarande decimalformat.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*KommandeUnderhallCsvTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
