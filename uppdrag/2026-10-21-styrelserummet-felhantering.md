# Uppdrag: Styrelserummet, felhantering och helhetstest

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Robusthetsuppdraget (2026-10-20) gjorde läsningen och indatan tåligare. Kvar innan tjänsten kan provköras med riktiga handlingar är att fel möter användaren med begripliga svenska meddelanden och att hela flödet (uppladdning, maskning, tolkning, utkast, CSV) har ett helhetstest med en stubbad AI-klient. Ingen API-nyckel behövs för det här uppdraget.

## Beslut (Claude, 2026-10-21)

- Cursor delar upp i uppgifter (minst 10, högst 16) enligt MALL.md och CLAUDE.md punkt 4. All implementation som ryms i ett uppgiftsformat görs av den lokala modellen. Cursor tar bara det som kräver byggfiler eller fler än 3 filer, och säger varför i PR-beskrivningen.
- Uppgifterna är oberoende av varandra där det går (`beror-på: ingen`).
- Inga nya beroenden. Inga ändringar i `Komponent` och `Forening`.
- Skriv frågor till Claude under "Frågor till Claude" i PR-beskrivningen.

## Underlag (Cursor delar upp efter dessa områden)

1. **Felmeddelanden till användaren** (ren funktion `felTillMeddelande(fel): String` i servermodulen, svenska, utan tekniska detaljer och utan filinnehåll eller personuppgifter): PDF utan text (inskannad bild), krypterad PDF, fil över storleksgränsen, fil som inte är PDF, AI-tjänsten svarar inte (timeout), AI-tjänsten svarar med fel eller ogiltigt JSON, gränsen 5 jobb per timme uppnådd (med besked om när man kan försöka igen), okänt fel. En uppgift per par av fall.
2. **Statuskoder**: varje fel ovan mappas till rätt HTTP-status (400, 413, 415, 429, 502, 504, 500) i en ren funktion med test.
3. **Stubbad AI-klient**: ett gränssnitt för AI-anropet finns eller införs minimalt, så att tester kan ge ett fast svar. Bara om det inte redan finns.
4. **Helhetstest** i `ApiTest.kt`: POST /api/jobb med en liten testPDF som skapas i testet, stubbad AI som returnerar ett giltigt svar med tre komponenter; kontrollera att svaret innehåller utkastmärkning, tre rader, att personnummer i PDF-texten inte når AI-klienten (stubben registrerar vad den fick), och att CSV-exporten har rätt kolumner och summor.
5. **Helhetstest, felvägar**: stubben kastar timeout, ger ogiltigt JSON, ger tomt svar; kontrollera status och meddelande, och att inget stacktrace eller filinnehåll syns i svaret eller i loggen.
6. **Loggning**: en ren funktion som tar ett jobbfel och ger en loggrad utan filnamn, text, belopp eller personuppgifter (bara jobb-id, feltyp, tid).

## Klart när

- Alla uppgifter är skrivna i `uppgifter/ny/` med exempel och test, och PR:en med dem är mergad.
- `gradle :styrelserummet:test :styrelserummet-server:test` är grönt när uppgifterna är gjorda.

## Gör inte

- Skriva implementationen själv, utom enligt beslutet ovan.
- Lägga till beroenden eller ändra modeller.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
