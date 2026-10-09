# Beslutslogg

## 2026-10-08

- Projektet bygger en portfölj av appar för Google Play. Roller: Mattias beslutar, Claude projektleder och kontrollerar direktiven, Cursor delar upp och granskar, lokal modell skriver koden. (Mattias)
- Apparna publiceras via TM Connect som organisationskonto. Apputveckling läggs till i företagets verksamhet. (Mattias)
- Den lokala modellen arbetar när datorn inte används. (Mattias)
- Målet är en portfölj av flera appar över tid. (Mattias)
- Mattias hanterar frågan om bisyssla själv. (Mattias)
- Första appen är Garantivalvet, enligt IDEURVAL.md. (Claude föreslog, Mattias godkände)
- Den lokala modellen kör på Mattias 3080 Ti när datorn inte används, även långsamt. Cursor får ta tyngre uppgifter. (Mattias)
- Mattias sköter företaget och Play-kontot själv. (Mattias)
- Repot är `mattehe-creator/appfabriken`, huvudgren `main`. Ett Gradle-bygge med biblioteket `:karna` och en appmodul per app. (Claude)
- Första lokala uppgiften (garantitid) godkänd och mergad. qwen3-coder:30b med Aider räcker för uppgifter i den storleken. (Claude)
- Uppdraget köp, lista och formulär skickat till Cursor. Nya beroenden i `:garantivalvet`: Room 2.8.4 med KSP, lifecycle-viewmodel-compose och lifecycle-runtime-compose 2.8.7, och Robolectric som testberoende. Cursors egen del och uppgifterna för den lokala modellen läggs i samma PR. (Claude föreslog, Mattias godkände uppdraget)

## 2026-10-09

- Cursors PR #3 (Room, KopDao med tester, ViewModel, navigering utan bibliotek, formulärskelett) granskad och mergad. Sju uppgifter (02–08) väntar på den lokala modellen. (Claude)
- Den lokala modellen körs via Schemaläggaren i Windows (`verktyg/installera-schema.ps1`): när datorn varit oanvänd i 10 minuter och varje natt kl. 01. Körskriptet hoppar över uppgifter vars beroenden inte ligger i `uppgifter/klar/` på main, och uppgifter som redan har en gren. (Claude)
- Tills Cursors granskning är automatiserad granskar och mergar en schemalagd Claude-uppgift den lokala modellens PR:er var tredje timme, och flyttar uppgiften till `uppgifter/klar/`. (Claude)
- Granskning av lokala PR #4–#7: #5, #6 och #7 godkända efter små rättelser av Claude (filer i fel mapp, avrundning till öre, hårt mellanslag i pris, feltexter som inte stämde med reglerna). #4 (sortering) underkänd och ersatt av försök 2. Granskningen körs nu varje timme i stället för var tredje, eftersom den var flaskhalsen. (Claude)
- Cursor granskar den lokala modellens PR:er via workflowet `cursor-granskning`, och Claude granskar Cursors beslut innan merge. Frågor ställs på fasta ställen enligt ARBETSFLODE.md: lokala modellen i FRAGA.md, Cursor i sin beslutsfil, Claude i FRAGOR.md. (Mattias beslutade granskningen, Claude utformade frågorna)
- Cursor godkände PR #9 (formulärskärm) med två fel som Claude rättade: tom eller ogiltig garantitid sparades som 0 månader i stället för att ge `fel_garanti`, och Spara var avstängd vid tomt vad så att `fel_vad_saknas` aldrig visades (uppgiftens första exempel). Cursor ska pröva uppgiftens exempel mot koden, inte bara att fälten finns. (Claude)
- Cursors granskningsbeslut verkställs direkt av workflowet `cursor-beslut` (merge eller försök 2), så att den lokala modellen inte väntar på Claudes timvisa körning. Claude granskar Cursors beslut i efterhand. (Claude, efter att Mattias påpekat väntetiden)
- Uppdraget kvittofoto skickat till Cursor (migration 1→2 med kolumnen kvitto_fil, intern lagring utan behörigheter, room-testing som testberoende). (Claude)
- Uppdrag delas i två steg: Cursor lämnar först uppgiftsfilerna, sedan den tunga koden, så att den lokala modellen kan börja tidigare. Uppgifter som väntar på Cursors kod märks beror-på: cursor:<uppdrag>. (Claude)
- Cursors PR #12 (kvittofoto: migration 1→2, KvittoLager, FileProvider) granskad och mergad. Svar på Cursors frågor: uppdraget heter `uppdrag/2026-10-09-kvittofoto.md` (referenserna i uppgifterna rättade); migrationstestet med manuell v1-databas räknas som "motsvarande" MigrationTestHelper. Lagt till uppgift 07: avkoda stora bilder med inSampleSize, eftersom hela bilden avkodas i full storlek i dag och kan ta slut på minnet. (Claude)
- Kön stod still på uppgift 02: Aider gjorde ingen ändring, så pushen innehöll bara uppgiftsfilen (.md). Bygget ignorerade .md-pushar, inget bygge kördes och därför startade aldrig Cursor-granskningen. Rättat: lokal/-grenar byggs alltid; andra grenar hoppar fortfarande över bygget när bara .md ändrats. Samma lucka gällde FRAGA.md från den lokala modellen. (Claude)
