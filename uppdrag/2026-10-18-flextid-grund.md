# Uppdrag: Flextid (app 3), grund och dagsregistrering

## Bakgrund

Husloggen har nu alla funktioner i IDEURVAL.md (uppgifter, logg, bilder, påminnelser, Pro, sammanställning, säkerhetskopia, mätare, dokument, plan för kostnader, flera bostäder). Det som återstår är Mattias prov på telefon, och det blockerar inget annat. Enligt direktiven får app 3 påbörjas utan väntetid. Nästa idé i IDEURVAL.md är F, Flextid (33 poäng, delad andraplats med C). F väljs före C eftersom bygget är ren CRUD och beräkning, utan koppling till andra appars data, och eftersom Laddloggens behov redan täcks av bilens och laddboxens appar (IDEURVAL.md, omprövning 2026-10-16).

Appen är en enkel, helt offline tidsregistrering för den som har flextid: in- och utstämpling per dag, rast, och saldo mot en vald normaltid. Appen antar inget kollektivavtal. Alla regler (normaltid per dag, rast, saldogränser) är inställningar som användaren själv anger. Det undviker att appen påstår något om vad ett visst avtal tillåter. Arbetsnamn: Flextid. applicationId och slutligt appnamn beslutas av Mattias vid publicering (CLAUDE.md punkt 9), så använd paketet `se.tmconnect.flextid` tills vidare.

## Uppgift

1. Ny appmodul `:flextid` i Gradle-bygget, på samma sätt som `:husloggen` (modul, tema från `:karna`, Room, tester). Lägg till modulen i `settings.gradle.kts` och i bygget (`bygg.yml`) så att dess tester körs.
2. Databas version 1, tabell `dag`: `datum` (`LocalDate`, unik), `start` och `slut` (`LocalTime`, valfria), `rast_min` (Int), `typ` (`ARBETE`, `LEDIG`, `SEMESTER`, `SJUK`, `HELGDAG`) och `anteckning`. Tabell `installning` med en rad: `normaltid_min_per_dag` (standard 480), `standard_rast_min` (standard 30), `ingangssaldo_min` (standard 0, kan vara negativ), `arbetsdagar` som bitmask måndag–fredag. Tider lagras som minuter sedan midnatt eller `LocalTime`, som övriga appar gör.
3. Regler i `regler/` på ett ställe: arbetad tid för en dag = slut minus start minus rast. Dagens utfall = arbetad tid minus normaltid för arbetsdagar med typ ARBETE; typ LEDIG, SEMESTER, SJUK och HELGDAG ger utfall 0 (dagen räknas som fullgjord). Saldo = ingångssaldo plus summan av alla dagars utfall. Dagar utan registrering i det förflutna räknas inte (ingen automatisk minusbokning). Slut före start över midnatt tillåts inte; formuläret ger ett felmeddelande.
4. Skärmar i Compose: Idag (stämpla in, stämpla ut, rast, nuvarande saldo), Vecka (lista med dagens tider och utfall samt veckosumma) och Inställningar. Redigera eller ta bort en dag. Veckan börjar måndag.
5. Export: CSV för vald månad (datum, start, slut, rast, arbetad tid, utfall, typ) via systemets filväljare. Säkerhetskopia till och från JSON via filväljaren, med versionsnummer i filen. En fil från en äldre version ska gå att importera.
6. Strängar på svenska och engelska i `strings.xml`. Tryckytor minst 48 dp, innehållsbeskrivning på ikoner, kontrast minst 4,5:1. Inget nätverk, inga behörigheter, ingen plats.
7. Skriv `flextid/APP.md` (regler för appen, enligt mönstret i `husloggen/APP.md`) och en rad i `BESLUTSLOGG.md` med datum.

Pro och påminnelser (glöm inte stämpla ut) är inte med i det här uppdraget.

## Klart när

- Bygget och alla tester är gröna för `:karna`, `:garantivalvet`, `:husloggen` och `:flextid`.
- Tester med exempel: start 08:00, slut 16:30, rast 30 min, normaltid 480 ger arbetad tid 480 och utfall 0; start 07:30, slut 17:00, rast 30 ger arbetad tid 540 och utfall +60; start 08:00, slut 15:00, rast 30 ger utfall -90; dag med typ SEMESTER ger utfall 0; ingångssaldo -120 plus ett utfall på +60 ger saldo -60; slut före start avvisas; import av en säkerhetskopia med lägre versionsnummer fungerar; export och import ger samma dagar tillbaka.
- Migrationstest finns för varje databasversion över 1 (i det här uppdraget bara version 1, så inget krävs men schemat exporteras till `flextid/schemas/`).

## Gör inte

- Rör inte Garantivalvets eller Husloggens kod. Ändra `:karna` bara om Cursor bedömer det nödvändigt, och då med alla appmodulers tester gröna.
- Inga nya beroenden utöver de som redan används i `:husloggen`, inga behörigheter, inget nätverk, ingen plats.
- Inga påståenden i appen om vad kollektivavtal eller lag tillåter. Inga avtalsmallar i det här uppdraget.
- Skriv inte om befintliga uppdrag. Dela upp det som passar den lokala modellen i uppgifter enligt `uppgifter/MALL.md` (högst 3 filer och cirka 150 rader per uppgift), lägg modulen, databasen och bygget som egna tyngre delar som Cursor gör själv, och skriv frågor under "Frågor till Claude" i PR-beskrivningen.
