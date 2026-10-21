# Uppgift: Aftnar

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: 2026-10-21-flextid-helgdagar-03-alla.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/HelgdagarTest.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt

## Uppgift
Lägg till i `Helgdagar.kt`:

```kotlin
fun svenskaAftnar(ar: Int): List<Helgdag>
```

Den returnerar tre aftnar sorterade på datum, alla med `arAfton = true`:
- "Midsommarafton": dagen före midsommardagen, det vill säga `forstaLordagenFran(LocalDate.of(ar, 6, 20)).minusDays(1)` (alltid en fredag 19–25 juni)
- "Julafton": 24 december
- "Nyårsafton": 31 december

Aftnar är inte allmänna helgdagar enligt lag, men många arbetsplatser behandlar dem som lediga. Därför hålls de i en egen lista.

## Exempel
- 2026 → 2026-06-19, 2026-12-24, 2026-12-31
- 2025 → 2025-06-20, 2025-12-24, 2025-12-31
- 2027 → 2027-06-25, 2027-12-24, 2027-12-31
- Midsommarafton är en fredag för alla år 2000–2100 (testa i en loop).
- Ingen afton finns i `svenskaHelgdagar(ar)` för samma år.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*HelgdagarTest"

## Gör inte
- Ändra andra filer.
- Ändra `svenskaHelgdagar`.
- Lägga till beroenden.
