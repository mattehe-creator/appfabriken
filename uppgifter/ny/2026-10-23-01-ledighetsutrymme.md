# Uppgift: Ledighetsutrymme

uppdrag: uppdrag/2026-10-23-flextid-ledighetskalkyl.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Ledighetsutrymme.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/LedighetsutrymmeTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/SaldoGrans.kt

## Uppgift
Antal hela flexdagar som kan tas ut utan att saldot hamnar under golvet.

```kotlin
fun ledighetsutrymme(saldoMin: Int, golvMin: Int?, normaltidMin: Int): Int?
```

- `golvMin == null` → `null` (ingen golvgräns).
- `normaltidMin <= 0` → `null`.
- Annars: om `saldoMin <= golvMin` → `0`, annars `(saldoMin - golvMin) / normaltidMin` (heltalsdivision).

## Exempel
- (1200, 0, 480) → 2
- (479, 0, 480) → 0
- (300, -480, 480) → 1
- (500, null, 480) → null
- (1200, 0, 0) → null

## Test som ska gå igenom
- Gränsfall: saldo exakt på golvet, t.ex. (480, 480, 480) → 0

gradle :flextid:testDebugUnitTest --tests "*LedighetsutrymmeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
