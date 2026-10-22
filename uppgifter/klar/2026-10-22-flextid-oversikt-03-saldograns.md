# Uppgift: Saldo utanför gränserna

uppdrag: uppdrag/claude/2026-10-22-flextid-oversikt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/SaldoGrans.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SaldoGransTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidRegler.kt

## Uppgift
Många arbetsplatser har ett tak för plussaldo och ett golv för minussaldo. Användaren anger dem själv (appen antar inget avtal).

```kotlin
enum class SaldoStatus { INOM, OVER_TAKET, UNDER_GOLVET }

fun saldoStatus(saldoMin: Int, takMin: Int?, golvMin: Int?): SaldoStatus
fun minuterTillGrans(saldoMin: Int, takMin: Int?, golvMin: Int?): Int?
```

- `takMin` är ett positivt tak (till exempel 2400 = +40 timmar), `golvMin` ett negativt golv (till exempel −600). `null` betyder ingen gräns.
- `saldoStatus`: `OVER_TAKET` om `takMin != null && saldoMin > takMin`, `UNDER_GOLVET` om `golvMin != null && saldoMin < golvMin`, annars `INOM`.
- `minuterTillGrans`: hur långt det är kvar till närmaste gräns i den riktning saldot ligger: för `saldoMin >= 0` och tak satt → `takMin - saldoMin` (negativt om över); för `saldoMin < 0` och golv satt → `saldoMin - golvMin` (negativt om under). Annars `null`.

## Exempel
- (2500, 2400, −600) → OVER_TAKET, minuterTillGrans −100
- (1200, 2400, −600) → INOM, 1200
- (−700, 2400, −600) → UNDER_GOLVET, −100
- (−300, null, −600) → INOM, 300
- (500, null, null) → INOM, null
- (−300, 2400, null) → INOM, null

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SaldoGransTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: utförd av Claude 2026-10-22 (datorn användes)
