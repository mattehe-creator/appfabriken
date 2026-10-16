# Uppdrag: tillsyn och självläkning för den lokala körningen

## Bakgrund
Den lokala körningen har stannat flera gånger per dag av olika skäl (lösa filer, git-meddelanden, kvarliggande grenar, och 2026-10-16 en hängd Aider när Gradle startade en daemon under dess rör, se `2026-10-17-korskript-gradle-daemon-hang`). Varje gång har kön stått still tills Mattias eller Claude märkt det. Varken Claude eller Cursor når datorns processer, så åtgärden måste köra på datorn och agera själv. Läggs efter `2026-10-17-korskript-gradle-daemon-hang` (samma filer).

## Uppgift
Gör `verktyg/kor-alla.ps1` (och vid behov `kor-uppgift.ps1`) självläkande:
1. **Tillsyn:** `kor-alla` startar `kor-uppgift` som barnprocess och övervakar den. Räknas som hängd om `status.json` inte ändrats och `aider-senaste.log` inte vuxit på 15 minuter (värdet överst i skriptet), eller om en uppgift överstiger 90 minuter totalt. Då: döda hela processträdet (`taskkill /T /F`, inklusive `java`/Gradle-daemoner som startats av körningen), skriv orsaken i loggen och gå vidare.
2. **Ta vara på arbetet:** finns commits på `lokal/<uppgift>` efter en avbruten körning, kör testerna och pusha grenen och öppna PR som vanligt, med texten "Körningen avbröts av tillsynen" i PR-beskrivningen så att Cursors granskning ser det. Finns inga commits räknas avbrottet som ett misslyckat försök: uppgiften går tillbaka till `uppgifter/ny/` med försök 2, och vid andra misslyckandet gör Cursor uppgiften enligt `ARBETSFLODE.md`.
3. **Aldrig stå still:** ett fel i en uppgift (hängning, git-fel, saknad fil) får inte stoppa `kor-alla`; den går vidare till nästa uppgift. Tre avbrott i rad: vila 30 minuter och skriv en rad i `FRAGOR.md`-liknande fil `granskning/lokal-stopp.md` på main, så att Claude ser det.
4. **Hjärtslag:** `kor-alla` anropar Vaktmästaren (`workflow_dispatch`, som idag, utan indata) minst var 30:e minut även när en uppgift körs länge eller ingen körs. Då syns på GitHub att datorn lever, och Claudes timgranskning kan larma när hjärtslaget uteblir. Workflows ändras inte i det här uppdraget.
5. **Städning:** `gradlew.bat --stop` vid start av `kor-alla` och efter varje avbrott.

## Klart när
- En medvetet hängd Aider (prov: tidsgräns satt till en minut) dödas av tillsynen, arbetet pushas eller uppgiften återförs, och nästa uppgift startar utan att någon rör datorn.
- Stoppas `kor-uppgift` mitt i (processen dödas) går `kor-alla` vidare till nästa uppgift.
- Hjärtslaget syns som Vaktmästare-körningar på GitHub minst var 30:e minut, även under en lång uppgift.
- Ingen annan fil än skripten i `verktyg/` och `granskning/lokal-stopp.md` ändras. Lägg inte till beroenden.

## Gör inte
- Ändra uppgiftsformatet, workflows eller appkod.
- Radera grenar på origin.
- Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
