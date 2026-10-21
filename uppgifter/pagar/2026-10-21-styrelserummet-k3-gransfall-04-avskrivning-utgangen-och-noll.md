# Uppgift: Fullt avskriven komponent och anskaffningsvärde 0

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallUtgangenTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt

## Uppgift
1. **Utgången period:** anskaffningsår + nyttjandeperiod ≤ räkenskapsår. `avskrivningFor` för räkenskapsåret ska vara 0 kr (ingen ytterligare avskrivning).
2. **Anskaffningsvärde 0:** alla år ska ge 0 kr avskrivning.

Vid fel: rätta högst i `Avskrivning.kt`.

## Exempel
- 100_000 kr, anskaffningsår 2010, period 10, `ar` = 2025 → 0 kr (period slut 2019).
- 0 kr anskaffningsvärde, period 20, valfritt år → 0 kr.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallUtgangenTest"

## Gör inte
- Ändra andra filer än test + ev. `Avskrivning.kt`.
- Lägga till beroenden.
