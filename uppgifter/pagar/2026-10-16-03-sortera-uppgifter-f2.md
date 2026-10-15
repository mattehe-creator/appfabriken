# Uppgift: sortera uppgifter

## Vad som saknades (försök 1)
- Båda filerna committades tomma. Gradle/CI kan rapportera success utan att något `@Test` körs — skriv riktig kod och tester.
- Implementera `sorteraUppgifter` enligt signaturen nedan. Anropa `nastaDatum(senastGjord, startDatum, intervallManader)` från `NastaDatum.kt` i samma paket (finns på `main` om den saknas på din gren: hämta senaste `main` före kod).
- `UppgiftSorteringTest` ska ha minst ett test för huvuexemplet (A → C → B med `idag = 2026-10-16`) och minst ett för namn vid samma nästa datum (`"a"` före `"B"` case-insensitive). Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`) och jämför hela listordningen (t.ex. på `namn` eller `List<Uppgift>`), inte bara listlängd.
- Förebild för stil: `KopSortering.kt` / `KopSorteringTest.kt` i garantivalvet.

uppdrag: uppdrag/2026-10-16-husloggen-grund.md
försök: 2
beror-på: cursor:2026-10-16-husloggen-grund

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftSortering.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/UppgiftSorteringTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Uppgift.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopSortering.kt

## Uppgift
Ren Kotlin i `se.tmconnect.husloggen.regler`.

fun sorteraUppgifter(uppgifter: List<Uppgift>, idag: LocalDate): List<Uppgift>

Sortera efter **nästa datum** (stigande, beräknat med `nastaDatum` för varje uppgift), sedan `namn` utan hänsyn till stora och små bokstäver (`String.CASE_INSENSITIVE_ORDER`).

Tips: `uppgifter.sortedWith(compareBy<Uppgift> { nastaDatum(it.senastGjord, it.startDatum, it.intervallManader) }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.namn })`. Parametern `idag` ska finnas kvar i signaturen även om sorteringsnyckeln bara bygger på `nastaDatum`.

## Exempel
Med `idag = 2026-10-16`:
- Uppgift A: namn "Tak", start 2026-01-01, intervall 12, senast gjord null → nästa 2026-01-01 (försenad).
- Uppgift B: namn "Fönster", start 2026-06-01, intervall 6, senast gjord 2026-06-01 → nästa 2026-12-01.
- Uppgift C: namn "altan", start 2026-11-01, intervall 12, senast gjord null → nästa 2026-11-01.
- Ordning: A (2026-01-01), C (2026-11-01), B (2026-12-01).
- Två uppgifter med samma nästa datum sorteras på namn: "Gärdes" före "gärdes" räknas lika; "B" före "a" med case-insensitive → "a" före "B".

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*UppgiftSorteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
