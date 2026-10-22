# Uppdrag: Koppla in kostnader i Husloggen

## Bakgrund
Rena funktioner är mergade men syns inte i appen: `forbrukningKostnadOre`, `senasteKostnadPerUppgift` och `KommandeKostnad`. Mattias ska kunna se dem utan att ange en hel prismodell.

## Uppgift
Cursor delar upp arbetet i små uppgifter för den lokala modellen (UI i Compose, strängar) och tar själv databasändringen:
1. Pris per mätare (öre per enhet) i mätarformuläret — databas v7, migration och test (Cursor).
2. Visa uppskattad kostnad vid mätarförbrukning när pris finns.
3. Visa senaste kostnad per uppgift i Att göra-listan och i uppgiftsformuläret.
4. Visa uppskattad kostnad kommande 12 månader på Översikt.

## Klart när
- Alla fyra delarna syns i appen och bygget och alla tester är gröna.
- Migrationen har test och schemafil i `husloggen/schemas/` är uppdaterad.

## Gör inte
- Ändra Garantivalvet eller `:karna`.
- Lägga till nätverk.
