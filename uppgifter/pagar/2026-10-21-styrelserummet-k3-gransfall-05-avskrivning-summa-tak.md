# Uppgift: Summan av avskrivningar överstiger aldrig anskaffningsvärdet

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallSummatakTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/regler/K3.md

## Uppgift
För en komponent, summera `avskrivningFor` för varje år från anskaffningsår till sista avskrivningsår (inklusive). Summan ska **exakt** motsvara avskrivningsbart belopp (`anskaffningsvarde - restvarde`), aldrig mer.

Testa minst:
- 1_000_000 kr, period **30** år, restvärde 0 (avrundning ger 33_333 kr/år tills sista året tar resten).
- Ett fall där normalt årsbelopp avrundas uppåt (t.ex. 100_000 kr, period 3).

Varje enskilt årsbelopp ska vara ett `Long` (hela kronor).

Vid fel: rätta högst i `Avskrivning.kt`.

## Exempel
- 1_000_000 kr, anskaffningsår 2000, period 30 → summa år 2000–2029 = 1_000_000 exakt.
- 100_000 kr, period 3 → summa = 100_000.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallSummatakTest"

## Gör inte
- Ändra andra testfiler.
- Lägga till beroenden.
