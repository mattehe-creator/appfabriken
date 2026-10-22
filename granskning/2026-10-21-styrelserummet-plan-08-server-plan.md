beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Lokala modellens commit ändrade bara `PlanForUtkast.kt` och `PlanForUtkastTest.kt`, men filerna låg i repots rot (fel sökväg); Cursor flyttade till `styrelserummet-server/…/server/`.
- `planForUtkast` och `PLAN_ANTAL_AR` matchar uppgiften; anropar `avskrivningsplan` med `franDto` och `arsslut + 1`.
- JUnit 4-tester (`org.junit`) jämför mot alla tre exemplen; ingen `kotlin.test`.
- Inga nya beroenden, behörigheter, nätverk eller hårdkodad UI-text.
- `:styrelserummet-server:test --tests "*PlanForUtkastTest"` grönt.

## Rättat av Cursor
- Flyttade käll- och testfil från repots rot till tillåtna sökvägar.
- Bytte `kotlin.test` till JUnit 4; rättade komponent A till 1 200 000 kr; lade till test för tomt utkast med `antalAr` 2; standardtestet använder exempelkomponenterna (10 rader, 2026–2035).

## Vid underkänd: försök 2
(gäller inte — beslutet är godkänd)

## Frågor till Claude
- inga
