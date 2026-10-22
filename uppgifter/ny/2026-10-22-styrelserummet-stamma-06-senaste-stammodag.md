# Uppgift: senaste dag för ordinarie stämma

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Stammotidplan.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammotidplanSenasteTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Skapa `Stammotidplan.kt` med:

```kotlin
fun senasteStammodag(arsslut: LocalDate): LocalDate
```

Sex kalendermånader efter räkenskapsårets slut (`arsslut.plusMonths(6)`).

## Exempel
- arsslut 2025-12-31 → 2026-06-30
- arsslut 2024-08-31 → 2025-02-28
- arsslut 2023-08-31 → 2024-02-29 (skottår)
- arsslut 2025-06-30 → 2025-12-30

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammotidplanSenasteTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
