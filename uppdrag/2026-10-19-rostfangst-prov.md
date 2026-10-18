# Uppdrag: Röstfångst, minsta version för Mattias (test)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Idéurval 2 (IDEURVAL.md, 2026-10-18): Mattias vill kunna säga vad han behöver i stunden och få det gjort. Påminnelse, kalenderhändelse, att göra, en anteckning, eller en fråga med svar. Provet: Mattias använder appen varje dag i 30 dagar. Används den mindre än en gång om dagen efter två veckor är idén svag. Appen publiceras inte i det här steget.

## Beslut (Claude, 2026-10-19)

- Ny modul `:rostfangst`, `namespace` och `applicationId` `se.tmconnect.rostfangst`, appnamn "Säg det". Samma versioner som `husloggen/build.gradle.kts`. `:karna` för temat. Room, DataStore och WorkManager (finns redan i portföljen). Inga andra beroenden; HTTP görs med `HttpURLConnection` och JSON med `org.json`.
- Regler i `rostfangst/APP.md` (skriv den): provapp, publiceras inte nu. Avsteg från CLAUDE.md punkt 5: appen får `INTERNET` för tolkningen. Ljud lämnar aldrig telefonen; bara den transkriberade texten skickas till tolkningen.
- Tal till text: Androids `SpeechRecognizer` med `sv-SE`. På enheten när det finns (`SpeechRecognizer.isOnDeviceRecognitionAvailable`, API 31+, annars vanlig `createSpeechRecognizer`). `RECORD_AUDIO` frågas första gången.
- Tolkning: ett gränssnitt `Tolkare` med en implementation mot Anthropics Messages API. Modell och API-nyckel anges av Mattias i appens inställningar och sparas i DataStore, aldrig i repot. Standardmodell `claude-haiku-5-5`. Tolkningen returnerar JSON enligt ett schema (nedan). Gränssnittet ska göra det lätt att senare byta till en lokal modell.
- Handlingar utan extra behörigheter: kalenderhändelse via `Intent.ACTION_INSERT` mot `CalendarContract.Events` (Mattias bekräftar i kalendern); påminnelse som egen avisering vid rätt tid via WorkManager; att göra och anteckning i appens egen lista; fråga visas med svaret i appen.

## Tolkningens schema

```json
{"typ": "KALENDER|PAMINNELSE|ATT_GORA|ANTECKNING|FRAGA",
 "titel": "kort text",
 "start": "ISO-8601 eller null", "slut": "ISO-8601 eller null",
 "plats": "text eller null",
 "svar": "text eller null (bara för FRAGA)"}
```

Systemprompten innehåller dagens datum, veckodag och tidszon (Europe/Stockholm) och ber om svenska svar och bara JSON. Ogiltig JSON ger typ ANTECKNING med hela texten, så att inget tappas.

## Del A (Cursor)

1. Modulen, manifest, ikon, strängar på svenska och engelska, `APP.md`.
2. Ingångar: en stor knapp på startsidan, en snabbinställningsruta (`TileService`) och en genväg från appikonen. Alla startar inspelning direkt.
3. Flödet: lyssna, visa texten, tolka, visa förslaget med en knapp för att utföra och en för att ändra typ eller text. Texten sparas lokalt **innan** tolkningen, så att inget försvinner om nätet saknas. Saknas nät: sparas som ANTECKNING och kan tolkas om senare.
4. Listor: "Att göra" (bocka av), "Anteckningar", "Historik" (allt som sagts, med typ och tid).
5. Inställningar: API-nyckel, modell, och en rad som visar antal fångster per dag de senaste 30 dagarna (det är provets mått). Export av historiken som CSV via filväljaren.
6. `Tolkare` mot Anthropic med tester mot en falsk HTTP-server eller ett utbytbart transportlager. Inga riktiga nätanrop i testerna.

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt. Högst 3 filer och med test.

- **Tolka JSON**: `tolkaSvar(json: String, originaltext: String): Forslag`. Giltig JSON ger rätt typ och fält; ogiltig JSON, okänd typ eller tom titel ger ANTECKNING med originaltexten.
- **Fångster per dag**: `fangsterPerDag(tider: List<Instant>, idag: LocalDate, zon: ZoneId): List<Pair<LocalDate, Int>>` för de senaste 30 dagarna inklusive dagar med noll.
- **Påminnelsens fördröjning**: `fordrojning(nu: Instant, start: Instant): Duration?`; null om start har passerat.
- **Systemprompt**: `byggSystemprompt(nu: ZonedDateTime): String` med datum, veckodag på svenska och tidszon; test att de finns i texten.
- **CSV**: historiken som CSV med rubrikrad, citattecken runt fält med kommatecken eller radbrytning.

## Klart när

- Bygget och testerna är gröna för alla moduler och `senaste-main` får en APK för `:rostfangst`.
- Mattias kan trycka på rutan i snabbinställningarna, säga "påminn mig om att ringa tandläkaren imorgon klockan nio" och få en avisering imorgon 09:00, och säga "lunch med Erik på torsdag tolv" och få kalendern öppnad med händelsen ifylld.
- Utan nät sparas det sagda och kan tolkas senare.

## Gör inte

- Spara eller skicka ljud.
- Lägga API-nyckel, testnyckel eller verkliga anrop i repot eller testerna.
- Ändra något i `garantivalvet/` eller `husloggen/`, eller i `:karna` utöver att använda temat.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
