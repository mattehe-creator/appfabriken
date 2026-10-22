# Uppgift: Förbrukning jämfört med året innan

## Vad som saknades (försök 1)
- `ForbrukningJamforelse.kt` och `ForbrukningJamforelseTest.kt` committades som helt tomma filer (commit-meddelandet nämnde okommittade Aider-ändringar). Det finns ingen kod och inga tester.
- Filerna ska innehålla `ArsJamforelse`, `jamforArsforbrukning` enligt specifikationen nedan och JUnit 4-tester (`org.junit`) som jämför resultat mot varje exempel.

uppdrag: uppdrag/claude/2026-10-22-husloggen-sok-forbrukning.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForbrukningJamforelse.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/ForbrukningJamforelseTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareForbrukning.kt

## Uppgift
data class ArsJamforelse(val ar: Int, val forbrukning: Long, val forandringTiondelsProcent: Long?)

fun jamforArsforbrukning(perAr: Map<Int, Long>): List<ArsJamforelse>

- `perAr` kommer från `beraknaArsforbrukning` (år → förbrukning i tusendelar av enheten).
- Resultatet är sorterat på år, nyaste först.
- `forandringTiondelsProcent` jämför med föregående kalenderår (`ar - 1`) om det finns i kartan och är större än 0: `(nu - foreg) * 1000 / foreg` avrundat till närmaste, halva bort från noll, utan flyttal. Annars `null`.

Avrundning utan flyttal: beräkna `(nu - foreg) * 1000` och dividera heltalsvis med `foreg` så att 0,5-steg hamnar bort från noll (samma mönster som i andra `regler`-filer med procent i tusendelar).

## Exempel
- {2024: 12 000 000, 2025: 13 200 000, 2026: 12 540 000} → [(2026, 12 540 000, −50), (2025, 13 200 000, 100), (2024, 12 000 000, null)]
- {2023: 1000, 2025: 1500} → [(2025, 1500, null), (2023, 1000, null)]
- {2025: 0, 2026: 100} → [(2026, 100, null), (2025, 0, null)]
- tom karta → tom lista

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*ForbrukningJamforelseTest"

Använd `org.junit.Test` och `org.junit.Assert.assertEquals` (se `MatareForbrukningTest.kt`). Ett test per exempel ovan; jämför hela listan med `ArsJamforelse`-rader (år, förbrukning, förändring).

## Gör inte
- Ändra andra filer.
- Använda flyttal.
- Lägga till beroenden.
