# Uppgift: status för uppgift

uppdrag: uppdrag/2026-10-16-husloggen-grund.md
försök: 2
beror-på: cursor:2026-10-16-husloggen-grund

## Vad som saknades
- `husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftStatus.kt` och `UppgiftStatusTest.kt` fanns på rätt plats men var helt tomma. Försök 1 levererade ingen kod och inga tester.

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftStatus.kt (ny/innehåll)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftStatusTest.kt (ny/innehåll)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`. Inga Android-klasser, bara `java.time.LocalDate`.

enum class UppgiftStatus { FORSENAD, SNART, SENARE }

const val UPPGIFT_SNART_DAGAR = 30

fun uppgiftStatus(nasta: LocalDate, idag: LocalDate): UppgiftStatus

- `FORSENAD` om `nasta` är före `idag` (`nasta.isBefore(idag)`).
- `SNART` om `nasta` inte är före `idag` och antalet dagar från `idag` till `nasta` (via `ChronoUnit.DAYS.between(idag, nasta)`) är högst `UPPGIFT_SNART_DAGAR` — samma idé som `garantiStatus` och `SNART_DAGAR` i förebilden `Garanti.kt`.
- Annars `SENARE`.

## Exempel
Med `idag = 2026-10-16`:
- `nasta = 2026-10-15` → `FORSENAD`
- `nasta = 2026-10-16` → `SNART`
- `nasta = 2026-11-15` → `SNART` (30 dagar efter 2026-10-16)
- `nasta = 2026-11-16` → `SENARE`

## Test som ska gå igenom
Skriv JUnit 4-tester (`org.junit.Test`, `org.junit.Assert.assertEquals`) som täcker alla fyra exemplen ovan med exakt de datum och förväntade `UppgiftStatus`-värden.

gradle :husloggen:testDebugUnitTest --tests "*UppgiftStatusTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Använda `kotlin.test`.
