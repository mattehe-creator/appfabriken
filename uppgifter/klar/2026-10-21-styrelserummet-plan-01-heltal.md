# Uppgift: Avskrivning utan flyttal

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningHeltalTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt

## Uppgift
I `avskrivningFor`: byt raderna

```kotlin
val normaltAr = avskrivningsbartBelopp.toDouble() / k.nyttjandeperiodAr
val arsAvskrivning = kotlin.math.round(normaltAr).toLong()
```

mot heltalsräkning med avrundning uppåt vid exakt halv krona:

```kotlin
val n = k.nyttjandeperiodAr.toLong()
val arsAvskrivning = (2 * avskrivningsbartBelopp + n) / (2 * n)
```

Lägg också till överst i funktionen: om `k.nyttjandeperiodAr <= 0` returnera `0L`.

Allt annat i funktionen är oförändrat. Alla befintliga tester i modulen ska fortsätta gå igenom.

Nytt test `AvskrivningHeltalTest` med en hjälpfunktion som skapar en `Komponent` (namn "Tak", grupp TAK, källa "test", säkerhet MEDEL).

## Exempel
- 1 000 000 kr, restvärde 0, 3 år från 2020: 2019 → 0, 2020 → 333 333, 2021 → 333 333, 2022 → 333 334, 2023 → 0
- 100 kr, 3 år från 2020: 33, 33, 34
- 101 kr, 2 år från 2020: 51, 50 (halv krona avrundas uppåt)
- 500 000 kr, restvärde 50 000, 10 år från 2018: 2018 → 45 000, 2027 → 45 000
- 9 000 000 000 000 kr, 7 år från 2020: summan över 2020–2026 är exakt 9 000 000 000 000
- Nyttjandeperiod 0 → 0 för alla år

## Test som ska gå igenom
gradle :styrelserummet:test

## Gör inte
- Ändra andra filer.
- Ändra hur sista året räknas.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (inget)
