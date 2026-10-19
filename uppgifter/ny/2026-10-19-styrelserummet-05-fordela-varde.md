# Uppgift: Fördela värde på procent

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 1
beror-på: 2026-10-19-styrelserummet-01-validera-komponent.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FordelaVarde.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FordelaVardeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
`fun fordela(total: Long, andelar: List<Pair<Komponentgrupp, Int>>): Map<Komponentgrupp, Long>`

`andelar` är heltalsprocent som ska summera till exakt 100. Varje grupp får `total * procent / 100` avrundat nedåt (floor). **Öresresten** (total minus summan av fördelade belopp) läggs på gruppen med **störst** fördelat belopp; vid lika stor post, den som kommer först i `andelar`.

Kasta `IllegalArgumentException` med tydligt svenskt meddelande om procenten inte summerar till 100.

Summan av alla värden i mapen ska alltid vara exakt `total`.

## Exempel
- total 1_000_000, STOMME 63 % + FASAD 37 % → 630_000 + 370_000.
- total 100, tre grupper 34+33+33 → 34+33+33=100 (rest 0) eller vid behov rest på största.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FordelaVardeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
