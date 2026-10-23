# Uppgift: Genväg stämpla — regler

uppdrag: uppdrag/2026-10-23-flextid-genvag-stampla.md
försök: 1
beror-på: ingen

**OBS:** Reglerna och enhetstesterna implementeras på grenen `cursor/flextid-appgenv-gar-f731` (Cursor). Körskriptet kan hoppa över denna uppgift när den PR:en är mergad (`cursor:2026-10-23-flextid-genvag-stampla`).

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/GenvagStampla.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/GenvagStamplaTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/GratisGrans.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/data/Dag.kt

## Uppgift
Ren Kotlin i `regler/` för app-genvägarna (ingen Android-kod).

1. `fun avrundaTillHelMinut(tid: LocalTime): LocalTime` — nollställ sekunder och nanosekunder.
2. `sealed interface PlaneradStamplaIn` med `Spara(dag)`, `Oforandrad(start)` och `GransUtanPro`.
3. `fun planeraStamplaIn(befintlig: Dag?, datum: LocalDate, nu: LocalTime, standardRastMin: Int, antalDagar: Int, arPro: Boolean): PlaneradStamplaIn`
   - Om `befintlig?.start != null` → `Oforandrad` (ändra inte).
   - Om ny dag och `!kanLaggaTillDagPost(...)` → `GransUtanPro`.
   - Annars ny/uppdaterad `Dag` med `start = avrundaTillHelMinut(nu)`, standardrast och typ `ARBETE` om ny.
4. `sealed interface PlaneradStamplaUt` med `Spara(dag)`, `IngenInstampling`, `Oforandrad(slut)` och `OgiltigTid(fel)`.
5. `fun planeraStamplaUt(befintlig: Dag?, nu: LocalTime): PlaneradStamplaUt`
   - Ingen start → `IngenInstampling`.
   - Redan slut → `Oforandrad`.
   - Annars `slut = avrundaTillHelMinut(nu)` och `valideraTider`; vid fel `OgiltigTid`.

## Exempel
- Indata: ingen dag, nu 08:47:59, 0 poster, inte Pro. Förväntat: `Spara` med start 08:47.
- Indata: dag med start 07:55, nu 09:00. Förväntat: `Oforandrad(07:55)`.
- Indata: 15 poster, ny dag, inte Pro. Förväntat: `GransUtanPro`.
- Indata: start 08:00, inget slut, nu 16:05:12. Förväntat: `Spara` med slut 16:05.
- Indata: ingen dag, stämpla ut. Förväntat: `IngenInstampling`.
- Indata: start 17:00, nu 08:00. Förväntat: `OgiltigTid(SlutForeStart)`.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*GenvagStamplaTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
