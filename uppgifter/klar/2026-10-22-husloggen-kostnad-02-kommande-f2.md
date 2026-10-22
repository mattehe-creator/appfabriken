# Uppgift: Uppskattad kostnad för kommande jobb

## Vad som saknades (försök 1)
- Båda tillåtna filerna committades men är **helt tomma** — ingen Kotlin-kod, inga `@Test`.
- Implementera `KommandeKostnad` och `uppskattadKostnadKommande` enligt avsnittet «Uppgift» nedan.
- Lägg minst **två** JUnit 4-tester (`org.junit.Test`, `org.junit.Assert.assertEquals`) som jämför **hela** `KommandeKostnad` mot uppgiftens exempel (290 000 / 3 / 1) och tom map (0, 0, 0). Använd `KommandeJobb` från `Kommande.kt`; namn och datum i jobben spelar ingen roll för kostnadsfunktionen, bara `uppgiftId`.

uppdrag: uppdrag/claude/2026-10-22-husloggen-kostnad.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/KommandeKostnad.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/KommandeKostnadTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/Kommande.kt

## Uppgift
data class KommandeKostnad(val summaOre: Long, val antalMedKostnad: Int, val antalUtanKostnad: Int)

fun uppskattadKostnadKommande(
    kommande: Map<YearMonth, List<KommandeJobb>>,
    senasteKostnad: Map<Long, Long>,
): KommandeKostnad

- Gå igenom alla `KommandeJobb` i alla månader.
- Finns `senasteKostnad[jobb.uppgiftId]`: lägg till beloppet i `summaOre` och räkna upp `antalMedKostnad`.
- Annars: räkna upp `antalUtanKostnad`.

## Exempel
`senasteKostnad = {7: 120 000, 8: 50 000}`.
Kommande: 2026-11 har jobb för uppgift 7 och 9, 2027-05 har jobb för uppgift 7 och 8.
→ `KommandeKostnad(summaOre = 290 000, antalMedKostnad = 3, antalUtanKostnad = 1)`

Tom map → `KommandeKostnad(0, 0, 0)`.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*KommandeKostnadTest"

## Gör inte
- Ändra andra filer.

utfall: godkänd 2026-10-22 (hela implementationen — tomma filer vid försök 2)
