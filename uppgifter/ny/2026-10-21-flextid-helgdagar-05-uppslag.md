# Uppgift: Slå upp helgdag för ett datum

uppdrag: uppdrag/claude/2026-10-21-flextid-helgdagar.md
försök: 1
beror-på: 2026-10-21-flextid-helgdagar-04-aftnar.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/HelgdagarTest.kt

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt

## Uppgift
Lägg till i `Helgdagar.kt`:

```kotlin
fun helgdagPa(datum: LocalDate, medAftnar: Boolean): Helgdag?
fun arRodDag(datum: LocalDate): Boolean
fun arLedigDag(datum: LocalDate, raknaAftnarSomLediga: Boolean): Boolean
```

- `helgdagPa`: första elementet i `svenskaHelgdagar(datum.year)` med samma datum. Finns inget och `medAftnar` är `true`: första elementet i `svenskaAftnar(datum.year)` med samma datum. Annars `null`.
- `arRodDag`: `true` om datumet finns i `svenskaHelgdagar(datum.year)`. Aftnar ger `false`.
- `arLedigDag`: `arRodDag(datum)`, eller om `raknaAftnarSomLediga` är `true` även en afton.

Ingen cache behövs; listorna är små.

## Exempel
- `helgdagPa(2026-04-03, false)?.namn` → "Långfredagen"
- `helgdagPa(2026-12-24, false)` → null
- `helgdagPa(2026-12-24, true)?.namn` → "Julafton", `arAfton` → true
- `helgdagPa(2008-05-01, false)?.namn` → "Första maj"
- `helgdagPa(2026-04-07, true)` → null
- `arRodDag(2026-06-06)` → true
- `arRodDag(2026-06-19)` → false
- `arLedigDag(2026-06-19, true)` → true
- `arLedigDag(2026-06-19, false)` → false
- `arLedigDag(2026-05-14, false)` → true

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*HelgdagarTest"

## Gör inte
- Ändra andra filer.
- Ändra befintliga funktioner i `Helgdagar.kt`.
- Lägga till beroenden.
