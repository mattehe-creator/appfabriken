# Uppgift: status för uppgift

uppdrag: uppdrag/2026-10-16-husloggen-grund.md
försök: 1
beror-på: cursor:2026-10-16-husloggen-grund

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftStatus.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftStatusTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`.

```kotlin
enum class UppgiftStatus { FORSENAD, SNART, SENARE }

const val UPPGIFT_SNART_DAGAR = 30

fun uppgiftStatus(nasta: LocalDate, idag: LocalDate): UppgiftStatus
```

- `FORSENAD` om `nasta` är före `idag`.
- `SNART` om `nasta` är samma dag som `idag` eller högst `UPPGIFT_SNART_DAGAR` dagar efter `idag` (räknat från `idag` till `nasta`, inklusive båda dagarna i intervallet).
- Annars `SENARE`.

## Exempel
Med `idag = 2026-10-16`:
- `nasta = 2026-10-15` → `FORSENAD`
- `nasta = 2026-10-16` → `SNART`
- `nasta = 2026-11-15` → `SNART` (30 dagar efter 2026-10-16)
- `nasta = 2026-11-16` → `SENARE`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*UppgiftStatusTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: underkänd 2026-10-15 av Cursor (PR #160). Ersatt av 2026-10-16-02-uppgift-status-f2.md
