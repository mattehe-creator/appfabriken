beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/`; ingen `AvskrivningGransfallUtgangenTest.kt` och ingen ändring i `Avskrivning.kt`.
- Uppgiftens krav (test för utgången period och anskaffningsvärde 0) är inte implementerade; lokala körningen rapporterade röda tester eftersom testklassen saknas.
- Inga otillåtna filer i repots rot; inga nya beroenden, behörigheter eller nätverk.
- JUnit 4: inga tester tillagda (saknad fil).
- `:styrelserummet:testDebugUnitTest` grönt på grenen (ingen ny kod som kan fallera).

## Rättat av Cursor
- inget (saknad implementation — inte småfel)

## Vid underkänd: försök 2

# Uppgift: Fullt avskriven komponent och anskaffningsvärde 0

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Filen `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallUtgangenTest.kt` skapades inte.
- `gradle :styrelserummet:test --tests "*AvskrivningGransfallUtgangenTest"` kan inte köras utan testklassen; uppgiftens exempel är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallUtgangenTest.kt (ny)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt (endast om testerna avslöjar fel i logiken)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt (hjälpfunktionen `komponent(...)` och JUnit 4-mönster)

## Uppgift
Skapa testklassen `AvskrivningGransfallUtgangenTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`).

1. **Utgången period:** när `ar > anskaffningsar + nyttjandeperiodAr - 1` (ekvivalent: anskaffningsår + nyttjandeperiod ≤ räkenskapsår i uppgiftens formulering) ska `avskrivningFor(k, ar)` vara **0** kr.
2. **Anskaffningsvärde 0:** med `anskaffningsvarde = 0` (restvarde 0 om inte annat behövs) ska `avskrivningFor` ge **0** för minst två olika räkenskapsår inom och utanför nyttjandeperioden.

Vid fel i produktionskoden: rätta **högst** i `Avskrivning.kt`.

## Exempel (ska finnas som assert i tester)
- 100_000 kr, anskaffningsår 2010, nyttjandeperiod 10, `ar` = 2025 → `avskrivningFor` = **0** (sista avskrivningsår 2019).
- 0 kr anskaffningsvärde, nyttjandeperiod 20, t.ex. `ar` = 2020 och `ar` = 2045 → **0** kr vardera.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallUtgangenTest"

## Gör inte
- Ändra andra filer (inklusive befintliga testfiler som `AvskrivningTest.kt`).
- Lägga till beroenden.
- Lägga filer i repots rot.

## Frågor till Claude
- inga
