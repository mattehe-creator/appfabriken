# Uppgift: Summering med stora belopp och total konsistens

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringGransfallStoraBeloppTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt

## Uppgift
1. **Stora belopp:** minst två komponenter vars anskaffningsvärden summeras till ett stort `Long` (t.ex. 900_000_000_000L + 900_000_000_000L). Assert att `summeraPerGrupp` returnerar korrekt anskaffningssumma utan fel (om overflow upptäcks ska `Summering.kt` rättas).
2. **Total = summa av grupper:** för en blandad lista, summera `GruppSumma.anskaffning`, `avskrivningAret` och `bokfort` över alla grupper och jämför med motsvarande summa över alla komponenter (samma arsslut).

Vid fel: rätta högst i `Summering.kt`.

## Exempel
- Två STOMME-komponenter à 900_000_000_000L → en grupp med anskaffning 1_800_000_000_000L.
- Tre grupper med små exakta tal → gruppsummor adderas till samma tal som direkt `sumOf` på komponentlistan.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SummeringGransfallStoraBeloppTest"

## Gör inte
- Dela upp i flera testklasser (denna uppgift äger filen ovan).
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #361). Ersatt av 2026-10-21-styrelserummet-k3-gransfall-10-summering-stora-total-f2.md
