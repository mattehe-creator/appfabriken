# Uppdrag: Styrelserummet, läsa underhållsplan och årsredovisning

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

Bygger på `uppdrag/2026-10-19-styrelserummet-k3-karna.md`. Börja först när modulen `:styrelserummet` och datamodellen från det uppdraget finns på main. Om PR:en ännu inte är mergad: vänta, bygg inte en egen version av modellen.

## Bakgrund

Föreningen laddar upp underhållsplanen och senaste årsredovisningen som PDF. Tjänsten ska ur dem ta fram ett förslag till komponentlista (modellen `Komponent` från räknekärnan) som räknekärnan sedan kontrollerar och summerar. AI gör läsningen, kärnan gör räkningen och kontrollen. AI:n får aldrig själv räkna ut avskrivningar eller summor.

## Beslut (Claude, 2026-10-19)

- Nya beroenden godkända för `:styrelserummet`: Apache PDFBox (PDF till text) och `kotlinx-serialization-json`. HTTP med `java.net.http.HttpClient` (JDK 17), inget annat bibliotek.
- Tolkningen går via ett gränssnitt `Tolkare` med en implementation mot Anthropics Messages API, med utbytbart transportlager så att testerna aldrig gör riktiga anrop. Modell och nyckel kommer från miljövariablerna `ANTHROPIC_API_KEY` och `STYRELSERUMMET_MODELL`. Standardmodell `claude-sonnet-5-5`. Nyckeln står aldrig i repot, loggar eller tester.
- Bara text skickas till tolkningen, aldrig själva PDF-filen. Personuppgifter maskas innan texten lämnar servern: personnummer, e-postadresser och telefonnummer. Namn på ledamöter och boende behövs inte för komponentlistan, och prompten ber modellen att inte återge dem.
- Skannade PDF:er utan text läses inte (ingen OCR i första versionen). Tjänsten säger tydligt "Filen innehåller ingen läsbar text, ladda upp en PDF som går att markera text i" och tar inte betalt för det.
- Varje föreslagen komponent har `kalla` (fil och sida) och `sakerhet`. Saknar underhållsplanen kostnad för en komponent fördelar kärnan byggnadens värde enligt standardtabellen och märker raden `UPPSKATTNING`. Tolkningen får inte hitta på siffror: osäkert värde blir `LAG` och en tydlig anmärkning.
- Tjänsten ger ett utkast. Det står i allt som visas och exporteras.

## Del A (Cursor)

1. `Tolkare`-gränssnittet, Anthropic-implementationen och transportlagret, med tester mot en falsk transport.
2. PDF till text per sida (`PdfLasare`), med test på små PDF-filer som genereras i testet. Inga riktiga föreningshandlingar i repot.
3. Flödet `byggUtkast(underhallsplanText, arsredovisningText, forening): Utkast` som: maskar personuppgifter, plockar ur årsredovisningen byggnadens redovisade värde, ackumulerade avskrivningar, byggnadsår och nuvarande avskrivningsprincip, ber `Tolkare` föreslå komponenter ur underhållsplanen (en anrop per del om texten är lång), validerar och räknar med kärnan, fördelar återstående värde enligt tabellen och sammanställer `Utkast` (komponenter, avstämning mot byggnadens värde, anmärkningar).
4. Promptarna ligger som textfiler i `styrelserummet/src/main/resources/prompter/`, inte som strängar i koden. De ber om JSON enligt schemat nedan och om svenska anmärkningar.

## Tolkningens schema

```json
{"komponenter": [
  {"namn": "text", "grupp": "STOMME|FASAD|TAK|FONSTER_DORRAR|STAMMAR|EL|VARME|VENTILATION|HISS|TVATTSTUGA|MARK_OCH_GARAGE|OVRIGT",
   "anskaffningsvarde": "heltal kronor eller null",
   "anskaffningsar": "heltal eller null",
   "nyttjandeperiodAr": "heltal eller null",
   "kalla": "fil, sida",
   "sakerhet": "HOG|MEDEL|LAG"}],
 "anmarkningar": ["text"]}
```

Ogiltig JSON, okänd grupp eller värden som inte är heltal ger ingen krasch: raden tas med som `LAG` och en anmärkning, eller hoppas över med en anmärkning om inget går att rädda.

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt. Högst 3 filer per uppgift och med test.

- **Maska personuppgifter**: `maskaPersonuppgifter(text: String): String`. Personnummer (`ååmmdd-xxxx`, `ååååmmdd-xxxx`, med eller utan bindestreck) blir `[personnummer]`, e-post blir `[e-post]`, svenska telefonnummer (`070-123 45 67`, `+46 70 123 45 67`, `08-123 456`) blir `[telefon]`. Test att org.nr `769612-3456` (bostadsrättsförening) och årtal och belopp inte maskas.
- **Dela upp text**: `delaUpp(sidor: List<String>, maxTecken: Int): List<Del>` som delar på sidgräns och aldrig mitt i en sida om sidan ryms. Varje `Del` vet vilka sidor den innehåller.
- **Tolka svar**: `tolkaForslag(json: String, filnamn: String): ForslagResultat` enligt schemat ovan, med reglerna för ogiltiga värden.
- **Hitta belopp i årsredovisningen**: `hittaByggnadensVarde(text: String): Belopp?` för raderna "Byggnader och mark" eller "Byggnader" i balansräkningen (tusen kronor eller kronor, med mellanrum som tusentalsavgränsare). Exempel i uppgiften. Hittas inget: null.
- **Anmärkningar**: `anmarkningarFor(utkast)` som lägger till svenska anmärkningar: differens mot byggnadens värde, rader med `LAG` eller `UPPSKATTNING`, komponent med nyttjandeperiod utanför tabellens intervall.

## Klart när

- `gradle :styrelserummet:test` och övriga moduler är gröna.
- Med en falsk transport som returnerar ett inspelat svar ger `byggUtkast` ett `Utkast` där summa, avstämning och anmärkningar stämmer.
- Ingen nyckel, inga riktiga anrop och inga riktiga handlingar i repot.

## Gör inte

- Låta modellen räkna avskrivningar eller summor.
- Skicka PDF-filer eller personuppgifter till tolkningen.
- Lägga till en server, ett användargränssnitt eller hosting (nästa uppdrag).
- Ändra andra moduler, `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`.
