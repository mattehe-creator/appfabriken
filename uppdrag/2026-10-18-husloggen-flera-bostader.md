# Uppdrag: Husloggen, flera bostäder

## Bakgrund

Husloggen har uppgifter, logg, mätare, dokument och plan för framtida kostnader (STATUS.md, Nu). Många har mer än en bostad: sommarstuga, hyresrätt de äger delar av, ett hus som ska säljas medan ett nytt köps. Idag finns en enda bostad. Flera bostäder står under "Senare" i STATUS.md. Det väntar inte på Mattias prov.

## Uppgift

1. Ny tabell `bostad` (id, namn, valfri adress, valfri anteckning). Befintlig data hör till en förvald bostad som skapas av migrationen (namn från strängresurs, svenska "Min bostad", engelska "My home"). Alla befintliga tabeller (uppgifter, loggposter, mätare, dokument, planerade kostnader) får kolumnen `bostad_id` med den förvalda bostaden som värde för befintliga rader. Databasen går till nästa versionsnummer med Room-migration och migrationstest som bevisar att befintliga rader behålls och hamnar på den förvalda bostaden. Cursor bedömer om migrationen ska vara en tabellkopia (SQLite kan inte lägga till främmande nyckel med ALTER) och skriver den noggrant. Stanna och skriv under "Frågor till Claude" om något data riskerar gå förlorat.
2. Bostadsväljare högst upp (eller i inställningar, Cursor väljer det som passar befintligt mönster). Vald bostad sparas och alla flikar visar bara den bostadens data. Lägg till, byt namn på och ta bort bostad. Den sista bostaden kan inte tas bort. Borttagning av en bostad med data kräver bekräftelsedialog som anger antalet poster som försvinner.
3. Gratisgränsen på 15 poster räknas per app (alla bostäder tillsammans), inte per bostad. Flera bostäder är gratis, upp till 3. Fler än 3 kräver Pro. Cursor lägger dialogen i samma mönster som befintlig Pro-dialog.
4. Sammanställning, PDF "Husets historik" och CSV gäller vald bostad och anger bostadens namn i rubriken.
5. Säkerhetskopia och import: bostäder ingår, och alla poster pekar på sin bostad. En säkerhetskopia utan bostäder (äldre version) importeras till den förvalda bostaden.
6. Påminnelser och notiser visar bostadens namn när det finns fler än en bostad.
7. Strängar på svenska och engelska i `strings.xml`. Tryckytor minst 48 dp, innehållsbeskrivning på ikoner, kontrast enligt CLAUDE.md punkt 6.
8. Uppdatera `husloggen/APP.md` och lägg en rad i `BESLUTSLOGG.md` med datum.

## Klart när

- Migrationstest för den nya versionen går igenom: rader i alla tabeller finns kvar och har den förvalda bostadens id. Befintliga tester i `:husloggen` går igenom.
- Nya tester med exempel: två bostäder med varsin loggpost visar bara den valda bostadens post; borttagning av den sista bostaden avvisas; en fjärde bostad utan Pro avvisas och visar Pro-dialogen; 10 poster i bostad A och 5 i bostad B ger gratisgränsen nådd (15); sammanställning för bostad A summerar bara A:s poster; export och import ger samma bostäder och kopplingar tillbaka; import av en säkerhetskopia utan bostäder lägger allt på den förvalda bostaden.
- Bygget och alla tester är gröna för `:karna`, `:garantivalvet` och `:husloggen`.

## Gör inte

- Rör inte Garantivalvets kod. Ändra `:karna` bara om Cursor bedömer det nödvändigt, och då med alla appmodulers tester gröna.
- Inga nya beroenden eller behörigheter, inget nätverk, ingen plats.
- Ingen flytt av poster mellan bostäder, ingen delning mellan bostäder och inga bostadstyper eller adressvalidering.
- Skriv inte om befintliga uppdrag. Dela upp det som passar den lokala modellen i uppgifter enligt `uppgifter/MALL.md` (högst 3 filer och cirka 150 rader per uppgift). Migrationen och allt som rör fler än 3 filer gör Cursor själv. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
