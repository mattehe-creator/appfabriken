# Uppdrag: Husloggen, flera bostäder

## Bakgrund

Husloggen är i dag ett hus. Många har fritidshus, hyresrätt de renoverar eller föräldrars hus att hålla koll på. STATUS.md (Senare) listar flera bostäder efter 1.0. Kön är tom och Husloggen är i aktiv utveckling, så detta tas nu. Beslut: Claude (CLAUDE.md punkt 9, databasschema och arkitektur).

## Uppgift

Cursor gör databasdelen själv (migration, test av migrationen) och delar upp resten i små uppgifter för den lokala modellen enligt uppgifter/MALL.md (högst 3 filer och cirka 150 rader per uppgift).

1. Ny tabell `bostad` (id, namn, skapad). Befintliga data hör till en förvald bostad "Mitt hem" (sv) / "My home" (en) som skapas av migrationen. Uppgifter, loggposter och mätare får kolumnen `bostad_id` med den förvalda bostaden som värde för befintliga rader. Databasversion upp ett steg, migration utan datatapp, `fallbackToDestructiveMigration` används inte, schemafil exporteras och migrationstest finns.
2. Ren funktion som filtrerar listor på vald bostad. Vald bostad sparas i DataStore. Alla listor, Översikt, Kommande år, sammanställning, PDF och CSV visar bara vald bostad.
3. Val av bostad högst upp på Översikt: lägg till, byt namn, byt aktiv, ta bort (bekräftelse, och borttagning av bostad med poster kräver att användaren väljer att radera posterna eller flytta dem till en annan bostad). Sista bostaden kan inte tas bort.
4. Gratisgränsen på 15 poster räknas totalt över alla bostäder (oförändrat beteende för den som har en bostad).
5. Säkerhetskopia och import tar med bostäder. En fil utan bostäder går att importera och hamnar i den förvalda bostaden.
6. Strängar i `strings.xml` på svenska och engelska. Inga nya behörigheter, inget nytt beroende.

## Klart när

- Migrationstest från föregående version: befintliga rader hamnar i "Mitt hem", inget försvinner.
- Enhetstester: filtrering per bostad, gratisgräns över flera bostäder, import av gammal fil, borttagning med flytt och med radering.
- `gradle :husloggen:test` och bygget är gröna.
- Skärmarna går att nå och visar påhittade värden korrekt.

## Gör inte

- Ändra `:karna` (utom om en gemensam hjälpare är oundviklig; skriv då varför under "Frågor till Claude"), Garantivalvet eller Flextid.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
