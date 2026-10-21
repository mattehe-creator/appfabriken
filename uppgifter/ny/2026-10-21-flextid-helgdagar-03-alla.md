# Uppgift: Alla allmänna helgdagar för ett år

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: 2026-10-21-flextid-helgdagar-02-rorliga-f2.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/HelgdagarTest.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt

## Uppgift
Lägg till i `Helgdagar.kt`:

```kotlin
fun forstaLordagenFran(datum: LocalDate): LocalDate
fun svenskaHelgdagar(ar: Int): List<Helgdag>
```

`forstaLordagenFran` returnerar `datum` om det är en lördag, annars närmast följande lördag.

`svenskaHelgdagar` returnerar de 13 allmänna helgdagarna i Sverige, sorterade på datum (stabil sortering: vid samma datum behålls ordningen nedan). Alla har `arAfton = false`.

Fasta:
- "Nyårsdagen" 1 januari
- "Trettondedag jul" 6 januari
- "Första maj" 1 maj
- "Sveriges nationaldag" 6 juni
- "Juldagen" 25 december
- "Annandag jul" 26 december

Beräknade:
- de fem från `rorligaHelgdagar(ar)`
- "Midsommardagen": `forstaLordagenFran(LocalDate.of(ar, 6, 20))`
- "Alla helgons dag": `forstaLordagenFran(LocalDate.of(ar, 10, 31))`

Bygg listan i ordningen: fasta, sedan rörliga, sedan midsommardagen och alla helgons dag. Sortera sist med `sortedBy { it.datum }` (Kotlins `sortedBy` är stabil).

## Exempel
- `forstaLordagenFran(2026-06-20)` → 2026-06-20 (lördag)
- `forstaLordagenFran(2025-06-20)` → 2025-06-21
- `forstaLordagenFran(2027-10-31)` → 2027-11-06
- 2026: Midsommardagen 2026-06-20, Alla helgons dag 2026-10-31, listan har 13 element, första är Nyårsdagen 2026-01-01, sista är Annandag jul 2026-12-26.
- 2025: Midsommardagen 2025-06-21, Alla helgons dag 2025-11-01.
- 2008: både "Första maj" och "Kristi himmelsfärdsdag" har datum 2008-05-01; listan har ändå 13 element och "Första maj" kommer före.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*HelgdagarTest"

## Gör inte
- Ändra andra filer.
- Ta med aftnar (julafton med flera); de kommer i nästa uppgift.
- Lägga till beroenden.
