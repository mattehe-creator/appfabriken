# Uppgift: Avskrivning året efter sista avskrivningsåret

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallEfterPeriodTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt

## Uppgift
Testa gränsen **dagen efter sista avskrivningsåret**: sista året är `anskaffningsar + nyttjandeperiodAr - 1`. Året därefter ska ge 0 kr.

Inkludera både en lång period (40 år) och en kort (3 år). Verifiera att sista året fortfarande kan ge ett belopp > 0 medan nästa år ger 0.

Vid fel: rätta högst i `Avskrivning.kt`.

## Exempel
- 1_200_000 kr, anskaffningsår 2010, period 40 → `avskrivningFor(..., 2049)` > 0, `avskrivningFor(..., 2050)` = 0.
- 100_000 kr, anskaffningsår 2020, period 3 → `avskrivningFor(..., 2022)` = 33_334, `avskrivningFor(..., 2023)` = 0.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallEfterPeriodTest"

## Gör inte
- Ändra befintliga testfiler (`AvskrivningTest.kt` m.fl.).
- Lägga till beroenden.
