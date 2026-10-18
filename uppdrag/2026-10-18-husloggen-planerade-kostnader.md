# Uppdrag: Husloggen, plan för framtida kostnader

## Bakgrund

Husloggen har uppgifter, logg, mätare och dokument (STATUS.md, Nu). Under "Senare" och "Inte i 1.0" står plan för framtida kostnader. Användaren vill se vad som väntar de kommande åren (nytt tak, ny panna, målning) och ha en summa per år. Det väntar inte på Mattias prov.

## Uppgift

1. Ny tabell `planerad_kostnad` (id, titel, uppskattat belopp i öre som `Long`, planerat år som `Int`, valfri anteckning, valfri koppling till en uppgift är inte med). Databasen går till nästa versionsnummer med Room-migration som bara kör `CREATE TABLE`/`CREATE INDEX` och ett migrationstest.
2. Ny undersida "Plan" i `:husloggen` (följ befintligt navigeringsmönster; Cursor väljer plats och bedömer om fem flikar redan är för många): lista grupperad på år med summa per år, lägg till, ändra, ta bort. Planerat år får inte ligga före innevarande år. Belopp måste vara större än 0.
3. Knappen "Genomförd" på en planerad kostnad öppnar loggformuläret för en utgiftspost med titel och belopp ifyllda, och tar bort den planerade kostnaden först när posten sparats. Gratisgränsen på 15 poster är oförändrad; när den är nådd visas Pro-dialogen som idag och den planerade kostnaden finns kvar.
4. Planerade kostnader är obegränsade i gratisversionen och räknas inte som poster.
5. Säkerhetskopia och import: planerade kostnader ingår. En säkerhetskopia utan dem ska fortfarande kunna importeras. Ingår i CSV-export om det passar befintligt mönster, annars inte.
6. Strängar på svenska och engelska i `strings.xml`. Tryckytor minst 48 dp, innehållsbeskrivning på ikoner, kontrast enligt CLAUDE.md punkt 6.
7. Uppdatera `husloggen/APP.md` (flytta plan för framtida kostnader från "Inte i 1.0") och lägg en rad i `BESLUTSLOGG.md` med datum.

## Klart när

- Migrationstest för den nya versionen går igenom, och befintliga tester i `:husloggen` går igenom.
- Nya tester med exempel: två planerade kostnader år 2028 på 40 000 kr och 15 000 kr ger summan 55 000 kr för 2028; ett belopp på 0 avvisas; ett planerat år före innevarande år avvisas; "Genomförd" tar bort raden först efter att posten sparats och behåller den om sparandet stoppas av gratisgränsen; export och import ger samma planerade kostnader tillbaka; import av en säkerhetskopia utan dem fungerar.
- Bygget och alla tester är gröna för `:karna`, `:garantivalvet` och `:husloggen`.

## Gör inte

- Rör inte Garantivalvets kod. Ändra `:karna` bara om Cursor bedömer det nödvändigt, och då med alla appmodulers tester gröna.
- Inga nya beroenden eller behörigheter, inget nätverk, ingen plats.
- Ingen prognos, inflationsuppräkning eller skatteberäkning. Ingen påminnelse för planerade kostnader. Ingen koppling till uppgifter.
- Skriv inte om befintliga uppdrag. Dela upp det som passar den lokala modellen i uppgifter enligt `uppgifter/MALL.md` (högst 3 filer och cirka 150 rader per uppgift) och skriv frågor under "Frågor till Claude" i PR-beskrivningen.
