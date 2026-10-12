# Uppgift: språkspecifik exempeldata och omgenererade skärmbilder

uppdrag: uppdrag/2026-10-12-sprakformat-och-engelska-skarmbilder.md
försök: 1
beror-på: 2026-10-12-35-locale-datum-pris-ui-f2.md

## Ändra bara dessa filer
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/butik/ButikSkarmbilderTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListaSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt

## Uppgift
Idag delar `ButikSkarmbilderSvTest` och `ButikSkarmbilderEnTest` samma `exempelLista` med svenska texter, så engelska skärmbilder visar "Mobiltelefon", "Cykel" och svenska datum (löses av föregående uppgift).

1. Flytta exempeldata till språkspecifik kod, t.ex. abstrakta metoder `exempelLista(): List<Kop>` och `exempelFormular(): ...` som `ButikSkarmbilderSvTest` respektive `ButikSkarmbilderEnTest` implementerar.
2. **Svenska** (`sv`): behåll nuvarande köp (Diskmaskin, Mobiltelefon, Cykel med befintliga butiksnamn).
3. **Engelska** (`en`): använd minst dessa texter:
   - "Dishwasher", "Local appliance store"
   - "Mobile phone", "Phone shop downtown"
   - "Bicycle", "Sports shop"
4. `formulär()`-testet ska använda samma språk som övriga skärmbilder för det aktuella testfallet (initialVad, initialVar m.m.), inte hårdkodad svenska i den engelska klassen.
5. Kör Roborazzi och committa **alla** PNG-filer som diffas:
   ```bash
   ./gradlew :garantivalvet:recordRoborazziDebug --tests "*ButikSkarmbilder*"
   ```
   Förväntat minst:
   - Alla fyra under `garantivalvet/butik/skarmbilder/en/` (exempeldata + engelska datum).
   - Under `garantivalvet/butik/skarmbilder/sv/`: de bilder där datumformat ändrats (lista och detalj efter locale-uppgiften).
   - **Generera om inte** `garantivalvet/butik/funktionsgrafik-sv.png` eller `funktionsgrafik-en.png` om innehållet inte ändrats.

Om `recordRoborazziDebug` misslyckas i din miljö (t.ex. saknad GPU): gör kodändringen, skriv kort vad som saknas i `FRAGA.md` i repots rot, och låt Cursor köra Roborazzi i granskningen.

## Exempel
- Engelsk `01-lista.png`: rubriker på engelska, "Mobile phone", datum enligt testets locale en-GB, alltså "1 Aug 2026" (inte "1 aug. 2026").
- Svensk `01-lista.png`: "Mobiltelefon", datum som "1 aug. 2026".

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikSkarmbilder*"

## Gör inte
- Ändra appkod utöver testfilen.
- Ändra `garantivalvet/butik/sv/*.txt`, `en/*.txt` eller `data-safety.md`.
- Lägga till beroenden.

utfall: godkänd 2026-10-12 (Roborazzi en-skärmbilder, oanvända importer)
