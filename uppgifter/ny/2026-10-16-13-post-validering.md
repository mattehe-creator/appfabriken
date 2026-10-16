# Uppgift: validering av postformulär

uppdrag: uppdrag/2026-10-16-husloggen-loggen.md
försök: 1
beror-på: 2026-10-16-12-belopp-tolkning.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/PostValidering.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/PostValideringTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftValidering.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BeloppTolkning.kt (`tolkaBeloppTillOre`)

## Uppgift
```kotlin
enum class PostFel {
    VAD_SAKNAS,
    DATUM_I_FRAMTIDEN,
    BELOPP_OGILTIGT,
}

fun valideraPost(
    vad: String,
    datum: LocalDate,
    idag: LocalDate,
    beloppText: String,
): PostFel?
```

Kontrollera i ordning **vad → datum → belopp**; returnera första felet eller `null` om allt är giltigt.

- `VAD_SAKNAS`: `vad` är tomt eller blankt.
- `DATUM_I_FRAMTIDEN`: `datum` är efter `idag`.
- `BELOPP_OGILTIGT`: `beloppText` är inte tom/blank och `tolkaBeloppTillOre(beloppText)` ger `null`.
- Tomt belopp (`""` eller blankt) är giltigt.

## Exempel
- `("", idag, "")` → `VAD_SAKNAS`
- `("Målade fasaden", idag.plusDays(1), "")` → `DATUM_I_FRAMTIDEN`
- `("Målade fasaden", idag, "abc")` → `BELOPP_OGILTIGT`
- `("Målade fasaden", idag, "-5")` → `BELOPP_OGILTIGT`
- `("Målade fasaden", idag, "")` → `null`
- `("Målade fasaden", idag, "12500")` → `null` (tolka som kronor via `tolkaBeloppTillOre`)

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*PostValideringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
