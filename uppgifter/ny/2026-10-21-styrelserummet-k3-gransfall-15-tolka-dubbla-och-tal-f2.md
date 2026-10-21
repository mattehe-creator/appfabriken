# Uppgift: Tolka dubbla komponenter, år som text och negativt belopp

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Filen `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagGransfallTalTest.kt` skapades inte.
- `gradle :styrelserummet:test --tests "*TolkaForslagGransfallTalTest"` kan inte köras utan testklassen; uppgiftens exempel är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagGransfallTalTest.kt (ny)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt (endast om testerna avslöjar fel, t.ex. negativa belopp utan anmärkning)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt

## Uppgift
Skapa testklassen `TolkaForslagGransfallTalTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert` — **inte** `kotlin.test`).

Testa `tolkaForslag(jsonText, filnamn)` för fall robusthetsuppdraget inte täckte:

1. **Dubbla komponenter:** JSON med två objekt i `komponenter` med samma `"namn":"Tak"` (övriga fält giltiga enligt `TolkaForslagTest`) → båda ska finnas i `resultat.komponenter` (`size == 2`), ingen krasch.
2. **Anskaffningsår som text:** `"anskaffningsar": "2015"` (sträng, inte JSON-tal) → `komponenter.single().anskaffningsar == 2015`.
3. **Negativt anskaffningsvärde:** `"anskaffningsvarde": -500` → minst en anmärkning som beskriver ogiltigt/negativt belopp (t.ex. innehåller `anskaffningsvarde` och `negativ`, eller motsvarande ny text i `TolkaForslag.kt`), och `sakerhet == Sakerhet.LAG` på komponenten — **inte** undantag.

Om (3) faller utan kodändring: utöka `heltalsVarde` eller parsningen i `TolkaForslag.kt` så negativa värden för `anskaffningsvarde` ger anmärkning och `markeraLag()` (samma mönster som ogiltigt heltal).

## Exempel (ska finnas som assert i tester)
- Två objekt med `"namn":"Tak"` → `komponenter.size == 2`.
- `"anskaffningsar": "2015"` → `anskaffningsar == 2015`.
- `"anskaffningsvarde": -500` → anmärkning om ogiltigt/negativt belopp, `sakerhet == LAG`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*TolkaForslagGransfallTalTest"

## Gör inte
- Ändra `TolkaForslagTest.kt` eller modeller.
- Lägga till beroenden.
- Lägga filer i repots rot.
