# Uppgift: Kostnad för förbrukning

uppdrag: uppdrag/claude/2026-10-22-husloggen-matarkostnad.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareKostnad.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/MatareKostnadTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForbrukningPrognos.kt

## Uppgift
```kotlin
fun forbrukningKostnadOre(forbrukningTusendel: Long, prisOrePerEnhet: Long): Long?
```

- Förbrukningen är i tusendelar av enheten (1 kWh = 1 000). Priset är i öre per hel enhet.
- Kostnad = förbrukning × pris / 1 000, avrundat till närmaste öre, halva uppåt: `(forbrukningTusendel * prisOrePerEnhet + 500) / 1000`.
- Negativ förbrukning eller negativt pris → `null`.

## Exempel
- 1 000 000 (1 000 kWh), pris 150 öre → 150 000
- 2 500 (2,5 kWh), pris 133 öre → 333 (332,5 avrundas uppåt)
- 1 (0,001 kWh), pris 100 öre → 0
- 0, pris 200 → 0
- −1 000, pris 100 → null
- 1 000, pris −1 → null

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*MatareKostnadTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.

utfall: underkänd 2026-10-22 av Cursor (PR #548). Ersatt av 2026-10-22-husloggen-matarkostnad-01-kostnad-f2.md
