# Uppdrag: Husloggen, flera bostäder (omstart på nuvarande main)

## Bakgrund

Uppdraget `2026-10-18-husloggen-flera-bostader.md` gav PR 254. Den PR:en byggdes på en main utan tabellen `planerad_kostnad` (Room v5 från PR 252) och bygger därför ett eget v5 som krockar med main (konflikt, och planerade kostnader saknas helt). PR 254 stängs. Detta uppdrag ersätter den.

## Uppgift

Gör hela uppdraget i `2026-10-18-husloggen-flera-bostader.md` på nuvarande main, med dessa ändringar:

1. Main har redan databas v5 (tabellen `planerad_kostnad`, migration 4 till 5, säkerhetskopia JSON v4 med `planeradeKostnader`). Bostäder blir databas v6, migration 5 till 6, och säkerhetskopia JSON v5. Rör inte befintlig migration 4 till 5.
2. `planerad_kostnad` får `bostad_id` precis som uppgifter, loggposter, mätare och dokument. "Genomförd" skapar en loggpost i samma bostad. Planerade kostnader filtreras per vald bostad, ingår i sammanställning per bostad och i export och import med koppling till sin bostad. Äldre säkerhetskopior (v1 till v4) importeras till den förvalda bostaden.
3. Starta från färsk main (`git fetch`, ny gren från origin/main). Återanvänd gärna idéer från PR 254 (gren `cursor/husloggen-flera-bost-der-d6c0`) men skriv migrationen och schemat `6.json` om mot main.

## Klart när

- Allt under "Klart när" i det ursprungliga uppdraget, plus: migrationstest 5 till 6 som visar att rader i `planerad_kostnad` behålls och får den förvalda bostaden, och ett test där två bostäder har varsin planerad kostnad och bara vald bostads visas och summeras.
- Bygget och alla tester gröna för `:karna`, `:garantivalvet` och `:husloggen`, och PR:en är mergebar mot main utan konflikt.

## Gör inte

- Samma som i det ursprungliga uppdraget. Rör inte migrationerna 1 till 5. Skriv inte om befintliga uppdrag. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
