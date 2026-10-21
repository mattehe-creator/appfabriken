# Uppgift: Tolka dubbla komponenter, år som text och negativt belopp

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagGransfallTalTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt

## Uppgift
Testa `tolkaForslag` för fall robusthetsuppdraget inte täckte:
1. **Dubbla komponenter** i samma JSON-svar (samma namn två gånger) → båda ska finnas i `komponenter` (ingen krasch; dubbelkontroll sker senare i anmärkningar).
2. **Anskaffningsår som text** `"anskaffningsar": "2015"` → tolkas till 2015 om möjligt, annars anmärkning och `LAG`.
3. **Negativt anskaffningsvärde** → ska ge **anmärkning** (rimlig validering) och `Sakerhet.LAG` på komponenten, inte undantag. Om koden idag accepterar negativa tal utan anmärkning, utöka `heltalsVarde` eller parsningen.

Vid fel: rätta högst i `TolkaForslag.kt`.

## Exempel
- Två objekt med `"namn":"Tak"` → `komponenter.size == 2`.
- `"anskaffningsar": "2015"` → `anskaffningsar == 2015`.
- `"anskaffningsvarde": -500` → anmärkning om ogiltigt/negativt belopp, `sakerhet == LAG`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*TolkaForslagGransfallTalTest"

## Gör inte
- Ändra `TolkaForslagTest.kt` eller modeller.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #368). Ersatt av 2026-10-21-styrelserummet-k3-gransfall-15-tolka-dubbla-och-tal-f2.md
