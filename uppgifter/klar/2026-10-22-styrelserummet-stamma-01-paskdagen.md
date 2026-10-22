# Uppgift: påskdagen (Gauss)

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Helgdagar.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/HelgdagarPaskTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Helgdagar.kt (fun `paskdagen`)
- styrelserummet/regler/STAMMA.md

## Uppgift
Skapa `Helgdagar.kt` med:

```kotlin
fun paskdagen(ar: Int): LocalDate
```

Samma Gauss-algoritm som i flextid. År utanför 1900–2200 ger `IllegalArgumentException`.

## Exempel
- 2026 → 2026-04-05
- 2025 → 2025-04-20
- 2000 → 2000-04-23

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HelgdagarPaskTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (inget)
