# Uppgift: Fördela ett belopp efter andelstal

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-andelstal.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Andelstal.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AndelstalTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avgiftskalkyl.kt

## Uppgift
```kotlin
fun fordelaEfterAndelstal(totalKronor: Long, andelstal: List<Long>): List<Long>?
```

Fördela `totalKronor` (hela kronor) på lägenheterna i proportion till `andelstal` (valfri skala, till exempel miljondelar). Resultatet har samma ordning som `andelstal` och summan är exakt `totalKronor`.

Metod (största resten):
1. `summa` = summan av andelstalen. Tom lista, `summa == 0`, något negativt andelstal eller `totalKronor < 0` → `null`.
2. För varje lägenhet: `grund = totalKronor * andel / summa` (heltalsdivision), `rest = totalKronor * andel % summa`.
3. Kvar att dela ut = `totalKronor` − summan av grund. Ge 1 krona extra till så många lägenheter, i ordning efter störst `rest`. Lika rest → lägst index först.

## Exempel
- total 1 000, andelstal [1, 1, 1] → [334, 333, 333]
- total 100, andelstal [50, 30, 20] → [50, 30, 20]
- total 10, andelstal [1, 2] → [3, 7] (3,33 och 6,67; resten för den andra är större)
- total 0, andelstal [5, 5] → [0, 0]
- tom lista → null; [0, 0] → null; [1, −1] → null

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AndelstalTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
