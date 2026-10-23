# Uppdrag: Styrelserummet, sida för stämmotidplan och avgifter vid överlåtelse

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Uppdraget `2026-10-22-styrelserummet-stammotidplan` är genomfört i funktionerna: stämmotidplan, helgdagar och närmaste arbetsdag, överlåtelse- och pantsättningsavgift, datafilen `stamma.csv` med läsare samt utskrift och CSV (`tidplanRader`, `tidplanCsv`). Punkt 6 i det uppdraget, sidskalet, återstår. Funktionerna syns inte för användaren förrän sidan finns.

## Uppgift

Cursor gör sidskalet själv (HTML, JS och serverrutter är inte uppgifter för den lokala modellen):

1. `styrelserummet-server/src/main/resources/static/stamma.html` och `stamma.js` i samma formspråk som `avgift.html` och `avgift.js`, utan externa skript. Inmatning: räkenskapsårets slut, önskat stämmodatum, stadgarnas kallelsefrist (veckor, från och till). Resultat som tabell med datum på formatet `2026-05-31 (söndag)`, anmärkning och källa per rad. Knapp för CSV (`tidplanCsv`). Sista raden visar alltid "Allmän information, inte juridisk rådgivning. Kontrollera era stadgar."
2. Avdelning på samma sida för överlåtelse- och pantsättningsavgift: inmatning av avgift och år, prisbasbelopp hämtas ur `stamma.csv` via `prisbasbeloppFor`, resultat visar högsta tillåtna avgift och ett felmeddelande på klarspråk när avgiften överstiger gränsen. Saknas året i datafilen visas det, inte ett gissat belopp.
3. Serverrutter eller anrop som behövs för att koppla sidan till de rena funktionerna, med test i serverns testmodul.
4. Länk från `index.html` och i sidkartan. Samma skydd, metadata och SEO som övriga gratisverktyg (se `2026-10-22-styrelserummet-verktyg-skydd.md`).

Använd de befintliga funktionerna. Skriv inte om deras logik.

## Klart när

- Sidan fungerar i en körning av servern med påhittade värden, och `gradle :styrelserummet:test :styrelserummet-server:test` är gröna.
- Ingen ny behörighet, inget nytt beroende, inga externa skript och inga personuppgifter.
- Rutten är med i sidkartan och i skyddet mot överbelastning som övriga gratisverktyg.

## Gör inte

- Ändra reglerna i `regler/`, `stamma.csv`, modellerna eller de befintliga verktygen.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
