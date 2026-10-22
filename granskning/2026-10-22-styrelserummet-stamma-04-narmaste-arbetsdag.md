beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Endast filerna under "Ändra bara dessa filer" är ändrade och ligger på rätt sökväg.
- `narmasteArbetsdag` matchar uppgiften: arbetsdag = vardag som inte är helgdag, riktning 1/-1, annat värde kastar `IllegalArgumentException`, redan arbetsdag returneras oförändrad.
- Testerna jämför mot exemplen i uppgiften (långfredag, påskdagen, midsommarafton, bakåt på arbetsdag, ogiltig riktning).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar utöver befintligt mönster i `Helgdagar.kt`.
- Enhetstest använder JUnit 4 (`org.junit`), inte `kotlin.test`.

## Rättat av Cursor
- `HelgdagarArbetsdagTest.kt`: bytte `kotlin.test.assertEquals` / `assertFailsWith` till `org.junit.Assert.assertEquals` / `assertThrows` så att testkompileringen går igenom.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
