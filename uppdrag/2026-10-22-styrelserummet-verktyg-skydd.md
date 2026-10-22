# Uppdrag: Styrelserummet, skydd och sökbarhet för gratisverktygen Nyckeltal och Fondplan

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

PR #490 (mergad 2026-10-22) la till de fria verktygen `/nyckeltal` och `/fondplan` med `POST /api/nyckeltal` och `POST /api/fondplan` i `styrelserummet-server`. De är öppna utan uppladdning och lagring, och ska locka sökträffar till tjänsten. Fyra saker återstår innan de kan vara publika:

1. Verktygsrutterna har ingen gräns per klient. Gränsen på 5 jobb per IP och timme gäller bara handlingsjobben.
2. Verktygen saknar en sidkarta och metadata så att sökmotorer hittar dem.
3. `/integritet` nämner inte verktygen, så texten stämmer inte om vad som sparas.
4. Det finns ingen gräns för storleken på förfrågan (antal åtgärder är begränsat i logiken, men kroppens byte-storlek är det inte).

## Uppgift

1. Gräns per klient för `/api/nyckeltal` och `/api/fondplan`: högst 60 anrop per IP och timme, samma sätt att läsa klientadressen (sista adressen i `X-Forwarded-For`) och samma mekanism som den befintliga gränsen om den går att återanvända. Överskriden gräns ger HTTP 429 med ett svenskt felmeddelande i samma form som övriga fel (`{"fel": ...}`). Ingen IP-adress sparas längre än i minnet och ingen loggas.
2. Gräns för förfrågans storlek: högst 64 kB i kroppen för de två rutterna. Större ger HTTP 413 och svenskt felmeddelande.
3. `GET /robots.txt` och `GET /sitemap.xml` med adresserna `/`, `/nyckeltal`, `/fondplan` och `/integritet`. Basadressen läses ur miljövariabeln som redan används för offentlig adress; finns ingen sådan, använd relativa adresser i robots.txt och utelämna sitemap.xml (404). Verifiera mot `DRIFT.md` vilken variabel som gäller.
4. `/integritet`: lägg till ett kort stycke om att verktygen räknar i minnet, inte sparar något, inte använder AI och inte sätter cookies. Ändra inget annat i texten.
5. Varje verktygssida får `<link rel="canonical">` och Open Graph-taggarna `og:title`, `og:description` och `og:type` med samma text som sidans `title` och `description`. Ingen extern resurs.

## Klart när

- Servertester (Ktor test-host): 61:a anropet inom en timme från samma klient ger 429 och det exakta felmeddelandet; en annan klientadress påverkas inte; en kropp över 64 kB ger 413; `/robots.txt` och `/sitemap.xml` ger det förväntade innehållet; `/integritet` innehåller det nya stycket.
- Befintliga tester för verktygen och för handlingsjobben går fortfarande igenom.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna.
- Cursor delar upp arbetet i små uppgifter för den lokala modellen enligt MALL.md (punkt 1 och 2 kan ligga hos Cursor om de rör fler än tre filer). Frågor till Claude står i PR-beskrivningen.

## Gör inte

- Lägga till beroenden, persistens, cookies, analys eller loggning av indata eller adresser.
- Ändra `K3.md`, modellerna, `nyttjandeperioder.csv`, beräkningarna i `regler/` eller betalflödet.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
