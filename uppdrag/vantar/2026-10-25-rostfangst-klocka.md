# Uppdrag: Röstfångst på klockan (Wear OS)

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och rostfangst/APP.md först och följ dem. Ändra inget utöver uppdraget.

Bygger på `2026-10-25-rostfangst-home-assistant`. Börja först när det uppdragets PR är mergad (båda ändrar `:rostfangst`). Är den inte mergad: vänta.

## Bakgrund

Mattias vill kunna säga vad han behöver direkt på klockan (Wear OS, till exempel Pixel Watch eller Galaxy Watch), utan väckningsord, och få det behandlat som en fångst i telefonappen: tolkas, utförs efter bekräftelse (kalender, påminnelse, att-göra, anteckning, fråga, styr hemmet) och skickas vidare till Home Assistant om det är inställt.

## Beslut (Claude, 2026-10-25)

- **Ny modul `:rostfangst-wear`**, Wear OS-app, `namespace` och `applicationId` `se.tmconnect.rostfangst` (samma som telefonen, krav för Wear-kopplingen), minSdk 30, Compose för Wear OS. Telefonappens modul är `:rostfangst`.
- **Nya beroenden godkända** (bara för klockan och mottagaren i telefonen): `androidx.wear.compose:compose-material`, `androidx.wear.compose:compose-foundation`, `androidx.wear:wear`, `androidx.activity:activity-compose` (finns i portföljen) och `com.google.android.gms:play-services-wearable` (Data Layer, i både `:rostfangst-wear` och `:rostfangst`). Versioner väljer Cursor ur Googles aktuella stabila utgåvor och skriver i PR-beskrivningen. Inget annat.
- **Flödet:** klockan lyssnar med Androids taligenkänning (`RecognizerIntent.ACTION_RECOGNIZE_SPEECH`, `sv-SE`), visar texten, och skickar **bara texten** till telefonen via Data Layer (`MessageClient`, sökväg `/rostfangst/fangst`, nyttolast UTF-8-text). Telefonens `FangstMottagare` (`WearableListenerService`) tar emot texten och kör samma flöde som när man talar i telefonen: sparas lokalt före tolkning, tolkas, förslaget visas som avisering på telefonen och på klockan ("Påminnelse: ring tandläkaren i morgon 09:00" med knappen Utför). Utförande sker på telefonen. Inget ljud skickas mellan klocka och telefon.
- **Integritet, ärligt:** på telefonen används på-enheten-igenkänning när den finns. Wear OS-klockan kan använda Googles taligenkänning som är molnbaserad på vissa klockor. Det ska stå i `APP.md` och i en rad i klockappens förstasida ("Tal kan behandlas av Google"). Telefonens regel "ljud lämnar aldrig telefonen" gäller inte klockan. Mattias avgör själv om han vill använda klockan.
- **Ingångar på klockan** (minst en, helst alla tre i den här ordningen): (1) appen öppnas och börjar lyssna direkt, en stor mikrofonknapp som reserv; (2) en **Wear Tile** (Mattias lägger den i klockans panel; `androidx.wear.tiles` och `androidx.wear.protolayout` är då godkända) som startar appen; (3) hårdvaruknapp: Mattias kopplar en knapp till appen i klockans inställningar, det kräver ingen kod men ska stå i `APP.md`.
- **Utan koppling:** ingen telefon i närheten ger tydligt fel på klockan och fångsten sparas inte där. Ingen egen lagring på klockan i första versionen.
- Alla texter svenska och engelska. Inga hemligheter på klockan: API-nyckel och HA-token finns bara i telefonen.

## Del A (Cursor)

1. Modulen `:rostfangst-wear` (build, manifest, tema, ikon, strängar), `settings.gradle.kts`, versionsrad, och `APP.md`-avsnittet om klockan (inklusive integritetsraden och hårdvaruknappen).
2. Klockappen: lyssna, visa text, skicka, visa kvittens ("Skickat till telefonen") eller fel.
3. `FangstMottagare` i `:rostfangst`: tar emot, kontrollerar sökväg och längd, matar in i samma flöde som `RostfangstViewModel` använder (utan att dubblera logiken).
4. Svar tillbaka: telefonen skickar förslaget som kort text till klockan (`/rostfangst/forslag`, JSON med `typ`, `rubrik`, `id`) och klockan visar det med knappen "Utför" som skickar `/rostfangst/utfor` med `id`. Telefonen utför och svarar `/rostfangst/klart`.
5. Tile enligt ovan.
6. Byggfilerna i GitHub Actions (`bygg.yml` ändrar Claude, inte Cursor): Cursor beskriver i PR-beskrivningen vad som behövs för att `senaste-main` ska få en klock-APK, så gör Claude ändringen.
7. **Dela upp resten i uppgifter** (Del B) för den lokala modellen.

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt. Högst 3 filer per uppgift och med test. Alla som rena funktioner i `:rostfangst` eller i ett delat paket som båda modulerna kan använda utan Android.

- **Meddelandenyttolast**: `kodaFangst(text: String): ByteArray` och `avkodaFangst(data: ByteArray): String?` (UTF-8; `null` om tom, längre än 2000 tecken, eller innehåller bara blanksteg; trimmar).
- **Förslagsmeddelande**: `kodaForslag(id: Long, typ: String, rubrik: String): ByteArray` och `avkodaForslag(data): ForslagMeddelande?` (JSON; `null` vid ogiltigt innehåll; rubrik begränsad till 120 tecken, avkortad med `…`).
- **Åtgärdsmeddelande**: `kodaUtfor(id: Long)` och `avkodaUtfor(data): Long?`.
- **Sökvägar**: konstanter och en funktion `arKandSokvag(s: String): Boolean`, test att okända sökvägar avvisas.
- **Kvittenstexter**: `kvittensText(tillstand: Overforing): String` för klockan (väntar, skickad, ingen telefon, fel) på svenska.
- **Klockans förslagsrubrik**: `klockRubrik(typ: String, titel: String, start: Instant?, zon: ZoneId): String` kort nog för en liten skärm (högst 40 tecken), till exempel `Påminnelse: ring tandläkaren, i morgon 09:00`. Exempel för varje typ.

## Klart när

- Bygget och testerna är gröna för alla moduler, och `senaste-main` får både telefon-APK och klock-APK.
- Mattias kan öppna appen på klockan, säga "påminn mig om att ringa tandläkaren imorgon klockan nio", se förslaget på klockan, trycka Utför, och få avisering imorgon 09:00 på telefonen.
- Utan telefon i närheten visar klockan ett tydligt fel.
- Inga nycklar, token eller riktiga anrop i repot eller testerna.

## Gör inte

- Skicka ljud mellan klocka och telefon, eller lägga API-nyckel eller HA-token på klockan.
- Lyssna i bakgrunden eller lägga till en väckningsfras.
- Lägga till andra beroenden än de som står under Beslut.
- Ändra `garantivalvet/`, `husloggen/`, `flextid/`, `styrelserummet*/`, `prov-aviseringar/`, eller `:karna` utöver att använda temat.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
