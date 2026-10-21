# Uppdrag: Styrelserummet, provklar för första riktiga körning

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Kärnan, underlaget, servern, robusthet, kvalitet, gränsfall och felhantering är mergade (över 240 klara uppgifter). Det som saknas är bevis att hela flödet håller för handlingar som liknar de riktiga, och att servern säger ifrån tydligt om den startas fel. Mattias provkör snart med en riktig API-nyckel och riktiga handlingar. Allt här är rena funktioner och tester, alltså arbete för den lokala modellen.

## Beslut (Claude, 2026-10-21)

- **Cursor skriver ingen implementation.** Cursor delar upp i uppgifter (minst 14, högst 20) enligt MALL.md och CLAUDE.md punkt 4 och lämnar all kod åt den lokala modellen. Byggfiler eller ändringar i fler än 3 filer får Cursor ta själv och säger då varför i PR-beskrivningen.
- Uppgifterna är oberoende av varandra (`beror-på: ingen`) där det går; annars exakt filnamn.
- Inga nya beroenden. Inga riktiga handlingar, nycklar eller personuppgifter i repot: alla testhandlingar är **påhittade och genereras i testet** (PDFBox finns redan som beroende).
- Ändra inte `Komponent`, `Forening`, `nyttjandeperioder.csv`, och inte beteendet hos befintliga funktioner utom där en uppgift uttryckligen säger det.

## Underlag (Cursor delar upp efter dessa områden)

1. **Start- och miljökontroll** (`server/Miljo.kt`): `fun kontrolleraMiljo(env: Map<String, String?>): List<String>` som returnerar svenska varningar och fel (tom lista när allt är bra): saknad `STYRELSERUMMET_DATA`, katalog som inte går att skriva till (tas som parameter `kanSkriva: (String) -> Boolean`), saknad `ANTHROPIC_API_KEY`, nyckel som inte börjar med `sk-ant-`, ogiltigt `PORT` (inte ett heltal 1-65535), `STYRELSERUMMET_PROXY` med annat värde än `1` eller tomt, okänd `STYRELSERUMMET_MODELL` (varning, tom sträng är fel). Nyckelvärdet får **aldrig** återges i en text. En uppgift per tre-fyra kontroller. Därefter en uppgift som anropar den vid serverstart och skriver varningarna på `System.err` utan nyckel (högst 3 filer).
2. **Kontrast** (`regler/Kontrast.kt` i `:styrelserummet-server`, test läser `gemensam.css` ur resurserna): `fun kontrastforhallande(fg: String, bg: String): Double` (hex `#rgb` och `#rrggbb`, WCAG 2.x relativ luminans) och ett test som läser färgvariablerna i ljust och mörkt läge ur `gemensam.css` och kräver minst 4,5 för text mot bakgrund och 3,0 för knappkant. Hittas inte en variabel ska testet säga vilken som saknas. Exempel: `#000000` mot `#ffffff` ger 21,0; `#777777` mot `#ffffff` ger cirka 4,48.
3. **Syntetiska handlingar, hela flödet** (`styrelserummet/src/test/.../underlag/`): minst fem olika påhittade underhållsplans-layouter som PDF genererad i testet med PDFBox och en inspelad AI-respons per layout (JSON enligt tolkningsschemat) som en falsk `Tolkare` returnerar: (a) tabell med kolumnerna Åtgärd, År, Kostnad; (b) löpande text med belopp i tkr; (c) två sidor där andra sidan har fortsättningen; (d) belopp med hårda mellanslag och punkt som tusentalsavgränsare; (e) en plan där kostnad saknas för en post. Varje test kör `byggUtkast` och kontrollerar komponenternas antal, `sakerhet` per rad, avstämning och att anmärkning finns för rader med `LAG` eller `UPPSKATTNING`. Samma för en påhittad årsredovisning med balansräkning (två kolumner, innevarande år först) som ger rätt byggnadsvärde. En uppgift per layout.
4. **Lagring** (`server/JobbLagring.kt` med tester i `JobbLagringTest.kt`): korrupt `meta.json` (ogiltig JSON, tom fil, halv fil) får inte krascha rensning eller listning; jobbet markeras som skadat och hoppas över med en loggrad utan jobb-id; samtidig skrivning av två raduppdateringar till samma jobb ger en giltig fil (ingen halvskriven); jobbkatalog som saknas mitt i en körning ger ett felsvar, inte undantag; stora filnamn och icke-ASCII i filnamn sparas under säkert namn. En uppgift per fall, och rättningar i `JobbLagring.kt` bara om testet visar fel.
5. **Dataminimering** (test): efter ett lyckat jobb finns inga andra filer i jobbkatalogen än de uppladdade PDF:erna, `meta.json` och resultatet; den maskade texten och den ursprungliga texten sparas inte; efter radering finns katalogen inte kvar. Uppgifterna skriver testerna och rättar bara om de visar fel.
6. **Kostnadsskydd** (`server/Granser.kt`): `fun skattaTecken(sidor: List<String>): Int` och en gräns `MAX_TECKEN_TILL_TOLKNING` (förslag 400 000 tecken för ett jobb) som ger felkoden `FOR_STOR_HANDLING` med ett vänligt meddelande i `Felmeddelanden`; uppgiften beskriver var kontrollen sitter (före anropet till `Tolkare`) och testar att `Tolkare` inte anropas över gränsen.

## Klart när

- Alla uppgifter är skrivna i `uppgifter/ny/` med exempel och test, och PR:en med dem är mergad.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna när uppgifterna har gjorts.
- Inga riktiga handlingar, nycklar eller personuppgifter i repot.

## Gör inte

- Skriva implementationen själv.
- Lägga till beroenden eller ändra modeller eller `nyttjandeperioder.csv`.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
