beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #361 innehåller enbart flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/` (commit «Uppgift startad»); ingen kod eller test.
- Filen `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringGransfallStoraBeloppTest.kt` saknas — uppgiftens enda tillåtna ändring är inte gjord.
- Uppgiftens beteende (stora belopp, total = summa av grupper) är inte testat; `gradle :styrelserummet:test --tests "*SummeringGransfallStoraBeloppTest"` hittar ingen testklass.
- Inga otillåtna filer, beroenden, behörigheter eller nätverk (ingen appkod ändrad).
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Summering med stora belopp och total konsistens

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehåller bara «Uppgift startad» (flytt till `uppgifter/pagar/`). Testfilen `SummeringGransfallStoraBeloppTest.kt` skapades aldrig.
- Uppgiftens exempel och testkommando är därmed oimplementerade.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringGransfallStoraBeloppTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt

## Uppgift
Skapa testklassen `SummeringGransfallStoraBeloppTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`).

Minst två `@Test`-metoder:

1. **Stora belopp:** minst två `Komponent` i samma grupp (`Komponentgrupp.STOMME`) med anskaffningsvärde `900_000_000_000L` vardera. Anropa `summeraPerGrupp(komponenter, arsslut)` och asserta att exakt **en** grupp returneras med `anskaffning == 1_800_000_000_000L` (inga overflow/teckenfel). Välj `arsslut`, anskaffningsår och nyttjandeperiod så att avskrivning fortfarande är meningsfull (t.ex. samma mönster som `SummeringTest.kt` för hjälpfunktionen `komponent(...)`).

2. **Total = summa av grupper:** bygg en lista med komponenter i **minst tre** olika `Komponentgrupp` med små exakta heltal (Long). Ett gemensamt `arsslut`. Beräkna `val grupper = summeraPerGrupp(komponenter, arsslut)` och jämför:
   - `grupper.sumOf { it.anskaffning }` mot `komponenter.sumOf { it.anskaffningsvarde }`
   - `grupper.sumOf { it.avskrivningAret }` mot `komponenter.sumOf { avskrivningFor(it, arsslut) }`
   - `grupper.sumOf { it.bokfort }` mot `komponenter.sumOf { bokfortVarde(it, arsslut) }`  
   Alla tre ska vara exakt lika (`assertEquals`).

Återanvänd privat `komponent(...)` som i `SummeringTest.kt` (`Komponent`, `Sakerhet`, `Komponentgrupp`).

Om ett test visar fel i summeringslogiken (t.ex. overflow) får du **endast** lägga till `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt` under «Ändra bara dessa filer» och hålla total diff inom cirka 150 rader.

## Exempel
- Två STOMME à 900_000_000_000L → en grupp med anskaffning 1_800_000_000_000L.
- Tre grupper med små exakta tal → gruppsummor adderas till samma tal som direkt `sumOf` på komponentlistan (anskaffning, avskrivning året, bokfört).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SummeringGransfallStoraBeloppTest"

(Cursor kör `:styrelserummet:testDebugUnitTest` vid granskning.)

## Gör inte
- Dela upp i flera testklasser (denna uppgift äger filen ovan).
- Ändra `SummeringTest.kt` eller andra filer (utom `Summering.kt` enligt regeln ovan vid visad bugg).
- Lägga till beroenden.

## Frågor till Claude
- inga
