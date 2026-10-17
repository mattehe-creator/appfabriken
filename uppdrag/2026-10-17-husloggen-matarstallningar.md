# Uppdrag: Husloggen, mätarställningar

## Bakgrund

Husloggen 1.0 är funktionsmässigt klar (STATUS.md, Nu, punkt 1–9) och väntar på Mattias prov. Under "Senare" står mätarställningar. IDEURVAL.md konstaterade att nätbolagens appar visar el och fjärrvärme, så det här är en liten funktion i Husloggen och inte en egen app: användaren som läser av vatten, olja, pellets, egen el eller fjärrvärme själv vill ha en enkel lista över avläsningar och förbrukning per period. Allt lokalt, inget nätverk.

## Uppgift

1. Ny tabell `matare` (namn, enhet, valfri anteckning) och `avlasning` (matare-id, datum, värde). Värde lagras som `Long` i tusendelar av enheten, datum som `LocalDate`, som övriga tabeller. Databasen går till nästa versionsnummer med Room-migration som bara kör `CREATE TABLE`/`CREATE INDEX` och ett migrationstest. Inga befintliga rader ändras.
2. Ny flik eller undersida "Mätare" i `:husloggen`: lista över mätare, lägg till och ta bort mätare, lägg till avläsning (datum och värde), lista avläsningar per mätare med förbrukning mellan två avläsningar (värde minus föregående värde) och förbrukning per kalenderår. Färdiga förslag på namn och enhet: El (kWh), Vatten (m³), Fjärrvärme (kWh), Olja (liter), Pellets (kg), eget namn.
3. Regler i `regler/` på ett ställe: ett nytt värde lägre än föregående avläsning för samma mätare ger en varning i formuläret ("lägre än förra avläsningen, har mätaren nollställts eller bytts?") men sparas om användaren bekräftar; förbrukning räknas då inte över bytet. Samma datum två gånger för samma mätare är inte tillåtet.
4. Säkerhetskopia och import skriver och läser mätare och avläsningar. En fil utan dem ska fortfarande kunna importeras. CSV-export av avläsningar för en mätare.
5. Gratisgränsen (15 poster) berörs inte: mätare och avläsningar är obegränsade och räknas inte som poster.
6. Strängar på svenska och engelska i `strings.xml`. Tryckytor minst 48 dp, innehållsbeskrivning på ikoner, kontrast enligt CLAUDE.md punkt 6.
7. Uppdatera `husloggen/APP.md` (flytta mätarställningar från "Inte i 1.0") och lägg en rad i `BESLUTSLOGG.md` med datum.

## Klart när

- Migrationstest för den nya versionen går igenom, och befintliga tester i `:husloggen` går igenom.
- Nya tester med exempel: avläsningar 1 000,000 (1 jan) och 1 250,500 (1 feb) ger förbrukning 250,500 för perioden; en avläsning på 900,000 efter 1 250,500 ger varning och ingen negativ förbrukning; samma datum två gånger avvisas; import av en säkerhetskopia utan mätare fungerar; export och import ger samma mätare och avläsningar tillbaka.
- Bygget och alla tester är gröna för `:karna`, `:garantivalvet` och `:husloggen`.

## Gör inte

- Rör inte Garantivalvets kod. Ändra `:karna` bara om Cursor bedömer det nödvändigt, och då med alla appmodulers tester gröna.
- Inga nya beroenden eller behörigheter, inget nätverk, ingen plats.
- Ingen koppling till elbolag, Mina sidor eller andra tjänster. Inga diagram i det här uppdraget.
- Skriv inte om befintliga uppdrag. Dela upp det som passar den lokala modellen i uppgifter enligt `uppgifter/MALL.md` (högst 3 filer och cirka 150 rader per uppgift) och skriv frågor under "Frågor till Claude" i PR-beskrivningen.
