# Uppgift: test för Konverterare (Room-typomvandlare)

uppdrag: uppdrag/claude/2026-10-26-testtackning-dag.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/data/KonverterareTest.kt (ny)

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/data/Konverterare.kt
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/regler/ForslagTyp.kt
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/regler (valfri befintlig testfil, för stil och JUnit-version)

## Uppgift
Skriv enhetstester för klassen `Konverterare` (paket `se.tmconnect.rostfangst.data`) med funktionerna `franTyp(String?): ForslagTyp?` och `tillTyp(ForslagTyp?): String?`. Testklassen heter `KonverterareTest` i samma paket. Ändra ingen produktionskod.

## Exempel
- `tillTyp(ForslagTyp.HEM)` ger "HEM".
- `franTyp("ATT_GORA")` ger `ForslagTyp.ATT_GORA`.
- `tillTyp(null)` ger null och `franTyp(null)` ger null.
- För varje värde i `ForslagTyp.entries`: `franTyp(tillTyp(v))` ger `v` (rundresa).
- `franTyp("FINNS_INTE")` kastar `IllegalArgumentException` (anropa med `assertThrows` eller motsvarande i testbiblioteket som redan används).

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*KonverterareTest"

## Gör inte
- Ändra andra filer än testfilen.
- Lägga till beroenden.
- Ändra databasschemat.
