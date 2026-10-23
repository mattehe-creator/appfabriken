# Uppdrag: Styrelserummet, påhittade exempelhandlingar för provkörning och demo

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Mattias ska provköra tjänsten lokalt (se `styrelserummet/PROVKORNING.md`) och landningssidan behöver ett exempel som visar vad man får. Båda behöver **påhittade** handlingar som liknar riktiga men inte är det: en underhållsplan och en årsredovisning för en påhittad förening. Det får inte finnas något verkligt namn, någon verklig adress eller något verkligt organisationsnummer.

## Beslut (Claude, 2026-10-23)

- **Cursor skriver ingen implementation.** Cursor delar upp i uppgifter (minst 10, högst 16) enligt MALL.md och CLAUDE.md punkt 4 och lämnar funktionerna åt den lokala modellen. Cursor gör själv det som uppgiftsformatet inte klarar: ett Gradle-uppgiftsnamn `skapaExempel` i `styrelserummet-server/build.gradle.kts` som anropar generatorn och skriver två PDF-filer till en katalog som anges med `-PexempelKatalog=...`.
- Föreningen heter **"Brf Exempelgården"** (påhittad), organisationsnummer `000000-0000`, adress "Exempelvägen 1, 100 00 Teststad". Ingen verklig person, firma eller adress får förekomma. Byggår 1974.
- Generatorn använder PDFBox (finns redan) och inga nya beroenden. Beloppen är påhittade men **konsekventa**: komponentlistan ska summera till byggnadens redovisade värde i den påhittade årsredovisningen (så att avstämningen går att visa), och en rad ska med flit sakna kostnad så att tjänstens varning syns.
- Ändra inte modeller, `nyttjandeperioder.csv` eller befintliga verktyg.

## Underlag (Cursor delar upp efter dessa områden)

1. **Data** (`underlag/exempel/ExempelData.kt`): konstanter för den påhittade föreningen och en lista om 12 till 16 åtgärder (namn, år, kostnad i kronor) som täcker tak, fasad, fönster, stammar, el, värme, ventilation, hiss, tvättstuga och mark. En uppgift för föreningens uppgifter, en per fyra-fem åtgärder.
2. **Texter** (`underlag/exempel/ExempelTexter.kt`): `fun underhallsplanSidor(): List<String>` (tre sidor: inledning, tabell med åtgärd/år/kostnad, fortsättning med belopp i tkr på sista sidan) och `fun arsredovisningSidor(): List<String>` (förvaltningsberättelse och balansräkning med två kolumner, innevarande år först, "Byggnader och mark" i kronor). Test: summan av åtgärdernas kostnader i planen och byggnadsvärdet i årsredovisningen stämmer enligt `stamAv`.
3. **PDF** (`underlag/exempel/ExempelPdf.kt`): `fun skrivPdf(sidor: List<String>): ByteArray` med PDFBox, markerbar text, A4, standardtypsnitt, radbrytning vid 90 tecken; test som läser tillbaka texten med `PdfLasare` och jämför.
4. **Hela flödet**: ett test som kör `byggUtkast` med en falsk `Tolkare` som returnerar ett inspelat svar för exempelhandlingarna och kontrollerar komponenter, avstämning och att anmärkningen om raden utan kostnad finns.
5. **Gradle-uppgiften** (Cursor): `skapaExempel`, och en rad i `PROVKORNING.md` under "Det du behöver" om hur man skapar exempelfilerna. Mattias provar med dem först.

## Klart när

- Alla uppgifter är skrivna i `uppgifter/ny/` med exempel och test, och PR:en med dem är mergad.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna när uppgifterna har gjorts.
- Inga verkliga namn, adresser eller organisationsnummer i repot.

## Gör inte

- Skriva funktionerna själv (bara Gradle-uppgiften och raden i `PROVKORNING.md`).
- Lägga till beroenden eller ändra befintliga modeller och verktyg.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
