# Uppgift: sortera efter närmaste frist

uppdrag: uppdrag/2026-10-09-reklamationsratt.md
försök: 1
beror-på: cursor:2026-10-09-reklamationsratt

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopSortering.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KopSorteringNarmasteFristTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Konsumentkoplagen.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KopSorteringTest.kt

## Uppgift
Lägg till i **`KopSortering.kt`** (rör inte `sorteraEfterGaranti`):

```kotlin
fun sorteraEfterNarmasteFrist(lista: List<Kop>, idag: LocalDate): List<Kop>
```

För varje köp: närmaste **aktiva** frist = garantislut om garantin inte passerat (`!idag.isAfter(garantiSlut(...))`), annars reklamationsslut om `reklamationSlut(kopdatum)` inte är null och inte passerat, annars köpet räknas utgånget.

Sortering:
- Aktiva köp först, sorterade stigande på närmaste frist, sedan `vad` som tie-break.
- Utgångna sist, sorterade fallande på senast passerade frist (den som passerades senast — garanti eller reklamation beroende på vilket som var senast), sedan `vad`.

## Exempel
- Två aktiva köp: garanti om 10 dagar före garanti om 40 dagar.
- Köp med garanti utgången men reklamation om 200 dagar kommer före helt utgånget köp.
- Köp 2022-04-30 (ingen reklamation) med utgången garanti hamnar bland utgångna.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopSorteringNarmasteFristTest"

Befintliga tester i `KopSorteringTest` ska fortsätta gå igenom oförändrade.

## Gör inte
- Ändra `sorteraEfterGaranti` eller testerna i `KopSorteringTest.kt`.
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
