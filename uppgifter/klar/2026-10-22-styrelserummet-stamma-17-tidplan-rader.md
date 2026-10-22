# Uppgift: tidplanRader

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: 2026-10-22-styrelserummet-stamma-16-veckodag-text-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaUtskrift.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaTidplanRaderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till:

```kotlin
data class StammaTabellRad(val etikett: String, val datum: String, val anmarkning: String, val kalla: String)

fun tidplanRader(plan: Stammotidplan, kallelseVeckor: Int): List<StammaTabellRad>
```

Returnera **sju rader** i denna ordning:

1. Senaste dag för ordinarie stämma — `plan.senasteStammodag` — anmärkning tom om `plan.stammaInomLagligFrist` annars kort text att stämman ligger efter lagfrist — källa `EFL 6 kap. 9 §`
2. Planerat stämmodatum — `plan.stammodatum` — tom eller "Efter lagens senaste datum" — tom källa
3. Tidigaste kallelsedag — `plan.tidigasteKallelsedag` — "Ordinarie stämma" — `EFL 6 kap. 17 §`
4. Senaste kallelsedag — `plan.senasteKallelsedag` — text med antal veckor från `kallelseVeckor` — `EFL 6 kap. 17 §`
5. Revisionsberättelse till styrelsen senast — `plan.senasteRevisionTillStyrelsen` — tom — `EFL 8 kap. 32 §`
6. Årsredovisning tillgänglig från — `plan.forstaDagArsredovisningTillganglig` — "Minst två veckor före stämman" — `EFL 6 kap. 23 §`
7. Räkenskapsårets slut — `plan.arsslut` — tom — tom

Alla datum via `datumMedVeckodag`.

## Exempel
- plan från STAMMA-exempel (arsslut 2025-12-31, stamma 2026-05-15, inom frist), kallelseVeckor 4 → rad 4 datum `2026-04-17 (fredag)`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammaTidplanRaderTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-22 av Cursor (PR #585). Ersatt av 2026-10-22-styrelserummet-stamma-17-tidplan-rader-f2.md
