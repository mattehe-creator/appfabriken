# Uppgift: Test av små hjälpfunktioner i Husloggen

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 2
beror-på: ingen

## Vad som saknades
- `husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/HusSmaHjalpfunktionerTest.kt` skapades men är **tom** (0 rader kod).
- Det ska finnas en JUnit 4-klass `HusSmaHjalpfunktionerTest` i paketet `se.tmconnect.husloggen.regler` med `@Test`-metoder som anropar funktionerna och jämför resultat med exemplen nedan.
- Använd `org.junit.Test`, `org.junit.Assert.assertEquals`, `assertTrue` och `assertFalse` — **inte** `kotlin.test`.
- För `kommandeUnderhallCsvFilnamn`: importera `se.tmconnect.husloggen.export.kommandeUnderhallCsvFilnamn`.
- Skicka alltid in `LocalDate` till datumfunktionerna; anropa inte `standardForsaljningsar()` eller `kommandeUnderhallCsvFilnamn()` utan argument.
- Förebild för paket, imports och stil: `AvlasningUrvalTest.kt` i samma mapp.

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/HusSmaHjalpfunktionerTest.kt (fyll i)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForsaljningSammanstallning.kt (de två sista funktionerna)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareTolkning.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/KommandeUnderhallCsv.kt (sista funktionen)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/AvlasningUrvalTest.kt

## Uppgift
Skriv JUnit 4-tester för:

- `arSverigeForSkatteInfo(landskod: String): Boolean`
- `standardForsaljningsar(idag: LocalDate): Int`
- `formateraMatareVarde(tusendel: Long): String`
- `kommandeUnderhallCsvFilnamn(idag: LocalDate): String`

Minst en testmetod som täcker alla exemplen per funktion (fler `@Test` är okej).

## Exempel
- `arSverigeForSkatteInfo("SE")` → `true`
- `arSverigeForSkatteInfo("se")` → `true`
- `arSverigeForSkatteInfo("NO")` → `false`
- `arSverigeForSkatteInfo("")` → `false`
- `standardForsaljningsar(LocalDate.of(2026, 10, 25))` → `2026`
- `standardForsaljningsar(LocalDate.of(2027, 1, 1))` → `2027`
- `formateraMatareVarde(12_345_600L)` → `"12345,6"`
- `formateraMatareVarde(1_005L)` → `"1,005"`
- `formateraMatareVarde(1_050L)` → `"1,05"`
- `formateraMatareVarde(999L)` → `"0,999"`
- `formateraMatareVarde(0L)` → `"0"`
- `kommandeUnderhallCsvFilnamn(LocalDate.of(2026, 10, 25))` → `"husloggen-kommande-underhall-2026-10-25.csv"`
- `kommandeUnderhallCsvFilnamn(LocalDate.of(2027, 1, 5))` → `"husloggen-kommande-underhall-2027-01-05.csv"`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusSmaHjalpfunktionerTest"

## Gör inte
- Ändra andra filer.
- Ändra produktionskoden. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Lägga till beroenden.
