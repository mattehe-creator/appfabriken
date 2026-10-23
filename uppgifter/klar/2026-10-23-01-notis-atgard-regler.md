# Uppgift: Notisåtgärd, regler för utfall

uppdrag: uppdrag/2026-10-23-flextid-notisatgard.md
försök: 1
beror-på: cursor:2026-10-23-flextid-notisatgard

`cursor:2026-10-23-flextid-notisatgard` betyder att Cursors kod för manifest, BroadcastReceiver och notisbygge ska vara mergad innan denna uppgift körs.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/NotisAtgardRegler.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/NotisAtgardReglerTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/data/FlextidRepository.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/GenvagStampel.kt

## Uppgift

Ren Kotlin i `regler/` som mappar resultat från `FlextidRepository.genvagStamplaIn` respektive `genvagStamplaUt` till vad notisen ska göra efter tryck på åtgärdsknappen.

1. Lägg till `NotisAtgardTextNyckel` (enum): `GENVAG_REDAN_INSTAMPLAD`, `GENVAG_INGEN_INSTEMPLING`, `FEL_SLUT_FORE_START`, `PRO_GRANS_NADD`.
2. Lägg till `sealed interface NotisAtgardUtfall`:
   - `TaBort` — notisen ska tas bort (lyckad stämpling).
   - `VisaText(nyckel: NotisAtgardTextNyckel, tidForFormat: LocalTime? = null)` — text i notisen ersätts; `tidForFormat` sätts bara för `GENVAG_REDAN_INSTAMPLAD`.
3. Funktioner:
   - `fun notisAtgardEfterStamplaIn(resultat: FlextidRepository.GenvagStamplaInResult): NotisAtgardUtfall`
   - `fun notisAtgardEfterStamplaUt(resultat: FlextidRepository.GenvagStamplaUtResult): NotisAtgardUtfall`

Mappning enligt uppdraget (samma strängnycklar som genvägarna använder i UI, men här bara enum):

| Repository-resultat | Utfall |
|---------------------|--------|
| `GenvagStamplaInResult.Sparat` | `TaBort` |
| `GenvagStamplaInResult.RedanInstamplad(start)` | `VisaText(GENVAG_REDAN_INSTAMPLAD, start)` |
| `GenvagStamplaInResult.GransUtanPro` | `VisaText(PRO_GRANS_NADD)` |
| `GenvagStamplaUtResult.Sparat` | `TaBort` |
| `GenvagStamplaUtResult.IngenInstempling` | `VisaText(GENVAG_INGEN_INSTEMPLING)` |
| `GenvagStamplaUtResult.SlutForeStart` | `VisaText(FEL_SLUT_FORE_START)` |
| `GenvagStamplaUtResult.GransUtanPro` | `VisaText(PRO_GRANS_NADD)` |

## Exempel
- Indata: `GenvagStamplaInResult.Sparat` → Förväntat: `NotisAtgardUtfall.TaBort`
- Indata: `GenvagStamplaInResult.RedanInstamplad(LocalTime.of(8, 15))` → Förväntat: `VisaText(GENVAG_REDAN_INSTAMPLAD, LocalTime.of(8, 15))`
- Indata: `GenvagStamplaUtResult.IngenInstempling` → Förväntat: `VisaText(GENVAG_INGEN_INSTEMPLING)`
- Indata: `GenvagStamplaUtResult.SlutForeStart` → Förväntat: `VisaText(FEL_SLUT_FORE_START)`

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*NotisAtgardReglerTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Android- eller Context-kod i `regler/`.

utfall: godkänd av Cursor i samma PR (regler behövdes för receiver)
