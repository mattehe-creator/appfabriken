# Uppgift: tidplanRader

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-16-veckodag-text-f2.md

## Vad som saknades
- PR #585 har bara commit «Uppgift startad» (flytt till `pagar/`). Varken `StammaUtskrift.kt` utökades med `StammaTabellRad`/`tidplanRader` eller testfilen `StammaTidplanRaderTest.kt` skapades.
- Implementera enligt uppgiften nedan. Använd befintlig `datumMedVeckodag` för alla datumfält. Skriv JUnit 4-tester (`org.junit`, inte `kotlin.test`) som assertar mot exemplen.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaUtskrift.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaTidplanRaderTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Stammotidplan.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammotidplanTest.kt (bygg `plan` med `tidplan(...)`)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaVeckodagTest.kt (JUnit 4-stil)

## Uppgift
Lägg till i `StammaUtskrift.kt`:

data class StammaTabellRad(val etikett: String, val datum: String, val anmarkning: String, val kalla: String)

fun tidplanRader(plan: Stammotidplan, kallelseVeckor: Int): List<StammaTabellRad>

Returnera **sju rader** i denna ordning:

1. Senaste dag för ordinarie stämma — `datumMedVeckodag(plan.senasteStammodag)` — anmärkning tom om `plan.stammaInomLagligFrist` annars kort text att stämman ligger efter lagfrist — källa `EFL 6 kap. 9 §`
2. Planerat stämmodatum — `datumMedVeckodag(plan.stammodatum)` — tom anmärkning om `plan.stammaInomLagligFrist`, annars `"Efter lagens senaste datum"` — tom källa
3. Tidigaste kallelsedag — `datumMedVeckodag(plan.tidigasteKallelsedag)` — `"Ordinarie stämma"` — `EFL 6 kap. 17 §`
4. Senaste kallelsedag — `datumMedVeckodag(plan.senasteKallelsedag)` — anmärkning med antal veckor från parametern `kallelseVeckor` (t.ex. att kallelsen ska utfärdas senast så många veckor före stämman) — `EFL 6 kap. 17 §`
5. Revisionsberättelse till styrelsen senast — `datumMedVeckodag(plan.senasteRevisionTillStyrelsen)` — tom — `EFL 8 kap. 32 §`
6. Årsredovisning tillgänglig från — `datumMedVeckodag(plan.forstaDagArsredovisningTillganglig)` — `"Minst två veckor före stämman"` — `EFL 6 kap. 23 §`
7. Räkenskapsårets slut — `datumMedVeckodag(plan.arsslut)` — tom — tom källa

Etiketterna ovan är exakt text i fältet `etikett` på respektive rad.

## Exempel
- `plan = tidplan(arsslut = 2025-12-31, stammodatum = 2026-05-15, kallelsefristVeckor = 4..6)`, `kallelseVeckor = 4` → rad 4 (index 3) har `datum == "2026-04-17 (fredag)"`
- Samma plan → rad 1 anmärkning tom, rad 2 anmärkning tom (stämma inom frist)
- Plan med stämma efter lagfrist (t.ex. `StammotidplanTest` tredje fallet) → rad 1 och 2 får icke-tom anmärkning enligt reglerna ovan

## Test som ska gå igenom
Skriv `StammaTidplanRaderTest` med JUnit 4. Asserta minst datum på rad 4 enligt första exemplet och att listan har längd 7.

Kör:
`gradle :styrelserummet:testDebugUnitTest --tests "*StammaTidplanRaderTest"`

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-22 (hela implementationen och testerna — lokal modell levererade ingen kod)
