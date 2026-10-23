# Uppgift: Exempeldata, förening Brf Exempelgården

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelDataForeningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Forening.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTabellTest.kt

## Uppgift
Skapa paketet `se.tmconnect.styrelserummet.underlag.exempel` med `ExempelData.kt` som innehåller **påhittade** konstanter (inga riktiga uppgifter):

- `const val FORENING_NAMN = "Brf Exempelgården"`
- `const val ORGANISATIONSNUMMER = "000000-0000"`
- `const val ADRESS = "Exempelvägen 1, 100 00 Teststad"`
- `const val BYGGNADSAR = 1974`
- `const val RAKENSKAPSAR = 2025` (innevarande år i handlingarna)

`data class ExempelAtgard(val namn: String, val ar: Int, val kostnadKronor: Long?)` — `null` kostnad betyder att planen saknar belopp för raden.

Lägg till `fun exempelForening(): Forening` som returnerar `Forening(FORENING_NAMN, BYGGNADSAR)`.

`val EXEMPEL_ATGARDER: List<ExempelAtgard>` ska finnas som **tom lista** (`emptyList()`) tills senare uppgifter fyller den; exportera listan som `var` eller byggfunktion enligt vad som passar nästa uppgift (nästa uppgift **ersätter/utökar** listan till en `val` med alla åtgärder).

Test `foreningKonstanterArPahittade`: assertEquals på namn, org.nr, adress, byggår och att org.nr inte liknar riktigt format med riktiga siffror (exakt `000000-0000`).

## Exempel
- `exempelForening().namn` → `"Brf Exempelgården"`
- `exempelForening().byggnadsar` → `1974`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelDataForeningTest"

## Gör inte
- Lägga till åtgärder i listan (nästa uppgifter).
- Ändra modeller eller andra filer.
- Lägga till beroenden.
