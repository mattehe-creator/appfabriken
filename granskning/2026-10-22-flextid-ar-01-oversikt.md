beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #536 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/` och skapade `ArsOversikt.kt` / `ArsOversiktTest.kt` på rätt sökvägar under `flextid/` — inget i repots rot.
- Båda källfilerna är helt tomma (0 byte): ingen `ManadsRad`, ingen `arsOversikt` och inga tester; uppgiften är inte implementerad.
- Testerna kan därför varken jämföra mot exemplen i uppgiften eller köra `ArsOversiktTest`.
- Inga nya beroenden, behörigheter eller nätverk tillagda; ingen UI-kod.
- Bygget på grenen blev grönt eftersom tomma filer kompileras, men beteendet saknas helt.

## Rättat av Cursor
- inget (saknad implementation är inte ett småfel)

## Vid underkänd: försök 2

# Uppgift: Årsöversikt per månad

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt.md
försök: 2
beror-på: ingen

## Vad som saknades
Försök 1 skapade `ArsOversikt.kt` och `ArsOversiktTest.kt` på rätt sökvägar men lämnade båda filerna helt tomma. Ingen `ManadsRad`, ingen `arsOversikt`-funktion och inga JUnit-tester. Implementera enligt avsnitten nedan och verifiera med test mot exemplen.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversikt.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ArsOversiktTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattning.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/PeriodSammanfattningTest.kt

## Uppgift
data class ManadsRad(val manad: Int, val arbetadMin: Int, val utfallMin: Int, val saldoVidSlutMin: Int)

fun arsOversikt(dagar: List<Dag>, ar: Int, installning: Installning): List<ManadsRad>

- Returnera alltid 12 rader med `manad` 1..12 (i ordning).
- För varje månad `m` i `ar`: ta dagar vars `datum.year == ar` och `datum.monthValue == m`.
- `arbetadMin`: summan av `arbetadeMinuter(start, slut, rastMin)` för dagar med typ `ARBETE` i den månaden.
- `utfallMin`: summan av `dagensUtfall(dag, installning.normaltidMinPerDag, installning.arbetsdagarMask)` för samma månad.
- `saldoVidSlutMin`: `installning.ingangssaldoMin` plus summan av `dagensUtfall(...)` för **alla** dagar i `dagar` vars `datum` inte ligger efter månadens sista dag (`LocalDate.of(ar, m, sista dagen i månaden)`), även dagar från tidigare år (t.ex. december föregående år räknas med i januari–decembers saldo för `ar`).

## Exempel
Installning(normaltidMinPerDag = 480, standardRastMin = 30, ingangssaldoMin = 60), övriga fält standard (måndag–fredag).
Dagar:
- 2025-12-01 (måndag) 08:00–17:00, rast 0 → arbetat 540, utfall +60
- 2026-01-05 (måndag) 08:00–17:00, rast 30 → arbetat 510, utfall +30
- 2026-03-02 (måndag) 08:00–16:00, rast 60 → arbetat 420, utfall −60

`arsOversikt(dagar, 2026, installning)`:
- januari: arbetad 510, utfall 30, saldo 150 (ingång 60 + utfall 60 från dec + 30 från jan)
- februari: 0, 0, 150
- mars: 420, −60, 90
- april–december: 0, 0, 90

Inga dagar alls → 12 rader med 0, 0, 60.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ArsOversiktTest"

Skriv minst två `@Test` med `org.junit.Test` och `org.junit.Assert.assertEquals`: ett som bygger daglistan ovan och jämför alla 12 `ManadsRad` (eller nyckelfält för jan/feb/mar/apr) mot exemplen; ett med tom lista → 12× (0, 0, 60). Importera `Dag`, `Installning`, `LocalDate`, `LocalTime` som i `PeriodSammanfattningTest`.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Använda `kotlin.test`.

## Frågor till Claude
- inga
