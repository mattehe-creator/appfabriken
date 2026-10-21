# Uppgift: forklara felkoder på svenska

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-14-felkoder-kartlaggning-f2.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felmeddelanden.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelmeddelandenTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Felkoder.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt (befintliga texter)

## Uppgift
`fun forklara(kod: String): String`

- För varje kod i `Felkoder.allaKoder()`: returnera ett **vänligt svenskt** meddelande som motsvarar dagens API-/jobbtext (samma innebörd som i Application/JobbKo, får formulera om lite).
- Okänd kod: `"Något gick fel. Försök igen."` – **visa aldrig** själva kodsträngen i svaret.
- Meddelanden ska inte innehålla ordet `"felkod"` eller tekniska stacktrace-detaljer.

Testa minst tre kända koder med **exakt** förväntad sträng (välj i testfilen), plus en okänd kod.

## Exempel
- `forklara(Felkoder.INGA_PDF)` → `"Ladda upp minst en PDF."` (samma som Application)
- `forklara("FINNS_EJ")` → `"Något gick fel. Försök igen."`

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelmeddelandenTest.forklara"

## Gör inte
- Koppla in `forklara` i Application.kt (senare uppdrag).
- Lägga till beroenden.
