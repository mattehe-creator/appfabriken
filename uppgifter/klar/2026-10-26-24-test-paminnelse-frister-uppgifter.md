# Uppgift: Test av påminnelsefrister ur uppgifter (Husloggen)

uppdrag: uppdrag/claude/2026-10-26-testtackning-dag.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/paminnelse/PaminnelseFristerFranUppgifterTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/paminnelse/PaminnelseFrister.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/NastaDatum.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Uppgift.kt
- karna/src/main/kotlin/se/tmconnect/karna/paminnelse/Paminnelse.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) i paketet `se.tmconnect.husloggen.paminnelse` för `paminnelseFristerFranUppgifter(uppgifter: List<Uppgift>, idag: LocalDate): List<PaminnelseFrist>`. Bygg `Uppgift(id = ..., namn = ..., intervallManader = ..., startDatum = ..., senastGjord = ..., paminnelse = ...)` direkt; övriga fält har standardvärden. Använd idag = LocalDate.of(2026, 10, 26). Frister skapas bara för uppgifter med `paminnelse = true`. Nyckeln är "<id>:<nästa datum>" och slutdatum är nästa datum (`nastaDatum`).

## Exempel
- Tom lista ger tom lista.
- id 7, startDatum 2026-03-01, senastGjord null, intervallManader 12, paminnelse true ger nyckel "7:2026-03-01" och slutdatum 2026-03-01.
- id 8, startDatum 2025-01-01, senastGjord 2026-01-31, intervallManader 1 ger nyckel "8:2026-02-28" och slutdatum 2026-02-28.
- id 9 med paminnelse false ger ingen frist.
- Lista med id 7 och 9 ger exakt en frist (den för 7).

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*PaminnelseFristerFranUppgifterTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-26 av spärren (bara tester, grönt bygge)
