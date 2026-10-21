beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast de två angivna Kotlin-filerna och uppgiftsflytt `ny/` → `pagar/`; korrekt sökväg under `flextid/`, inget i repots rot.
- Båda Kotlin-filerna är helt tomma (0 byte): ingen `PeriodSammanfattning`, ingen `periodSammanfattning`, inga tester. Uppgiften är därmed inte implementerad.
- CI kan bli grönt trots det: tom `.kt`-fil kompilerar och `--tests "*PeriodSammanfattningTest"` hittar ingen testklass att köra.
- Inga nya beroenden, behörigheter eller nätverk (ingen produktionskod tillagd).
- Ingen `FRAGA.md` i repots rot.
- `:flextid:testDebugUnitTest --tests "*PeriodSammanfattningTest"` kunde inte köras lokalt (saknar Android SDK); granskningen bygger på filinnehåll och diff mot `main`.

## Rättat av Cursor
- inget (saknad implementation — försök 2 till den lokala modellen)

## Vid underkänd: försök 2

# Uppgift: Sammanfattning för ett intervall

uppdrag: uppdrag/claude/2026-10-22-flextid-oversikt.md
försök: 2
beror-på: 2026-10-22-flextid-oversikt-01-normaltid-intervall.md

## Vad som saknades
Försök 1 skapade `PeriodSammanfattning.kt` och `PeriodSammanfattningTest.kt` på rätt sökvägar men lämnade båda filerna helt tomma. Ingen `data class`, ingen `periodSammanfattning`-funktion och inga JUnit-tester. Implementera enligt avsnitten nedan och verifiera med testerna mot exemplen.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattning.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattningTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsNormaltid.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/NormaltidIntervallTest.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidReglerTest.kt

## Uppgift
```kotlin
data class PeriodSammanfattning(val arbetadMin: Int, val normaltidMin: Int, val utfallMin: Int)

fun periodSammanfattning(
    dagar: List<se.tmconnect.flextid.data.Dag>,
    fran: LocalDate,
    till: LocalDate,
    installning: se.tmconnect.flextid.data.Installning,
    aftnarLediga: Boolean,
): PeriodSammanfattning
```

- Ta bara dagar med `datum` från och med `fran` till och med `till` (inklusive).
- `arbetadMin` = summan av `arbetadeMinuter(start, slut, rastMin)` för dagar med typ `ARBETE` (semester/sjuk m.m. räknas inte in i arbetad tid).
- `utfallMin` = summan av `dagensUtfall(dag, installning.normaltidMinPerDag, installning.arbetsdagarMask)` för samma filtrerade dagar.
- `normaltidMin` = `normaltidForIntervallMin(fran, till, installning.arbetsdagarMask, installning.normaltidMinPerDag, aftnarLediga)` — beror på intervallet, inte på vilka dagar som finns i listan.

## Exempel
Installning normaltid 480, mask måndag–fredag (`ARBETSDAGAR_MANDAG_FREDAG`). Vecka 2026-10-12 – 2026-10-18:
- Mån 08:00–16:30 rast 30, tis 07:45–16:45 rast 30, ons 08:00–16:30 rast 30, tors SEMESTER, fre 08:15–15:00 rast 15
- → `PeriodSammanfattning(arbetadMin = 1860, normaltidMin = 2400, utfallMin = -60)`  
  (arbetad: 480 + 510 + 480 + 0 + 390; utfall: 0 + 30 + 0 + 0 − 90)
- Tom lista `dagar`, samma `fran`/`till` → `(0, 2400, 0)`

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*PeriodSammanfattningTest"

Skriv minst två `@Test` med `org.junit.Test` och `org.junit.Assert.assertEquals` som jämför `periodSammanfattning(...)` mot exemplen ovan (vecka med fem dagar respektive tom lista). Importera `Dag`, `DagTyp`, `Installning`, `LocalDate`, `LocalTime` som i `TidReglerTest`.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Använda `kotlin.test`.

## Frågor till Claude
- inga
