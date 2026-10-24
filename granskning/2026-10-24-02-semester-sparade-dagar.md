beslut: underkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- PR #647 ändrar bara `SemesterSparadeTest.kt` (tom fil, 0 rader) och flytt av uppgiftsfil `ny` → `pagar`; **`Semester.kt` är oförändrad** trots att den ska ändras.
- Uppgiften är inte implementerad: ingen `sparadeDagar` i `semesterSammanfattning`, inget fält `sparade` i `SemesterSammanfattning`, ingen `kvar`-formel med sparade dagar.
- `SemesterSparadeTest.kt` innehåller inga tester; exemplen i uppgiften verifieras inte.
- Inga nya beroenden, behörigheter eller nätverk; inga filer i fel sökväg (repots rot).
- Befintliga `SemesterSammanfattningTest` oförändrade (korrekt enligt «Gör inte»).
- CI grönt missvisande: tom `.kt`-fil ger inga testkörningar för `SemesterSparadeTest`.
- Ingen `FRAGA.md` på grenen.
- Lokalt kunde `:flextid:testDebugUnitTest` inte köras (saknar Android SDK i denna miljö); bygget på grenen rapporterades success.

## Rättat av Cursor
- inget (saknat arbete — inte småfel)

## Vid underkänd: försök 2

```
# Uppgift: Sparade semesterdagar

uppdrag: uppdrag/claude/2026-10-24-flextid-semester-statistik.md
försök: 2
beror-på: 2026-10-24-01-semester-roda-dagar-f2.md

## Vad som saknades
- `flextid/.../Semester.kt` ändrades inte alls.
- `SemesterSparadeTest.kt` skapades som tom fil utan testklass eller assert.
- Parametern `sparadeDagar`, fältet `sparade` och den nya `kvar`-beräkningen saknas.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Semester.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterSparadeTest.kt (ny eller fyll den tomma filen)

## Läs som förebild (ändra inte)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterSammanfattningTest.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SemesterRodaDagarTest.kt (JUnit 4, `org.junit.Assert.assertEquals`)

## Uppgift
Den som har sparat semesterdagar från tidigare år har fler dagar att ta ut.

1. Lägg till parametern `sparadeDagar: Int = 0` **sist** i funktionen `semesterSammanfattning` (efter `aftnarLediga`).
2. Beräkna `kvar` som  
   `dagarPerAr.coerceAtLeast(0) + sparadeDagar.coerceAtLeast(0) - uttagna - planerade`.
3. Lägg till fältet `sparade: Int = 0` **sist** i data class `SemesterSammanfattning` (efter `kvar`).
4. Fyll `sparade` med `sparadeDagar.coerceAtLeast(0)` i returvärdet från `semesterSammanfattning`.

Signatur efter ändring (förenklat):

```kotlin
data class SemesterSammanfattning(
    val ar: SemesterAr,
    val uttagna: Int,
    val planerade: Int,
    val kvar: Int,
    val sparade: Int = 0,
)

fun semesterSammanfattning(
    ...
    aftnarLediga: Boolean = true,
    sparadeDagar: Int = 0,
): SemesterSammanfattning
```

## Exempel
Samma testdata som i `SemesterSammanfattningTest`: arbetsdagar måndag–fredag, semesterår april, idag `2026-10-21`, semester `2026-07-06`–`2026-07-10` (5 uttagna; 2 planerade enligt befintlig testdata), 25 dagar per år om inget annat anges.

- `sparadeDagar = 0` → `kvar = 20`, `sparade = 0`.
- `sparadeDagar = 7` → `kvar = 27`, `sparade = 7`.
- `sparadeDagar = -3` → räknas som 0 → `kvar = 20`, `sparade = 0`.

## Test som ska gå igenom
Skriv minst tre `@Test` i `SemesterSparadeTest` (JUnit 4: `org.junit.Test`, `org.junit.Assert.assertEquals`) — ett per exempel ovan, med `semesterSammanfattning(..., sparadeDagar = …)` och assert på `kvar` och `sparade`.

```
gradle :flextid:testDebugUnitTest --tests "*SemesterSparadeTest" --tests "*SemesterSammanfattningTest"
```

## Gör inte
- Ändra andra filer (inklusive befintliga tester).
- Lägga till beroenden.
- Använda `kotlin.test`.
```

## Frågor till Claude
- inga
