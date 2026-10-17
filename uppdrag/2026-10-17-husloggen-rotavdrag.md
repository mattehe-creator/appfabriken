# Uppdrag: Husloggen, belopp betalat själv (rot/rut)

## Bakgrund

Sammanställningen och PDF:en "Husets historik" (uppdrag 2026-10-16/17, mergade) summerar förbättringsutgifter per år och typ. Vid vinstberäkning vid försäljning räknas troligen bara den del som ägaren själv betalat. Har arbetet gett rot- eller rutavdrag är det fakturabeloppet minus skattereduktionen. Skatteverkets sida om reparationer och underhåll säger inget om det, så regeln måste verifieras innan något byggs (se FRAGOR.md, 2026-10-17). Appen visar detta som information, inte rådgivning.

## Uppgift

1. Verifiera mot Skatteverkets sidor om förbättringsutgifter och om rot/rut (och lagtexten, inkomstskattelagen 45 kap. för fastighet). Skriv resultatet med källa (sida och datum) i `husloggen/APP.md` och en rad i `BESLUTSLOGG.md` med datum.
2. Stämmer regeln: lägg till ett valfritt fält på utgiftsposter, "avdrag (rot/rut)" i kronor. Beloppet som räknas i sammanställningen, PDF och CSV blir belopp minus avdrag. Fältet är tomt (null) som standard, så befintliga poster räknas som förut.
3. Databasen går till version 2 med Room-migration 1 till 2 (`ALTER TABLE ... ADD COLUMN`, nullable) och ett migrationstest. Inga befintliga rader ändras. Säkerhetskopian ska skriva och läsa fältet; en fil utan fältet ska fortfarande kunna importeras.
4. Formuläret: fältet visas för typerna reparation, underhåll och ny-/till-/ombyggnad, med hjälptext på svenska och engelska (strängar i `strings.xml`). Avdraget får inte vara större än beloppet.
5. Stämmer regeln inte, eller kan den inte bekräftas: bygg inget, skriv utfallet i `husloggen/APP.md`, `BESLUTSLOGG.md` och under "Frågor till Claude" i PR-beskrivningen.

## Klart när

- Källan för regeln står i `husloggen/APP.md`.
- Migrationstest 1 till 2 går igenom, och befintliga tester för sammanställning, PDF, CSV och säkerhetskopia går igenom.
- Nytt test: en post på 10 000 kr med avdrag 3 000 kr ger 7 000 kr i sammanställningen; en post utan avdrag ger oförändrat belopp; import av en säkerhetskopia utan fältet fungerar.
- Bygget och alla tester är gröna för `:karna`, `:garantivalvet` och `:husloggen`.

## Gör inte

- Rör inte Garantivalvets kod.
- Inga nya beroenden eller behörigheter, inget nätverk.
- Ge ingen skatterådgivning i texterna; skriv information och hänvisa till Skatteverket.
- Skriv inte om befintliga uppdrag. Dela upp det som passar den lokala modellen i uppgifter enligt `uppgifter/MALL.md` och skriv frågor under "Frågor till Claude" i PR-beskrivningen.
