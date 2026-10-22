# Uppgift: Avskrivningar per år för en komponent

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-01-heltal.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt

## Uppgift
Skapa `Avskrivningsplan.kt` i paketet `se.tmconnect.styrelserummet.regler`:

```kotlin
data class PlanAr(val ar: Int, val avskrivning: Long, val bokfortVarde: Long)

fun planForKomponent(k: Komponent, franAr: Int, antalAr: Int): List<PlanAr>
```

En rad per år från `franAr` till `franAr + antalAr - 1`, med `avskrivningFor(k, ar)` och `bokfortVarde(k, ar)`. `antalAr <= 0` ger tom lista. `antalAr` större än 100 kastar `IllegalArgumentException`.

## Exempel
Komponent 300 000 kr, 10 år från 2020, restvärde 0:
- franAr 2028, antalAr 3 → (2028, 30 000, 60 000), (2029, 30 000, 0), (2030, 0, 0)
- franAr 2018, antalAr 2 → (2018, 0, 300 000), (2019, 0, 300 000)
- antalAr 0 → tom lista
- antalAr 101 → IllegalArgumentException

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningsplanTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (JUnit 4 och Komponent i test; bokfört värde 2028 i test 30 000 enligt bokfortVarde)
