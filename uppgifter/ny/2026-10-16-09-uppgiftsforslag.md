# Uppgift: färdiga förslag på uppgifter

uppdrag: uppdrag/2026-10-16-husloggen-uppgifter.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftsForslag.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftsForslagTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`.

1. `enum class IntervallBestamdAv { INGEN, KOMMUNEN, TILLVERKAREN }`
2. `data class UppgiftsForslag(val id: String, @androidx.annotation.StringRes val namnResId: Int, val intervallManader: Int, val bestamdAv: IntervallBestamdAv)`
3. `val UPPGIFTSFORSLAG: List<UppgiftsForslag>` med exakt åtta poster:

| id | namnResId (läggs i stränguppgiften) | intervall | bestamdAv |
|----|-------------------------------------|-----------|-----------|
| `rensa_hangrannor` | `R.string.forslag_rensa_hangrannor` | 12 | INGEN |
| `kontrollera_brandvarnare` | `R.string.forslag_kontrollera_brandvarnare` | 12 | INGEN |
| `byt_filter_ventilation` | `R.string.forslag_byt_filter_ventilation` | 6 | INGEN |
| `rensa_golvbrunn` | `R.string.forslag_rensa_golvbrunn` | 12 | INGEN |
| `kontrollera_tak` | `R.string.forslag_kontrollera_tak` | 12 | INGEN |
| `sotning` | `R.string.forslag_sotning` | 12 | KOMMUNEN |
| `service_varmepump` | `R.string.forslag_service_varmepump` | 12 | TILLVERKAREN |
| `service_ventilation` | `R.string.forslag_service_ventilation` | 24 | TILLVERKAREN |

Importera `se.tmconnect.husloggen.R`.

## Exempel
- `UPPGIFTSFORSLAG.size == 8`
- Alla `intervallManader` är 1–120
- `sotning` har `bestamdAv == KOMMUNEN`
- `service_varmepump` och `service_ventilation` har `TILLVERKAREN`
- Alla `id` är unika

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*UppgiftsForslagTest"

## Gör inte
- Ändra andra filer (strängar ska finnas från uppgift 08).
- Lägga till beroenden.
