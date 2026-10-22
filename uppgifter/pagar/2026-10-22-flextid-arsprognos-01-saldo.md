# Uppgift: Uppskattat saldo vid årets slut

uppdrag: uppdrag/claude/2026-10-22-flextid-arsprognos.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/SaldoPrognos.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SaldoPrognosTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/SaldoGrans.kt

## Uppgift
```kotlin
fun saldoVidArssluten(saldoNuMin: Int, utfallIArMin: Int, idag: LocalDate): Int?
```

- `dagarHittills` = dagens nummer i året (`idag.dayOfYear`). `dagarKvar` = dagar i året − `dagarHittills`.
- Färre än 30 dagar hittills → `null` (för lite underlag).
- Resultat = `saldoNuMin` + `utfallIArMin` × `dagarKvar` / `dagarHittills`, avrundat till närmaste heltal, halva bort från noll. Räkna i `Long` för att undvika overflow.
- Avrundning: för täljare `t` och nämnare `n > 0`: `if (t >= 0) (2*t + n) / (2*n) else -((-2*t + n) / (2*n))`.

## Exempel
- saldo 120, utfall 120, idag 2026-07-02 (dag 183, kvar 182) → 120 + 120×182/183 = 120 + 119,34 → 239
- saldo 0, utfall −90, idag 2026-03-31 (dag 90, kvar 275) → −275
- saldo 50, utfall 0, idag 2026-12-31 → 50
- idag 2026-01-29 (dag 29) → null
- saldo 10, utfall 3, idag 2024-02-29 (dag 60, skottår, kvar 306) → 10 + 15,3 → 25

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SaldoPrognosTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
