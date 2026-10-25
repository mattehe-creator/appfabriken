# Uppdrag: Röstfångst med egen dator, lokal modell (Ollama) och lokal taligenkänning (Whisper)

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och rostfangst/APP.md först och följ dem. Ändra inget utöver uppdraget.

Bygger på `2026-10-25-rostfangst-home-assistant`. Börja först när det uppdragets PR är mergad (båda ändrar tolkning, inställningar och flödet i `:rostfangst`). Är den inte mergad: vänta.

## Bakgrund

Mattias vill kunna köra Röstfångst mot sin egen dator, så att varken text eller ljud lämnar hans egna enheter: tolkningen av texten med en lokal modell via Ollama, och tal till text med en lokal Whisper-server. Dessutom ska hans Home Assistant kunna använda samma Ollama (det är HA:s egen inbyggda Ollama-integration, inget som ligger i Röstfångst). Trafiken går över hans privata nät (Tailscale/WireGuard) så att den är krypterad mellan enheterna. Anthropic förblir ett val i appen, och en reserv när datorn inte svarar.

## Beslut (Claude, 2026-10-25)

- **Nätet är Mattias sak, inte appens.** Tailscale installeras på datorn, telefonen och HA-maskinen av Mattias. Appen pratar bara med en adress han anger. Appen får aldrig kräva att Ollama exponeras mot internet.
- **Tolkningen**: ny implementation `OllamaTolkare` av gränssnittet `Tolkare`, mot `POST {adress}/api/chat` med `stream: false` och ett JSON-schema i fältet `format` (strukturerad utdata) som motsvarar tolkningens schema (inklusive typen HEM från HA-uppdraget). Modellnamnet anges i inställningarna och har inget standardvärde som appen antar finns; fältet förklarar hur man listar modeller (`ollama list`). Svaret går genom den befintliga `tolkaSvar`, så att ogiltig JSON fortsatt blir ANTECKNING med originaltexten.
- **Tal till text**: ny implementation `ServerRostLyssnare` som spelar in ljud med `AudioRecord` (16 kHz, mono, PCM 16-bit), kodar WAV och skickar det med `POST {adress}/v1/audio/transcriptions` (multipart, OpenAI-kompatibelt format, fält `file`, `model`, `language=sv`, `response_format=json`) till en Whisper-server på Mattias dator. Svaret är `{"text": "..."}`. Inspelning startar och stoppar med tryck (andra trycket stoppar), högst 60 sekunder. Ljudet sparas aldrig i telefonen utöver minnet under skickandet och skickas bara till adressen Mattias angett. Den befintliga taligenkänningen på telefonen (`SpeechRecognizer`) finns kvar som val.
- **Val i inställningarna**, var för sig: *Taligenkänning*: På telefonen | Min dator. *Tolkning*: Anthropic | Min dator | Min dator, annars Anthropic. Alla "Min dator"-val kräver att adressen är ifylld. Standard är dagens beteende.
- **Adressregel**: `https://` krävs. Undantag: `http://` accepteras för privata adresser (10.x, 172.16–31.x, 192.168.x, `localhost`, `*.local`) och visar en gul varning "Okrypterad anslutning på lokalt nät". Allt annat med `http://` avvisas med förklaring. Förslag i inställningstexten: `https://<datornamn>.<tailnet>.ts.net` via `tailscale serve`.
- **Reservlogik**: "Min dator, annars Anthropic" försöker datorn först med kort timeout (5 sekunder för anslutning, 60 för svar), och faller bara tillbaka på Anthropic om det är ett nät- eller timeoutfel, inte vid ogiltigt svar. Före reserven visar appen en tydlig rad "Datorn svarade inte, använder Anthropic" och loggar valet i historiken. Texten som skickas är densamma. Vid taligenkänning finns ingen molnreserv (ljud skickas aldrig till Anthropic eller Google); vid fel faller appen tillbaka på taligenkänningen på telefonen först efter att användaren tryckt "Använd telefonen istället".
- **Inga nycklar för Ollama eller Whisper** (de har ingen inloggning); skyddet är nätet. Om Mattias vill ha ett lösenord framför kan en omvänd proxy sätta ett huvud: ett valfritt fält `Bearer-token` i inställningarna skickas då som `Authorization: Bearer ...`. Sparas som övriga hemligheter i DataStore, skrivs aldrig i loggar eller repo.
- **Whisper-servern på Mattias dator** är verktyg på hans dator och ligger i `verktyg/whisper/` med ett startskript (`start-whisper.ps1`) och en kort README. Cursor väljer och **verifierar** en aktuell, underhållen server med OpenAI-kompatibelt `/v1/audio/transcriptions` (till exempel `faster-whisper`-baserad eller `whisper.cpp`:s server), skriver i PR-beskrivningen varför den valdes och vilka versioner som provats, och beskriver installation i README (Python-miljö eller binär, modellstorlek med hänsyn till att GPU:n delas med den lokala kodmodellen, standard `small` eller `medium` för svenska, körs på CPU eller GPU enligt val). Inget i servern får lyssna på något annat än `127.0.0.1`; `tailscale serve` lägger HTTPS framför.
- **Guide för Mattias**: `rostfangst/LOKAL-AI.md`, på svenska: (1) installera Tailscale på datorn, telefonen och HA-maskinen, (2) `ollama pull` av en modell och `ollama list`, (3) `tailscale serve` för Ollama (port 11434) och för Whisper-servern, med de kommandon Cursor har verifierat, (4) ställa in adresserna i appen, (5) i Home Assistant lägga till Ollama-integrationen och välja den som samtalsagent för Assist (HA → Inställningar → Enheter och tjänster → Lägg till integration → Ollama), (6) felsökning (datorn i vila, Android tillåter en VPN åt gången, GPU delas med kodkörningen). Cursor skriver bara kommandon den har kontrollerat mot aktuell dokumentation, och skriver "ej verifierat" annars.
- Inga nya beroenden i appen: `HttpURLConnection`, `org.json` och `AudioRecord` (plattformen). Inget av detta ändrar hur ljud hanteras på klockan (klockuppdraget).
- Appen är en provapp och publiceras inte. Avsteget från "ljud lämnar aldrig telefonen" är medvetet och begränsat till adressen Mattias själv anger; det antecknas i `rostfangst/APP.md`.

## Del A (Cursor)

1. `OllamaTolkare` med utbytbart transportlager och tester mot en falsk transport (som `AnthropicTolkare`).
2. `ServerRostLyssnare` med `AudioRecord`, WAV-kodning, multipart-uppladdning, tester mot en falsk transport.
3. Inställningar och val enligt ovan, adressreglerna och varningarna, reservlogiken och historikraden.
4. `verktyg/whisper/` (skript och README) och `rostfangst/LOKAL-AI.md`.
5. Uppdatera `rostfangst/APP.md`.
6. **Dela upp resten i uppgifter** (Del B) för den lokala modellen.

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt. Högst 3 filer per uppgift och med test.

- **Adressbedömning**: `bedomAdress(text: String): AdressBedomning` med fallen `Ogiltig(skäl)`, `Krypterad`, `OkrypteradLokal`, `Avvisad(skäl)` enligt adressregeln. `https://dator.tail1234.ts.net` är `Krypterad`; `http://192.168.1.10:11434` är `OkrypteradLokal`; `http://example.com` är `Avvisad`; `192.168.1.10` är `Ogiltig`; `http://172.32.0.1` är `Avvisad` (utanför 172.16–31).
- **WAV-kodning**: `pcmTillWav(pcm: ByteArray, samplingsfrekvens: Int, kanaler: Int): ByteArray` med korrekt 44-byte RIFF-huvud. Test på längdfälten för 1 sekund 16 kHz mono (32 000 byte nyttolast) och på tomt PCM.
- **Multipart**: `byggMultipart(grans: String, falt: List<Pair<String, String>>, filnamn: String, filtyp: String, fil: ByteArray): ByteArray` enligt RFC 7578, med exakta radbrytningar (CRLF). Test på en liten ingång byte för byte.
- **Transkriptionssvar**: `lasTranskription(json: String): String?` plockar `text`, trimmar, `null` om tom eller ogiltig JSON.
- **Ollama-förfrågan**: `byggOllamaChat(modell: String, systemprompt: String, text: String): String` ger JSON med `model`, `stream: false`, `messages` (system och user) och `format` med schemat. Test på att `format` innehåller alla sex typerna och att citattecken och radbrytningar i `text` kodas rätt.
- **Ollama-svar**: `lasOllamaSvar(json: String): String?` ger `message.content` eller `null`.
- **Valet av tolkare**: `valjTolkning(lage: TolkningsLage, lokalFel: LokalFel?): Val` där `LokalFel` är `Natfel`, `Timeout`, `OgiltigtSvar` eller `null`; "Min dator, annars Anthropic" faller bara tillbaka på `Natfel` och `Timeout`. Alla kombinationer testas.
- **Fel**: `lokalFelText(fel: LokalFel, kalla: Kalla): String` med svenska texter för tolkning och taligenkänning, utan adress eller token i texten.
- **Inspelningsgräns**: `ForeSlut(ms: Long, maxMs: Long): Boolean` och `formateraLangd(ms: Long): String` (`0:07`, `1:00`).
- **Modellnamn**: `giltigtModellnamn(text: String): Boolean` (tomt eller blanksteg nej, `qwen3:8b` och `llama3.1:8b-instruct-q4_K_M` ja, mellanslag mitt i nej).

## Klart när

- Bygget och testerna är gröna för alla moduler, och `senaste-main` får en APK för `:rostfangst`.
- Med Ollama och Whisper igång på Mattias dator och nåbara via hans Tailscale-nät kan han trycka på rutan, tala, och få texten tolkad lokalt utan att något når Anthropic eller Google.
- Stänger han av datorn visar appen ett tydligt fel; med valet "Min dator, annars Anthropic" tolkas texten av Anthropic och historiken visar det.
- `rostfangst/LOKAL-AI.md` och README för Whisper räcker för att Mattias ska kunna sätta upp det själv.
- Inga adresser, token eller verkliga anrop i repot eller testerna.

## Gör inte

- Skicka ljud till något annat än adressen Mattias angett, eller till Anthropic eller Google.
- Bygga in en moln-Whisper eller lyssna i bakgrunden.
- Öppna Ollama eller Whisper mot internet, eller ställa in något på Mattias dator utöver `verktyg/whisper/`.
- Lägga till beroenden i appen.
- Ändra `garantivalvet/`, `husloggen/`, `flextid/`, `styrelserummet*/`, `prov-aviseringar/`, eller `:karna` utöver att använda temat.
- Ändra `.github/workflows/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
