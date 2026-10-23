# Uppgift: Exempeltexter underhållsplan tre sidor

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-05-atgarder-balkong-brand-summor.md

## Vad som saknades
- `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt` skapades inte.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterPlanTest.kt` skapades inte.
- Committen «Uppgift startad» flyttade bara uppgiftsfilen utan Kotlin-kod.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterPlanTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTvaSidorTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTkrTextTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanSaknadKostnadTest.kt (kostnad `–` när belopp saknas)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt (`FORENING_NAMN`, `ORGANISATIONSNUMMER`, `ADRESS`, `BYGGNADSAR`, `EXEMPEL_ATGARDER`, `summaAtgarderMedKostnad()`)

## Uppgift
Skapa `fun underhallsplanSidor(): List<String>` som returnerar **exakt tre** strängar (en per sida), med markerbar löptext (radbrytningar `\n` är ok):

1. **Sida 1 — inledning:** inkludera `FORENING_NAMN`, `ADRESS`, `ORGANISATIONSNUMMER`, `BYGGNADSAR` och en kort mening om att underhållsplanen är påhittad för demo (svensk text).
2. **Sida 2 — tabell:** första raden ska innehålla kolumnrubrikerna `Åtgärd`, `År`, `Kostnad` (samma ordning). Sedan **en rad per** post i `EXEMPEL_ATGARDER` i listordning. Kostnad i kronor formateras med **mellanslag** som tusentalsavgränsare, t.ex. `5 000 000`. Om `kostnadKronor == null` (Balkonger): skriv `–` i kostnadskolumnen (som i `SyntetiskPlanSaknadKostnadTest`), inte ett påhittat belopp.
3. **Sida 3 — fortsättning:** minst en rad som uttrycker total planerad investering i **tkr** och som stämmer med `summaAtgarderMedKostnad()` (27_000_000 kr → t.ex. `27 000 tkr` eller `27000 tkr`; texten ska innehålla delsträngen `tkr`).

Hjälpfunktion för tusentalsavgränsare får ligga privat i samma fil (t.ex. `formatKronor(kronor: Long): String`).

Implementera **inte** `arsredovisningSidor()` ännu.

## Exempel
- Sida 2 innehåller strängen `5 000 000` (Yttertak).
- Sida 2 innehåller `Balkonger`, `2038` och `–` på samma rad eller i samma sidtext.
- Sida 1 innehåller `Brf Exempelgården` och `000000-0000`.
- Sida 3 innehåller `tkr`.

## Test som ska gå igenom
Skriv `ExempelTexterPlanTest` med testmetoden `underhallsplanHarTreSidorOchTabell` som:

- `Assert.assertEquals(3, underhallsplanSidor().size)`
- sida 1 (`[0]`) innehåller `FORENING_NAMN` och `ORGANISATIONSNUMMER`
- sida 2 (`[1]`) innehåller `Åtgärd`, `Yttertak`, `Balkonger` och exempelvis `5 000 000`
- sida 3 (`[2]`) innehåller `tkr` (case-insensitive ok)

Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), **inte** `kotlin.test`.

gradle :styrelserummet:testDebugUnitTest --tests "*ExempelTexterPlanTest"

## Gör inte
- Implementera `arsredovisningSidor()`.
- Hårdkoda andra föreningar än konstanterna i `ExempelData.kt`.
- Ändra `ExempelData.kt` eller andra filer.
- Committa enbart uppgiftsflytt utan kod.
