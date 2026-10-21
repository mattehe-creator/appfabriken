# Uppgift: Avskrivning året efter sista avskrivningsåret

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). `AvskrivningGransfallEfterPeriodTest.kt` skapades inte.
- Ingen JUnit 4-testklass enligt exemplen nedan; kommandot `gradle :styrelserummet:test --tests "*AvskrivningGransfallEfterPeriodTest"` hittar inga tester.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallEfterPeriodTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt

## Uppgift
Skapa testklassen `AvskrivningGransfallEfterPeriodTest` i paketet `se.tmconnect.styrelserummet.regler`.

Testa gränsen **dagen efter sista avskrivningsåret**: sista året är `anskaffningsar + nyttjandeperiodAr - 1`. Året därefter ska ge 0 kr.

Inkludera både en lång period (40 år) och en kort (3 år). Verifiera att sista året fortfarande kan ge ett belopp > 0 medan nästa år ger 0.

Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert` — **inte** `kotlin.test`). Bygg `Komponent`-instanser som i `AvskrivningTest.kt` (samma fält: värde, anskaffningsår, period, restvarde 0).

Vid fel i produktionslogiken: rätta högst i `Avskrivning.kt` (förväntas redan korrekt enligt befintliga tester).

## Exempel
- 1_200_000 kr, anskaffningsår 2010, period 40 → `avskrivningFor(..., 2049)` > 0, `avskrivningFor(..., 2050)` = 0.
- 100_000 kr, anskaffningsår 2020, period 3 → `avskrivningFor(..., 2022)` = 33_334, `avskrivningFor(..., 2023)` = 0.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallEfterPeriodTest"

Minst två `@Test`-metoder (t.ex. `langPeriodSistaArOchDagenEfter` och `kortPeriodSistaArOchDagenEfter`) som **assertEquals** mot värdena ovan — inte bara att funktionen körs.

## Gör inte
- Ändra befintliga testfiler (`AvskrivningTest.kt` m.fl.).
- Lägga till beroenden.
- Ändra andra filer än den nya testfilen (och vid behov `Avskrivning.kt`).

utfall: godkänd 2026-10-21 (inget)
