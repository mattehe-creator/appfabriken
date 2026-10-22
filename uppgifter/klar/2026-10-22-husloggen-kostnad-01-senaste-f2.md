# Uppgift: Senaste kostnad per uppgift

uppdrag: uppdrag/claude/2026-10-22-husloggen-kostnad.md
försök: 2
beror-på: ingen

## Vad som saknades

- `husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnad.kt` och `UppgiftKostnadTest.kt` committades som **tomma filer** (0 byte). Det finns ingen `senasteKostnadPerUppgift` och inga enhetstester.
- Filerna måste innehålla riktig Kotlin-kod som kompilerar och tester som **misslyckas** om logiken är fel (inte bara en tom testklass).

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnad.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnadTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/PostBelopp.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Post.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/PostBeloppTest.kt (JUnit 4: `org.junit.Test`, `org.junit.Assert`)

## Uppgift
fun senasteKostnadPerUppgift(poster: List<Post>): Map<Long, Long>

- Ta bara poster där `uppgiftId` inte är null och `postBeloppForSammanstallningOre(post)` inte är null (anropa funktionen från `PostBelopp.kt`).
- Per `uppgiftId`: välj posten med senast `datum`. Samma datum → högst `id`.
- Värdet i map:en är `postBeloppForSammanstallningOre` för den valda posten (öre, efter rot/rut).

Tips: filtrera först, gruppera på `uppgiftId`, välj vinnare med `maxBy` eller sortering på `(datum, id)`.

## Exempel
Poster (id, uppgiftId, datum, beloppOre, rotRutAvdragOre) — övriga fält kan vara minimala som i `PostBeloppTest` (`vad`, `typ`, `datum`):

- 1, 7, 2025-05-01, 120 000, null
- 2, 7, 2024-05-01, 150 000, 30 000
- 3, 7, 2026-09-01, null, null  → ska ignoreras (null belopp)
- 4, 8, 2026-01-01, 50 000, null
- 5, 8, 2026-01-01, 60 000, 10 000  → samma datum som 4, högre id vinner
- 6, null, 2026-02-01, 99 900, null  → ska ignoreras (null uppgiftId)

Förväntat resultat: `mapOf(7L to 120_000L, 8L to 50_000L)` — för uppgift 8: id 5 ger netto 60_000 − 10_000 = **50_000**, inte brutto 60_000.

Tom lista → tom map.

## Test som ska gå igenom
- Minst ett test som bygger sex `Post`-objekt enligt exemplen ovan och `assertEquals` på hela map:en.
- Ett test med `emptyList()` → `assertTrue(result.isEmpty())`.
- Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`.

gradle :husloggen:testDebugUnitTest --tests "*UppgiftKostnadTest"

## Gör inte
- Ändra andra filer.
- Lämna tomma källfiler.

utfall: godkänd 2026-10-22 (lokal modell levererade ingen kod; Cursor implementerade UppgiftKostnad.kt och UppgiftKostnadTest.kt)
