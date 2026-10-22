# Uppdrag: Koppla in kostnader i Husloggen

## Bakgrund
Rena funktioner är mergade men syns inte i appen: `forbrukningKostnadOre` (mätarförbrukning till kostnad), `senasteKostnadPerUppgift` och `KommandeKostnad` (uppskattad kostnad för kommande tolv månader). Mattias ska kunna se dem i appen utan att ange en hel prismodell.

## Uppgift
Cursor delar upp arbetet i små uppgifter för den lokala modellen (UI i Compose, strängar i `strings.xml` på svenska och engelska) och tar själv databasändringen:
1. Pris per mätare (öre per enhet, valfritt) i mätarformuläret. Databasändring med migration och migrationstest görs av Cursor.
2. Visa uppskattad kostnad bredvid mätarförbrukningen när pris finns.
3. Visa senaste kostnad per uppgift i uppgiftslistan och i detaljvyn.
4. Visa uppskattad kostnad för kommande tolv månader på Översikt.

Använd de befintliga funktionerna. Skriv inte om deras logik. Uppgifter som rör samma filer (`HusViewModel.kt`, `MainActivity.kt`) läggs så att konflikterna blir små.

## Klart när
- Alla fyra delarna syns i appen och bygget och alla tester är gröna.
- Migrationen har test, och schemafilen i `husloggen/schemas/` är uppdaterad.
- Ingen ny behörighet, inget nytt beroende och ingen hårdkodad text.

## Gör inte
- Ändra Garantivalvet eller `:karna`.
- Lägga till nätverk.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
