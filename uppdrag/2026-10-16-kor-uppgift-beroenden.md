# Uppdrag: körskriptet får inte fastna på cursor:-beroenden

## Bakgrund
Den lokala modellen stod stilla i onödan 2026-10-16. Uppgift 21-23 hade `beror-på: cursor:2026-10-16-husloggen-bilder`. `verktyg/kor-uppgift.ps1` räknar ett `cursor:`-beroende som klart bara om en mergad PR finns från exakt grenen `cursor/<namn>`. Cursor lägger ofta sin kod på en gren med annat namn (här `cursor/husloggen-bilder-p-poster-1a65`, mergad som #178). Beroendet blev aldrig uppfyllt, och `kor-alla.ps1` väntade 3 minuter i taget på något som redan var klart. Claude rättade de tre uppgifterna för hand. Felet kommer tillbaka vid nästa uppdrag med kod från Cursor, och den döda tiden bromsar hela projektet. Mattias vill att dötiden försvinner.

## Uppgift
Ändra bara `verktyg/kor-uppgift.ps1`, `verktyg/kor-alla.ps1` och `uppgifter/MALL.md`. Detta är tungt arbete och ska göras av Cursor direkt, inte delas upp till den lokala modellen.

1. **Matchning av `cursor:<namn>`.** Beroendet är klart när något av detta stämmer:
   - en mergad PR finns från grenen `cursor/<namn>` (som i dag), eller
   - en mergad PR från en gren som börjar med `cursor/` nämner `uppdrag/<namn>.md` eller `<namn>` i titel eller beskrivning (till exempel via `gh pr list --state merged --search "<namn>" --json number,headRefName,title,body`, filtrerat på `headRefName` som börjar med `cursor/`).
2. **Ingen tyst väntan.** När en uppgift hoppas över på grund av ett beroende ska skriptet skriva ut vilket beroende som saknas och varför det inte räknas som klart, tillsammans med närmaste öppna PR som hör till det (om någon). Det ska stå i loggen och i `status.json`, så att panelen visar det.
3. **Kortare väntan.** I `kor-alla.ps1`: när exit-koden är 3 och minst en uppgift väntar på ett beroende, vänta 60 sekunder i stället för 180. Är kön tom eller allt väntar på granskning av lokala PR:er, behåll 180 sekunder. Utöka exit-koderna med en egen kod för "blockerad av beroende" om det behövs för att skilja fallen.
4. **Varning vid fast beroende.** Har samma uppgift varit blockerad av ett `cursor:`-beroende i mer än 30 minuter, skriv en tydlig varning i fönstret och i `status.json`. Skriptet ska fortsätta med nästa uppgift som är redo.
5. **Dokumentation.** Lägg en rad i `uppgifter/MALL.md` om hur `cursor:<namn>` matchas, och att `beror-på` hellre pekar på en uppgiftsfil än på en gren när det går.

## Klart när
- Ett test eller en torrkörning (skript med låtsas-utdata från `gh`, eller `-IngenPush`) visar att en uppgift med `beror-på: cursor:2026-10-16-husloggen-bilder` räknas som redo när en mergad `cursor/`-PR nämner `uppdrag/2026-10-16-husloggen-bilder.md`, och som blockerad när ingen gör det.
- Utskriften för en blockerad uppgift namnger saknat beroende.
- Väntetiden i `kor-alla.ps1` är 60 sekunder vid beroendeblockering och 180 sekunder annars.
- Inga andra filer än de tre ovan är ändrade. Bygget är grönt.

## Gör inte
- Ändra workflows i `.github/workflows/`.
- Ändra uppgifter i `uppgifter/ny/`, `pagar/` eller `klar/`.
- Lägga till beroenden eller ändra något i appmodulerna.
- Ta bort beroendekontrollen. Uppgifter ska fortfarande vänta på kod som inte är mergad.

## Frågor till Claude
Skriv eventuella frågor under rubriken "Frågor till Claude" i PR-beskrivningen.
