# Uppgift: Senaste kostnad per uppgift

uppdrag: uppdrag/claude/2026-10-22-husloggen-kostnad.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnad.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnadTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/PostBelopp.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Post.kt

## Uppgift
```kotlin
fun senasteKostnadPerUppgift(poster: List<Post>): Map<Long, Long>
```

- Ta bara poster där `uppgiftId` inte är null och `postBeloppForSammanstallningOre(post)` inte är null.
- Per `uppgiftId`: välj posten med senast `datum`. Samma datum → högst `id`.
- Värdet är `postBeloppForSammanstallningOre` för den posten (öre, efter rot/rut).

## Exempel
Poster (id, uppgiftId, datum, beloppOre, rotRutAvdragOre):
- 1, 7, 2025-05-01, 120 000, null
- 2, 7, 2024-05-01, 150 000, 30 000
- 3, 7, 2026-09-01, null, null
- 4, 8, 2026-01-01, 50 000, null
- 5, 8, 2026-01-01, 60 000, 10 000
- 6, null, 2026-02-01, 99 900, null

Resultat: `{7: 120 000, 8: 50 000}` (för 8 vinner id 5: 60 000 − 10 000).

Tom lista → tom map.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*UppgiftKostnadTest"

## Gör inte
- Ändra andra filer.

utfall: underkänd 2026-10-22 av Cursor (PR #537). Ersatt av 2026-10-22-husloggen-kostnad-01-senaste-f2.md
