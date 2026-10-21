# Uppgift: Bokfört värde med restvärde

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallRestvardeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/regler/K3.md (restvärde enligt punkt 17.14–17.15)

## Uppgift
Testa komponenter med **restvärde > 0**. Efter full avskrivning ska bokfört värde ligga kvar på **restvärdet**, inte under.

Kontrollera också mitt i perioden: bokfört = anskaffningsvärde minus ackumulerad avskrivning, men minst restvärde.

Vid fel: rätta högst i `BokfortVarde.kt`.

## Exempel
- 1_000_000 kr, anskaffningsår 2010, period 10, restvärde 100_000 → efter sista avskrivningsår (2019) och vid arsslut 2025: bokfört = 100_000.
- Mitt i period (arsslut 2014): bokfört = 550_000 om avskrivning 90_000/år (1_000_000 − 100_000 over 10 years).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*BokfortVardeGransfallRestvardeTest"

## Gör inte
- Ändra `Komponent.kt` (restvärde finns redan).
- Lägga till beroenden.
