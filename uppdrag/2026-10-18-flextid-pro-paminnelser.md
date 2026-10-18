# Uppdrag: Flextid (app 3), Pro och påminnelser

## Bakgrund

`:flextid` finns på main (databas version 1: `dag`, `installning`). Pro-köpet och påminnelser byggs på samma sätt som i `:husloggen` (förebild: Pro-gräns, Billing via `:karna`, WorkManager-påminnelser). Svar på Cursors frågor i PR 258: gränsen i gratisversionen gäller antal dagposter (rader i `dag`) totalt, 15 st, i enlighet med CLAUDE.md punkt 6. Pro tar bara bort den gränsen. Export, CSV och säkerhetskopia är alltid gratis. Appikon vänta till butiksunderlag.

## Uppgift

1. Gräns på 15 dagposter i gratisversionen, i ett enda ställe i regler. Pro (engångsköp 49 kr via `:karna`) tar bort gränsen. Vid gränsen visas en tydlig förklaring och att export fortfarande är gratis. Befintlig data raderas aldrig.
2. Påminnelser med WorkManager, som kan slås av och på i Inställningar: "Glöm inte stämpla ut" (tid efter inställd normaltid, bara om dagen har start men inte slut) och valfri "Glöm inte stämpla in" på arbetsdagar. Notiskanal och behörigheten POST_NOTIFICATIONS enligt mönstret i `:husloggen`. Inget nätverk.
3. Strängar på svenska och engelska. Uppdatera `flextid/APP.md` och lägg en rad i `BESLUTSLOGG.md` med datum.

## Klart när

- Bygget och alla tester är gröna för alla moduler.
- Tester med exempel: 15 poster och inte Pro blockerar post 16; Pro tillåter 16; en påminnelse beräknas till rätt tid för normaltid 08:00 + 8 h + rast; ingen påminnelse när dagen redan har slut.
- Databasschemat ändras inte, annars migration och migrationstest.

## Gör inte

- Rör inte andra appars kod. Ändra `:karna` bara om nödvändigt och med alla modulers tester gröna.
- Inga nya beroenden utöver de i `:husloggen`, inget nätverk, ingen plats.
- Dela upp det som passar den lokala modellen i uppgifter enligt `uppgifter/MALL.md`. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
