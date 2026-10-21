# Uppgift: Åtgärder per år

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt

## Uppgift
Skapa `Fondplan.kt` i paketet `se.tmconnect.styrelserummet.regler`:

```kotlin
data class PlaneradAtgard(val ar: Int, val belopp: Long, val beskrivning: String)

fun summaPerAr(atgarder: List<PlaneradAtgard>, franAr: Int, antalAr: Int): List<Long>
```

Listan har `antalAr` element; element `i` är summan av beloppen för år `franAr + i`. Åtgärder utanför perioden räknas inte. `antalAr` mindre än 1 eller större än 100 ger `IllegalArgumentException`. Ett negativt belopp ger `IllegalArgumentException`.

## Exempel
Åtgärder: (2026, 100 000, "Takvård"), (2028, 600 000, "Fönster"), (2028, 300 000, "Balkonger"), (2030, 200 000, "Ventilation"), (2040, 1 000 000, "Stammar").
- franAr 2026, antalAr 5 → [100 000, 0, 900 000, 0, 200 000]
- franAr 2027, antalAr 2 → [0, 900 000]
- tom lista, franAr 2026, antalAr 3 → [0, 0, 0]
- antalAr 0 → IllegalArgumentException
- belopp −1 → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FondplanTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
