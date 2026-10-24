# Uppgift: Test av fördelning av byggnadens värde på komponentgrupper

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FordelaRestTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FordelaVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/ReglerTest.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`, `assertThrows`) för
`fordela(total: Long, andelar: List<Pair<Komponentgrupp, Int>>): Map<Komponentgrupp, Long>`.

Så räknar funktionen:
1. Andelarna (procent) måste summera till 100, annars `IllegalArgumentException`.
2. Om `total` är 0 får alla grupper 0.
3. Varje grupp får `total * procent / 100` med heltalsdivision (decimaler stryks).
4. Det som blir kvar (resten) läggs på gruppen med störst belopp. Har flera grupper samma största belopp får den som står först i listan resten.

Importera `se.tmconnect.styrelserummet.modell.Komponentgrupp`. Jämför hela kartan, till exempel
`assertEquals(mapOf(Komponentgrupp.STOMME to 51L, Komponentgrupp.FASAD to 50L), fordela(101, listOf(Komponentgrupp.STOMME to 50, Komponentgrupp.FASAD to 50)))`.

## Exempel
- `fordela(100, [STOMME 34, FASAD 33, TAK 33])` → `{STOMME=34, FASAD=33, TAK=33}` (ingen rest)
- `fordela(1000, [STOMME 50, FASAD 50])` → `{STOMME=500, FASAD=500}`
- `fordela(101, [STOMME 50, FASAD 50])` → `{STOMME=51, FASAD=50}` (101*50/100 = 50 för båda, resten 1 till den första)
- `fordela(10, [STOMME 33, FASAD 33, TAK 34])` → `{STOMME=4, FASAD=3, TAK=3}` (3 + 3 + 3 = 9, alla lika stora, resten 1 till den första)
- `fordela(999, [STOMME 63, FASAD 37])` → `{STOMME=630, FASAD=369}` (629 + 369 = 998, resten 1 till STOMME som är störst)
- `fordela(999, [FASAD 37, STOMME 63])` → `{FASAD=369, STOMME=630}` (störst belopp vinner även när den står sist)
- `fordela(7, [STOMME 100, MARK_OCH_GARAGE 0])` → `{STOMME=7, MARK_OCH_GARAGE=0}`
- `fordela(0, [STOMME 60, TAK 40])` → `{STOMME=0, TAK=0}`
- `fordela(100, [STOMME 50, TAK 40])` → kastar `IllegalArgumentException` (summan är 90)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FordelaRestTest"

## Gör inte
- Ändra andra filer.
- Ändra `FordelaVarde.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Lägga till beroenden.
