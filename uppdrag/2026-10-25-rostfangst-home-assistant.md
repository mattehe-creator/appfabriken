# Uppdrag: Röstfångst, snabbstart med en knapp och koppling till Home Assistant

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och rostfangst/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Mattias provar Röstfångst ("Säg det") som sitt eget verktyg. Önskemål 2026-10-25: trycka på en knapp i menyn (snabbinställningarna) och prata in, utan väckningsord ("Hey Siri", "Hey Google"), och få fångsten vidare till hans Home Assistant (HA), både som mottagare (att-göra, händelser) och för att styra hemmet med rösten. Klockan kommer i ett eget uppdrag efter detta (`2026-10-25-rostfangst-klocka`).

Appen finns redan med snabbinställningsruta (`FangstTileService`), appgenväg och en stor knapp. Delar av detta uppdrag verifierar att det beter sig som önskat och lägger till HA.

## Beslut (Claude, 2026-10-25)

- **Ingen väckningsfras.** Lyssningen startar bara av ett tryck: rutan i snabbinställningarna, appgenvägen eller knappen i appen. Ingen bakgrundslyssning, ingen `RECORD_AUDIO` i bakgrunden.
- **Snabbstart:** ett tryck på rutan (på låst skärm kräver Android upplåsning, det är OK) startar direkt inspelning utan att användaren ska trycka igen i appen. Rutan öppnar en lätt aktivitet som lyssnar med en gång (`TileService.startActivityAndCollapse`), visar texten medan den kommer, och avslutar sig själv efter utförd handling eller avbryt. Verifiera och rätta det som inte beter sig så i dag.
- **Home Assistant:** appens inställningar får `HA-adress` (till exempel `https://min.hemma.example` eller `http://192.168.1.10:8123`) och `HA-token` (long-lived access token som Mattias skapar i HA). Token sparas som API-nyckeln i dag (DataStore) och skrivs aldrig i repot, loggar eller felmeddelanden. Appen är en provapp och publiceras inte; `usesCleartextTraffic` får därför slås på för `:rostfangst` så att HA på lokalt nät över http fungerar. Det antecknas som avsteg i `rostfangst/APP.md`.
- **Tre vägar till HA, alla frivilliga, av om adress eller token saknas:**
  1. **Mottagare:** varje utförd fångst skickas som händelsen `rostfangst_fangst` via `POST /api/events/rostfangst_fangst` med JSON `{typ, titel, start, slut, plats, original}`. Då kan Mattias bygga automationer i HA hur han vill.
  2. **Att-göra:** typen ATT_GORA läggs dessutom i en HA-lista via `POST /api/services/todo/add_item` med `{entity_id, item}`; `entity_id` anges i inställningarna (till exempel `todo.att_gora`). Tomt `entity_id` betyder att steget hoppas över.
  3. **Styra hemmet:** ny typ **HEM** i tolkningen. Texten skickas oförändrad till HA:s samtalsfunktion (Assist) via `POST /api/conversation/process` med `{text, language: "sv"}` och HA:s svar visas i appen. Assist styr bara de enheter Mattias har valt att exponera i HA (Inställningar → Röstassistenter → Exponera). Appen har ingen egen lista över enheter.
- **Bekräftelse före styrning:** typen HEM utförs bara efter att Mattias trycker på knappen "Utför" i förslagsvyn, precis som övriga förslag. Ingen auto-utföring. Låsta enheter, larm och garageportar ska inte exponeras för Assist i HA; det står i `APP.md` som en rekommendation.
- **Tolkningen** (Anthropic, som i dag) får typen HEM i schemat: `typ` kan vara `KALENDER|PAMINNELSE|ATT_GORA|ANTECKNING|FRAGA|HEM`. Systemprompten säger att HEM är en uppmaning att styra en enhet i hemmet ("tänd lampan i köket", "sätt på värmen i sovrummet"). Allt annat är oförändrat. Ogiltig JSON eller okänd typ ger fortfarande ANTECKNING med originaltexten, och ingenting sparas eller skickas till HA vid ANTECKNING.
- **Ingen HA-trafik utan att användaren själv ställt in adress och token.** Inga anrop görs i test; transportlagret är utbytbart (som `Tolkare`).
- Inga nya beroenden: `HttpURLConnection` och `org.json` som i övrigt. Ljudet lämnar aldrig telefonen; HA får bara texten.
- Fel mot HA (nätfel, 401, 404, timeout) visas som svenska meddelanden i appen och stoppar inte fångsten: fångsten är sparad lokalt först, och HA-steget kan göras om från historiken ("Skicka igen").

## Del A (Cursor)

1. **Snabbstart**: gå igenom flödet från rutan till inspelning och rätta det så att ett tryck räcker. Skriv i PR-beskrivningen vad som redan fungerade.
2. **HA-klienten**: gränssnitt `HaKlient` med en implementation mot HA:s REST-API (`HaHttpKlient`) och utbytbart transportlager, tester mot en falsk transport. Inställningar (adress, token, todo-lista) i inställningsskärmen och DataStore.
3. **Flödet**: koppla in de tre vägarna enligt ovan. Historiken visar vilka steg som lyckats mot HA och en knapp "Skicka igen" vid fel.
4. **Typen HEM**: i `ForslagTyp`, i tolkningens JSON, i förslagsvyn ("Styr hemmet", knapp "Utför") och i `HandlingUtforare`.
5. Skriv `rostfangst/APP.md`: avstegen (HA, cleartext på provappen), rekommendationen om vad som inte exponeras, och hur Mattias skapar token (HA → Profil → Säkerhet → Long-lived access tokens).
6. **Dela upp resten i uppgifter** (Del B) för den lokala modellen.

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt. Högst 3 filer per uppgift och med test.

- **HA-adress**: `normaliseraHaAdress(text: String): String?` trimmar blanksteg och avslutande snedstreck, kräver `http://` eller `https://`, avvisar tomt, avvisar adress utan värdnamn. `"  http://192.168.1.10:8123/ "` ger `"http://192.168.1.10:8123"`; `"192.168.1.10"` ger `null`.
- **Händelsens JSON**: `bygghaHandelseJson(forslag, original: String): String` med nycklarna `typ, titel, start, slut, plats, original` (`null` blir JSON-`null`, citattecken och radbrytningar i `titel` och `original` kodas rätt).
- **Att-göra-JSON**: `byggTodoJson(entityId: String, titel: String): String?` ger `null` om `entityId` är tomt eller `titel` är tomt.
- **HA-fel**: `haFelText(status: Int?, undantag: String?): String` ger svenska texter: 401 "Token nekas, skapa en ny i Home Assistant.", 404 "Hittar inte Home Assistant på adressen.", timeout/nätfel "Når inte Home Assistant just nu.", 5xx "Home Assistant svarade med fel.", annars "Något gick fel mot Home Assistant." Token eller adress får aldrig förekomma i texten.
- **HEM i tolkningen**: `tolkaSvar` accepterar typen `HEM` (en uppgift, med test på giltigt HEM och på okänd typ som fortfarande ger ANTECKNING).
- **Systemprompten**: `byggSystemprompt` får en rad som förklarar HEM med exemplen ovan (test att raden finns och att övriga rader är oförändrade).
- **Assist-svaret**: `lasAssistSvar(json: String): String?` plockar ut talet ur HA:s svar (`response.speech.plain.speech`), `null` om det saknas eller JSON är ogiltig. Exempel i uppgiften skrivs ur HA:s dokumentation av `conversation/process`.
- **Kö att skicka igen**: `ejSkickade(poster: List<HaStatus>): List<Long>` ger id för poster där något HA-steg misslyckats och inte gjorts om.

## Klart när

- Bygget och testerna är gröna för alla moduler, och `senaste-main` får en APK för `:rostfangst`.
- Mattias kan trycka på rutan i snabbinställningarna, säga "påminn mig om att ringa tandläkaren imorgon klockan nio" och få en avisering imorgon 09:00 utan att trycka något mer i appen.
- Med HA-adress och token ifyllda: samma fångst syns som händelsen `rostfangst_fangst` i HA (Utvecklarverktyg → Händelser), och "lägg till mjölk på att-göra-listan" hamnar i den valda listan.
- "Tänd lampan i vardagsrummet" visas som "Styr hemmet" med knappen "Utför", och efter ett tryck visas HA:s svar.
- Inga nycklar, token eller verkliga anrop i repot eller testerna.

## Gör inte

- Lyssna i bakgrunden eller lägga till en väckningsfras.
- Spara eller skicka ljud, eller skicka något till HA utan att adress och token är ifyllda.
- Bygga en egen enhetslista eller egna kommandon för enheter (Assist i HA avgör).
- Lägga till beroenden.
- Ändra något i `garantivalvet/`, `husloggen/`, `flextid/`, `styrelserummet*/`, `prov-aviseringar/`, eller i `:karna` utöver att använda temat.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
