# Uppgift: beloppstolkning till öre

uppdrag: uppdrag/2026-10-16-husloggen-loggen.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BeloppTolkning.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/BeloppTolkningTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FormularValidering.kt (`tolkaPrisTillOre`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoTolkare.kt (`tolkaBeloppTillOre`, decimaler)

## Uppgift
Implementera:

```kotlin
fun tolkaBeloppTillOre(text: String): Long?
```

- Tom eller blank sträng → `null`.
- Ogiltigt tal, negativt värde, fler än två decimaler, eller belopp utanför **0 till 99 999 999 kronor** (räknat före öre) → `null`.
- Godkänn komma och punkt som decimaltecken (högst två decimaler). Mellanslag får tas bort.
- Returnera belopp i **öre** som `Long`.

## Exempel
- `"125"` → `12500`
- `"125,50"` → `12550`
- `"125.5"` → `12550`
- `""` → `null`
- `"12,345"` → `null` (tre decimaler)
- `"abc"` → `null`
- `"-5"` → `null`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*BeloppTolkningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
