# Uppdrag: Flextid — svenska helgdagar

Skrivet av Claude 2026-10-21. Uppgifterna 01–06 är skrivna direkt av Claude i `uppgifter/ny/` (Cursor delar inte upp detta uppdrag). Claudes egen del (inställning, databas och skärmar) görs av Claude efter att 05 är mergad.

## Bakgrund

Flextid räknar normaltid på alla dagar som arbetsdagsmasken säger är arbetsdagar. På röda dagar som infaller på en vardag (långfredag, 1 maj, juldagen med flera) blir det fel: den som är ledig får minus om dagen registreras som Arbete, och månadens normaltid blir för hög. Användaren måste i dag själv sätta typ Helgdag. Appen ska känna till de svenska allmänna helgdagarna enligt lagen (1989:253) om allmänna helgdagar, och de tre aftnar som de flesta arbetsplatser behandlar som lediga (midsommarafton, julafton, nyårsafton).

## Uppgift

Rena funktioner i `flextid/src/main/kotlin/se/tmconnect/flextid/regler/`, med enhetstester:

1. `paskdagen(ar)`.
2. Rörliga helgdagar ur påskdagen.
3. Alla allmänna helgdagar för ett år.
4. Aftnar.
5. Uppslag för ett datum.
6. Antal arbetsdagar och normaltid för en månad.

Därefter (Claude): inställningen "Röda dagar räknas inte som arbetsdagar" (standard på) och "Aftnar räknas som lediga" (standard på), databas version 2 med migration, och röda dagar markerade i Idag och Vecka.

## Klart när

- Alla sex uppgifter mergade med gröna tester.
- Datum stämmer mot exemplen i uppgifterna (kontrollerade mot en oberoende beräkning 2026-10-21).

## Gör inte

- Inga avtalsregler (OB, övertidsersättning, kollektivavtal). Appen ger ingen rådgivning (APP.md).
- Inget nätverk, inga nya beroenden.
