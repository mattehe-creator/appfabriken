# Uppgift: Årsavskrivning

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 1
beror-på: 2026-10-19-styrelserummet-01-validera-komponent.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/regler/K3.md

## Uppgift
`fun avskrivningFor(k: Komponent, ar: Int): Long`

Linjär avskrivning från `anskaffningsar`, ett helt räkenskapsår per kalenderår. Avskrivningsbart belopp = `anskaffningsvarde - restvarde`. Normal årsbelopp = avskrivningsbart / `nyttjandeperiodAr`, avrundat till närmaste hela krona (`Math.round` eller motsvarande). **Sista avskrivningsåret** (anskaffningsar + nyttjandeperiodAr - 1) tar resten så att total avskrivning exakt blir avskrivningsbart belopp.

Före anskaffningsåret och efter sista avskrivningsåret: returnera 0.

## Exempel
- 1_200_000 kr, anskaffningsår 2010, period 40 år, restvärde 0 → 30_000 kr varje år 2010–2049.
- 100_000 kr, år 2020, period 3, restvärde 0 → 33_333 (2020), 33_333 (2021), 33_334 (2022).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: underkänd 2026-10-19 av Cursor (PR #282). Ersatt av 2026-10-19-styrelserummet-02-arsavskrivning-f2.md
