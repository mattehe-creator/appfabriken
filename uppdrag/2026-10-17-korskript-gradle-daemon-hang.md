# Uppdrag: körskriptet hänger när Gradle startar en ny daemon under Aider

## Bakgrund
2026-10-16 kl. 22:13 startade körskriptet uppgift 30 (`2026-10-16-30-sakerhetskopia-hamta`). Aider skrev koden, committade (2143e27) och körde testerna via `--test-cmd`. Loggen visar "Starting a Gradle Daemon" och sedan ett grönt bygge kl. 22:22. Därefter hände ingenting: Aider avslutades aldrig, status.json blev kvar på "kodar", grenen pushades aldrig, ingen PR öppnades och `kor-alla` startade inga fler körningar eller Vaktmästare-anrop. Kön stod still i över en timme.

Trolig orsak: Gradle-daemonen startades som barnprocess under Aiders teststeg och ärver Aiders utskriftsrör. Röret stängs aldrig så länge daemonen lever, så Aider väntar för evigt på att testkommandot ska "bli klart". Tidigare körningar gick bra eftersom en daemon redan fanns. Uppstår när ingen daemon lever, till exempel efter att datorn använts (skriptet stoppas då) eller efter omstart.

Dessutom: loggen har 14 fel `GetContentWriterIOError` från `Add-Content` mot `aider-senaste.log` (raden `& $aider ... | ForEach-Object { ... Add-Content ... }`). Panelen läser filen samtidigt. Felen är ofarliga men fyller loggen och kan tappa rader.

## Uppgift
Gör `verktyg/kor-uppgift.ps1` (och vid behov `kor-alla.ps1`) robust:
1. Värm upp Gradle före Aider: kör `gradlew.bat help` (utan `--no-daemon`) en gång i repots rot med all utskrift till `$null`, så att daemonen startas av skriptet och inte av Aiders testkommando. Kontrollera att daemonen lever efter uppvärmningen.
2. Sätt en tidsgräns för Aider (förslag 60 minuter, ändringsbar överst i skriptet). Kör Aider som egen process (`Start-Process` med omdirigerad utskrift till fil, eller motsvarande) så att skriptet kan avsluta den. Vid tidsgräns: döda Aiders processträd (`taskkill /T /F`), skriv "Aider avbröts efter tidsgränsen" i loggen och fortsätt med den egna testkörningen, commit och push som vanligt, så att redan committat arbete inte går förlorat.
3. Om Aider har committat men skriptet ändå avbryts på annat sätt: lämna grenen kvar lokalt och låt nästa körning spara den som `rescue/<uppgift>-<tidsstämpel>` (se `2026-10-16-korskript-kvarliggande-gren`).
4. Skriv `Add-Content` till `aider-senaste.log` med `-ErrorAction SilentlyContinue` och ett kort nytt försök (till exempel tre försök med 100 ms paus), så att panelens läsning inte ger felrader.
5. Uppdatera status.json till "testar" strax efter att Aider avslutats eller avbrutits, så att panelen inte visar "kodar" för en körning som inte längre kodar.

## Klart när
- En körning där ingen Gradle-daemon lever går igenom hela vägen (Aider avslutas, push, PR) utan att hänga. Prova genom att köra `gradlew.bat --stop` och sedan en uppgift.
- En körning där Aider medvetet hängs (till exempel tidsgränsen satt till 1 minut) avbryts, och skriptet fortsätter till test och push.
- Loggen innehåller inga `GetContentWriterIOError` när panelen är öppen.
- Ingen annan fil än skripten i `verktyg/` ändras. Lägg inte till beroenden.

## Gör inte
- Ändra uppgiftsformatet, workflows eller någon appkod.
- Slå på Gradles configuration cache i det här uppdraget (eget uppdrag, låg prioritet).
- Radera grenar på origin.
- Skriv frågor under "Frågor till Claude" i PR-beskrivningen.
