# Uppgift: Kommande jobb per månad

uppdrag: uppdrag/claude/2026-10-22-husloggen-kommande.md
försök: 1
beror-på: 2026-10-22-husloggen-kommande-01-forekomster-f2.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/Kommande.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/KommandeTest.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Uppgift.kt

## Uppgift
```kotlin
data class KommandeJobb(val uppgiftId: Long, val namn: String, val datum: LocalDate, val forsenad: Boolean)

fun kommandePerManad(uppgifter: List<Uppgift>, idag: LocalDate, manader: Int = 12): Map<YearMonth, List<KommandeJobb>>
```

- Intervallet är `idag` till och med `idag.plusMonths(manader).minusDays(1)`.
- För varje uppgift: `forekomster(it.senastGjord, it.startDatum, it.intervallManader, idag, slut)`. Varje datum blir ett `KommandeJobb`; `forsenad` är sant om datumet ligger före `idag`.
- Försenade jobb läggs i `YearMonth.from(idag)` (innevarande månad), övriga i sin egen månad.
- Kartan är sorterad på månad (`toSortedMap()`), och listan i varje månad på datum och sedan namn.
- Månader utan jobb finns inte med.

## Exempel
Idag 2026-10-22. Uppgifter: A "Rensa hängrännor" senast 2025-10-01 intervall 12; B "Byta filter" senast 2026-09-15 intervall 3; C "Sotning" senast null start 2027-03-01 intervall 24.
- oktober 2026: [A 2026-10-01 försenad]
- december 2026: [B 2026-12-15]
- mars 2027: [B 2027-03-15, C 2027-03-01] sorterat → [C 2027-03-01, B 2027-03-15]
- juni 2027: [B 2027-06-15]
- september 2027: [B 2027-09-15]
- oktober 2027: [A 2027-10-01]

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*KommandeTest"

## Gör inte
- Ändra andra filer eller `forekomster`.
- Lägga till beroenden.
