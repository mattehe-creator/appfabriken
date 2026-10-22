# Uppgift: Förekomster av en uppgift i ett intervall

uppdrag: uppdrag/claude/2026-10-22-husloggen-kommande.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/Kommande.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/KommandeTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Uppgift.kt

## Uppgift
```kotlin
fun forekomster(
    senastGjord: LocalDate?,
    startDatum: LocalDate,
    intervallManader: Int,
    fran: LocalDate,
    till: LocalDate,
): List<LocalDate>
```

Förekomst nummer k (k = 1, 2, 3 …):
- om \`senastGjord\` finns: \`senastGjord.plusMonths(k * intervallManader)\`
- annars: \`startDatum.plusMonths((k - 1) * intervallManader)\`

(Räkna alltid från samma utgångsdatum, inte från förra förekomsten, så att månadsslut inte glider.)

Resultatet:
- Om förekomst 1 ligger före \`fran\` är uppgiften försenad: ta med förekomst 1 först.
- Ta sedan med alla förekomster som ligger från och med \`fran\` till och med \`till\`, i ordning.
- Sluta när en förekomst ligger efter \`till\`, eller efter 120 förekomster.
- \`intervallManader\` mindre än 1 → \`IllegalArgumentException\`.

## Exempel
- senast 2026-01-15, start 2025-01-01, intervall 3, 2026-03-01 – 2027-02-28 → [2026-04-15, 2026-07-15, 2026-10-15, 2027-01-15]
- senast null, start 2026-11-01, intervall 12, 2026-10-22 – 2027-10-21 → [2026-11-01]
- senast 2025-09-01, start 2025-01-01, intervall 12, 2026-10-22 – 2027-10-21 → [2026-09-01, 2027-09-01] (den första är försenad)
- senast 2026-01-31, start 2025-01-01, intervall 1, 2026-02-01 – 2026-04-30 → [2026-02-28, 2026-03-31, 2026-04-30]
- intervall 0 → IllegalArgumentException

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*KommandeTest"

## Gör inte
- Ändra andra filer eller `nastaDatum`.
- Lägga till beroenden.
