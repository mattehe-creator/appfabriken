# Uppdrag: full återställning av den lokala körningen i stället för väntan och många timers

## Bakgrund
2026-10-17 skrev körskriptet elva stoppnoteringar i `granskning/lokal-stopp.md`, alla "Tre avbrott i rad … stannade med fel (1)". Efter varje stopp vilade skriptet 30 minuter (uppdraget `2026-10-17-korskript-tillsyn`, punkt 3). Mattias stängde rutan och startade om, och då fungerade körningen direkt. Alltså sitter felet i kvarliggande tillstånd (Gradle-daemon, kvarvarande java/aider-processer, Ollama med halvladdad modell, låst fil), och väntan hjälper inte. Modellen själv fungerar: alla fyra lokala uppgifter samma dag blev godkända. Loggen `aider-senaste.log` ligger bara på datorn, så Claude och Cursor kan inte se orsaken till "fel (1)".

Läggs efter `2026-10-17-korskript-gradle-daemon-hang` och `2026-10-17-korskript-tillsyn` (samma filer). Kontrollera vad de redan gör och bygg vidare, dubblera inte.

## Uppgift
Ändra `verktyg/kor-alla.ps1` (och vid behov `kor-uppgift.ps1`):
1. **Full återställning före varje uppgift:** `gradlew.bat --stop`, döda kvarvarande `java`- och `aider`-processer som startats av körningen, `ollama stop <modell>` (ladda ur modellen) och starta om Ollama om det inte svarar. Kör sedan uppgiften i en helt ny process.
2. **Efter ett fel: återställ direkt och försök igen** (högst en gång per uppgift och försök) i stället för att vänta 30 minuter. Ersätt "tre avbrott i rad, vila 30 minuter" med: full återställning och nytt försök. Först när även det försöket misslyckas skrivs stoppnotering, och då vilar skriptet högst 5 minuter innan nästa uppgift.
3. **Tom kö är inget fel:** finns ingen uppgift i `uppgifter/ny/` avslutar `kor-alla` tyst med en rad i loggen. Det räknas inte som avbrott och ger ingen stoppnotering.
4. **Exit-kod 1 ska ha en orsak:** skriv varje felkod och dess källa (Aider, test, git, push, Ollama, Gradle) i loggen, och i stoppnoteringen.
5. **Logg till GitHub:** vid stoppnotering lägger skriptet de sista 40 raderna av `aider-senaste.log` och körloggen i `granskning/lokal-stopp-logg.md` på main (skriv över filen, behåll bara senaste stoppet). Bara loggtext om körningen, inga personuppgifter eller hemligheter; maska allt som liknar en token eller sökväg under användarens hemkatalog.
6. **En enda hård tidsgräns** (värde överst i skriptet) finns kvar som yttersta skydd, i enlighet med tillsynsuppdraget. Inga ytterligare timers läggs till.

## Klart när
- Prov: döda Ollama och en Gradle-daemon mitt i en uppgift. Skriptet återställer, försöker igen och uppgiften går igenom utan att någon rör datorn.
- Prov: tom `uppgifter/ny/` ger ingen stoppnotering.
- Vid ett medvetet fel finns `granskning/lokal-stopp-logg.md` på main med felkod, källa och de sista loggraderna.
- Ingen annan fil än skripten i `verktyg/`, `granskning/lokal-stopp.md` och `granskning/lokal-stopp-logg.md` ändras. Inga beroenden.

## Gör inte
- Ändra uppgiftsformatet, workflows eller appkod.
- Radera grenar på origin.
- Skriva frågor under "Frågor till Claude" i PR-beskrivningen om det inte behövs.
