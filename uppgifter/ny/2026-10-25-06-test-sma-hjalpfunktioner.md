# Uppgift: Test av små hjälpfunktioner i Husloggen

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/HusSmaHjalpfunktionerTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForsaljningSammanstallning.kt (de två sista funktionerna)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareTolkning.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/KommandeUnderhallCsv.kt (sista funktionen)

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`, `assertTrue`, `assertFalse`) i paketet `se.tmconnect.husloggen.regler` för:

- `arSverigeForSkatteInfo(landskod: String): Boolean`
- `standardForsaljningsar(idag: LocalDate): Int`
- `formateraMatareVarde(tusendel: Long): String`
- `kommandeUnderhallCsvFilnamn(idag: LocalDate): String` (ligger i paketet `se.tmconnect.husloggen.export`; importera `se.tmconnect.husloggen.export.kommandeUnderhallCsvFilnamn`)

Skicka alltid in ett datum; anropa inte funktionerna utan argument (då används dagens datum och testet blir olika från dag till dag).

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
