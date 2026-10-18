# Uppdrag: Husloggen, dokument (manualer, ritningar)

## Bakgrund

Husloggen 1.0 och mätarställningar är klara (STATUS.md, Nu). Under "Senare" står dokument: manualer, ritningar, besiktningsprotokoll och liknande som hör till huset. Användaren vill ha dem på ett ställe, offline, och ha dem med vid försäljning. Mattias prov på telefon pågår; det här väntar inte på honom.

## Uppgift

1. Ny tabell `dokument` (id, titel, valfri kategori, valfri anteckning, filnamn, mimetyp, tillagd datum som `LocalDate`). Filen kopieras till appens privata lagring via systemets filväljare (SAF, `OpenDocument`) för PDF och bilder, på samma sätt som `BildLager` hanterar bilder. Inga nya behörigheter. Databasen går till nästa versionsnummer med Room-migration som bara kör `CREATE TABLE`/`CREATE INDEX` och ett migrationstest.
2. Ny flik eller undersida "Dokument" i `:husloggen`: lista (sorterad på kategori, sedan titel), lägg till (välj fil, titel, kategori), öppna filen med systemets visare (`ACTION_VIEW` via `FileProvider`, som övrig delning), ändra titel/kategori/anteckning och ta bort (filen tas bort med posten). Färdiga kategorier: Manual, Ritning, Besiktning, Garanti, Avtal, Övrigt.
3. Valfri koppling från en uppgift eller en post till ett dokument ("Se manualen") är inte med i det här uppdraget.
4. Dokument är obegränsade i gratisversionen och räknas inte som poster (gränsen på 15 är oförändrad). Filstorlek över 25 MB avvisas med ett tydligt meddelande.
5. Säkerhetskopia och import: metadata för dokument ingår. Filerna följer med på samma sätt som bilderna gör idag (följ befintligt mönster i `:karna`). En säkerhetskopia utan dokument ska fortfarande kunna importeras.
6. Strängar på svenska och engelska i `strings.xml`. Tryckytor minst 48 dp, innehållsbeskrivning på ikoner, kontrast enligt CLAUDE.md punkt 6.
7. Uppdatera `husloggen/APP.md` (flytta dokument från "Inte i 1.0") och lägg en rad i `BESLUTSLOGG.md` med datum.

## Klart när

- Migrationstest för den nya versionen går igenom, och befintliga tester i `:husloggen` går igenom.
- Nya tester med exempel: spara ett dokument med titel "Manual diskmaskin" och kategori Manual ger en rad i listan under Manual; borttagning tar bort både raden och filen; en fil på 26 MB avvisas, en på 24 MB godtas; import av en säkerhetskopia utan dokument fungerar; export och import ger samma dokumentmetadata tillbaka.
- Bygget och alla tester är gröna för `:karna`, `:garantivalvet` och `:husloggen`.

## Gör inte

- Rör inte Garantivalvets kod. Ändra `:karna` bara om Cursor bedömer det nödvändigt, och då med alla appmodulers tester gröna.
- Inga nya beroenden eller behörigheter, inget nätverk, ingen plats. Ingen egen PDF-visare i appen, ingen avläsning (OCR) av dokument.
- Ingen koppling till uppgifter eller poster, ingen delning till andra användare.
- Skriv inte om befintliga uppdrag. Dela upp det som passar den lokala modellen i uppgifter enligt `uppgifter/MALL.md` (högst 3 filer och cirka 150 rader per uppgift) och skriv frågor under "Frågor till Claude" i PR-beskrivningen.
