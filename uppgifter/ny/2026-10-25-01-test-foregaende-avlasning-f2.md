# Uppgift: Test av föregående avläsning

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 2
beror-på: ingen

## Vad som saknades
- Filen `husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/ForegaendeAvlasningTest.kt` skapades inte. Grenen innehöll bara flytt av uppgiftsfilen till `uppgifter/pagar/`.
- Skapa testfilen på exakt den sökvägen (inte i repots rot). Förebild för paket, imports och JUnit 4-stil: `husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/AvlasningValideringTest.kt`.

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/ForegaendeAvlasningTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/AvlasningValidering.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/AvlasningValideringTest.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `org.junit.Assert.assertEquals`, `org.junit.Assert.assertNull`) för
`foregaendeAvlasning(datum: LocalDate, sorterade: List<Avlasning>, redigerarId: Long = 0L): Avlasning?`.

Funktionen tar bort avläsningen med id `redigerarId`, behåller avläsningar på eller före `datum` och returnerar den med senast datum.

Använd dessa tre avläsningar (importera `se.tmconnect.husloggen.data.Avlasning` och `java.time.LocalDate`). Ge alltid ett id från 1 och uppåt: en avläsning med id 0 filtreras bort eftersom `redigerarId` som standard är 0.

val a1 = Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 10), varde = 1_000_000)
val a2 = Avlasning(id = 2, matareId = 1, datum = LocalDate.of(2026, 2, 10), varde = 1_200_000)
val a3 = Avlasning(id = 3, matareId = 1, datum = LocalDate.of(2026, 3, 10), varde = 1_500_000)

Jämför med `assertEquals(a2, resultat)` (samma objekt) eller `assertNull(resultat)`.

## Exempel
- `foregaendeAvlasning(LocalDate.of(2026, 2, 15), listOf(a1, a2, a3))` → `a2`
- `foregaendeAvlasning(LocalDate.of(2026, 2, 10), listOf(a1, a2, a3))` → `a2` (samma dag räknas med)
- `foregaendeAvlasning(LocalDate.of(2026, 1, 9), listOf(a1, a2, a3))` → `null` (alla är senare)
- `foregaendeAvlasning(LocalDate.of(2026, 12, 31), listOf(a1, a2, a3))` → `a3`
- `foregaendeAvlasning(LocalDate.of(2026, 3, 10), listOf(a1, a2, a3), redigerarId = 3)` → `a2`
- `foregaendeAvlasning(LocalDate.of(2026, 2, 10), listOf(a1, a2, a3), redigerarId = 2)` → `a1`
- `foregaendeAvlasning(LocalDate.of(2026, 2, 20), listOf(a3, a1, a2))` → `a2` (ordningen i listan spelar ingen roll)
- `foregaendeAvlasning(LocalDate.of(2026, 2, 20), emptyList())` → `null`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*ForegaendeAvlasningTest"

## Gör inte
- Ändra andra filer.
- Ändra `AvlasningValidering.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Lägga till beroenden.
- Använd `kotlin.test`; använd JUnit 4 (`org.junit`).
