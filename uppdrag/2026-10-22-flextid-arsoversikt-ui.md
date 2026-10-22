# Uppdrag: Koppla in årsöversikt och saldoprognos i Flextid

## Bakgrund
`arsOversikt` (saldo vid månadens slut per månad, med CSV) och funktionen för uppskattat saldo vid årets slut är mergade som rena funktioner men syns inte i appen. Det finns redan en årsöversikt som CSV via PR #546; den här uppgiften gäller visningen på skärmen.

## Uppgift
Cursor delar upp arbetet i små uppgifter för den lokala modellen:
1. En skärm "Årsöversikt" (nås från Mer) som listar månaderna med saldo vid månadens slut, med `arsOversikt`.
2. Uppskattat saldo vid årets slut överst på skärmen, med en kort förklaring av hur det räknas (utifrån nuvarande takt).
3. Knapp för att dela årsöversikten som CSV med den befintliga CSV-funktionen.

Strängar i `strings.xml` på svenska och engelska. Använd de befintliga funktionerna och skriv inte om deras logik.

## Klart när
- Skärmen och prognosen syns i appen, CSV-knappen fungerar, bygget och alla tester är gröna.
- Ingen ny behörighet, inget nytt beroende och ingen hårdkodad text.

## Gör inte
- Ändra databasschemat eller `:karna`.
- Lägga till nätverk.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
