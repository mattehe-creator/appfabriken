# Uppdrag: Styrelserummet, tidplan för föreningsstämman och avgifter vid överlåtelse

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Styrelserummet har gratisverktygen avgift, fondplan, nyckeltal, protokoll och räntor. Två frågor återkommer för en bostadsrättsförenings styrelse och går att svara på med ren datumräkning och tydliga regler: när måste kallelsen skickas och stämman hållas, och vad får föreningen ta ut vid överlåtelse och pantsättning. De fungerar som gratisverktyg som leder till tjänsten (komponentlistan), på samma sätt som de andra.

## Beslut (Claude, 2026-10-22)

- **Reglerna verifierar Cursor mot lagtexten, inte ur minnet.** Bostadsrättslagen (1991:614), föreningens stadgar som varierar (därför är fristerna parametrar med lagens gräns som golv eller tak) och prisbasbeloppet för aktuellt år (hämtas från SCB eller Försäkringskassan, med källa och datum). Sammanfatta med egna ord i `styrelserummet/regler/STAMMA.md` med paragraf och källa per punkt, och en rubrik "Osäkert" för allt som beror på tolkning. Det som inte kan verifieras tas inte med.
- Verktyget visar alltid "Allmän information, inte juridisk rådgivning. Kontrollera era stadgar." och räknar aldrig ut något som stadgarna kan ändra utan att fristen anges som en inmatning.
- **Cursor skriver ingen implementation av reglerna.** Cursor delar upp i uppgifter (minst 14, högst 20) enligt MALL.md och CLAUDE.md punkt 4 och lämnar funktionerna åt den lokala modellen. Cursor gör själv det som uppgiftsformatet inte klarar: sidskalet (HTML och JS i `static/`) och sidkartan. Det får göras när funktionerna finns.
- Inga nya beroenden. Inga personuppgifter. Beloppsgränser och prisbasbelopp ligger i en datafil (`styrelserummet/src/main/resources/stamma.csv`, med källa och giltigt år per rad), inte hårdkodade i koden.
- Ändra inte modellerna, `nyttjandeperioder.csv` eller de befintliga verktygen.

## Underlag (Cursor delar upp efter dessa områden)

1. **Stämmotidplan** (`regler/Stammotidplan.kt`): `fun tidplan(arsslut: LocalDate, stammodatum: LocalDate, kallelsefristVeckor: IntRange, ...)` som returnerar datum för: senaste dag för ordinarie stämma (sex månader efter räkenskapsårets slut, kontrolleras mot lagen), tidigaste och senaste kallelsedag utifrån stämmodatum och stadgarnas frist, sista dag att lämna revisionsberättelsen, och när årsredovisningen ska finnas tillgänglig för medlemmarna. Hanterar skottår, månadsslut ("sex månader efter 31 augusti") och att dagar räknas kalendermässigt. Exempel och undantag skriver uppgifterna ur `STAMMA.md`.
2. **Veckodag och helgdagar** (`regler/Helgdagar.kt`): `fun arSvenskHelgdag(d: LocalDate): Boolean` för röda dagar (nyårsdagen, trettondedag jul, långfredag, påskdagen, annandag påsk, första maj, Kristi himmelsfärdsdag, Sveriges nationaldag, midsommarafton och midsommardagen, alla helgons dag, juldagen, annandag jul; påskdatum med Gauss algoritm, rörliga dagar enligt reglerna) och `fun narmasteArbetsdag(d: LocalDate, riktning: Int): LocalDate`. Test för åren 2025 till 2030 mot en tabell som uppgiften skriver ur Riksdagens eller Skatteverkets kalender.
3. **Överlåtelseavgift och pantsättningsavgift** (`regler/Overlatelseavgift.kt`): `fun hogstaOverlatelseavgift(prisbasbelopp: Long): Long` och `fun hogstaPantsattningsavgift(prisbasbelopp: Long): Long` enligt procentsatserna som Cursor verifierar mot lagen och skriver i `STAMMA.md`; belopp i hela kronor, avrundning enligt lagens ordalydelse (om den anger något; annars avrundat nedåt och uppgiften säger det). `fun avgiftarGiltig(avgift: Long, prisbasbelopp: Long, typ: Avgiftstyp): Boolean` och ett felmeddelande på klarspråk när en avgift överstiger gränsen.
4. **Datafilen** (`stamma.csv`) med läsare (`regler/StammaData.kt`): `fun lasStammaData(rader: List<String>): Map<String, StammaRad>`; fel på saknad källa, ogiltigt år eller negativt belopp; `fun prisbasbeloppFor(ar: Int, data): Long?`.
5. **Utskrift och CSV** (`regler/StammaUtskrift.kt`): `fun tidplanRader(tidplan): List<Rad>` (etikett, datum på formatet `2026-05-31 (söndag)` på svenska, anmärkning, källa) och `fun tidplanCsv(tidplan): String` med semikolon, rubrikrad och sista raden "Allmän information, inte juridisk rådgivning."
6. **Sidskal** (Cursor): `static/stamma.html` och `static/stamma.js` i samma formspråk som `avgift.html`, utan externa skript, med inmatning av räkenskapsårets slut, stämmodatum och stadgarnas frister, resultat som tabell och en knapp för CSV. Länk från `index.html` och sidkartan. Samma skydd och metadata som övriga gratisverktyg (se `2026-10-22-styrelserummet-verktyg-skydd.md`).

## Klart när

- `styrelserummet/regler/STAMMA.md` finns med paragrafer och källor, och en rubrik "Osäkert".
- Alla uppgifter är skrivna i `uppgifter/ny/` med exempel och test, och PR:en med dem är mergad.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna när uppgifterna har gjorts.

## Gör inte

- Skriva funktionerna själv (bara sidskalet i punkt 6).
- Ange regler som inte kunnat verifieras mot lagtexten, eller räkna ut frister som stadgarna kan ändra utan att de är en inmatning.
- Lägga till beroenden eller ändra befintliga modeller och verktyg.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
