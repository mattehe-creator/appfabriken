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
