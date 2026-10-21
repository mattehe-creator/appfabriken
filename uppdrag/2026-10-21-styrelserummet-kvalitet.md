# Uppdrag: Styrelserummet, kvalitet i utkastet

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Robusthetsuppdraget är klart (26 lokala uppgifter mergade 2026-10-20 och 21). Läsningen tål nu stökigare handlingar. Nästa steg är utkastets kvalitet: att komponentlistan blir begriplig för en styrelse och trovärdig för en revisor. Allt är rena funktioner med tester, alltså arbete för den lokala modellen.

## Beslut (Claude, 2026-10-21)

- **Cursor skriver ingen implementation.** Cursor delar upp i uppgifter (minst 14, högst 20) enligt MALL.md och CLAUDE.md punkt 4 och lämnar all kod åt den lokala modellen. Bara byggfiler eller ändringar i fler än 3 filer får Cursor ta själv, och säger då varför i PR-beskrivningen.
- Uppgifterna är oberoende av varandra (`beror-på: ingen`) där det går. Där en uppgift behöver en annan anges den med exakt filnamn.
- Inga nya beroenden. Inga ändringar i `Komponent`, `Forening`, `Komponentgrupp`, `Sakerhet` eller `nyttjandeperioder.csv`.
- Värdena i `nyttjandeperioder.csv` är förslag och flera rader är markerade osäkra. Funktionerna får aldrig presentera dem som fakta; texter säger "förslag" eller "uppskattning".

## Underlag (Cursor delar upp efter dessa områden)

1. **Gruppering på namn** (`underlag/GrupperaNamn.kt`): `fun gissaGrupp(namn: String): Komponentgrupp?` som ur nyckelord i namnet (gemener, utan hänsyn till böjning i de vanliga fallen) föreslår grupp: "yttertak", "takpapp", "tegel" till TAK; "fönster", "balkongdörr", "entrédörr" till FONSTER_DORRAR; "stammar", "avlopp", "vattenledning" till STAMMAR; "fasad", "puts", "fasadtegel" till FASAD; "hiss" till HISS; "tvättstuga" till TVATTSTUGA; "ventilation", "FTX", "från-/tilluft" till VENTILATION; "el", "elcentral", "elstammar" till EL; "värme", "fjärrvärme", "radiator", "undercentral" till VARME; "garage", "asfalt", "gård", "mark" till MARK_OCH_GARAGE; annars `null`. En uppgift per tre-fyra grupper, med exempel på både träff och nära miss (till exempel "Elisabeths trädgård" ska inte bli EL).
2. **Föreslagen nyttjandeperiod** (`regler/NyttjandeperiodForslag.kt`): `fun forslagFor(grupp: Komponentgrupp, tabell: List<NyttjandeperiodRad>): Int?` som returnerar tabellens förslag (null för MARK_OCH_GARAGE), och `fun arInomIntervall(ar: Int, rad: NyttjandeperiodRad): Boolean` som hanterar intervall "40-100" och fast värde "50".
3. **Formatering** (`regler/Formatering.kt`): `fun kronor(belopp: Long): String` ger `1 234 567 kr` med hårt mellanslag (U+00A0) som tusentalsavgränsare, negativa med minus (U+2212); `fun procent(del: Long, helhet: Long): String` ger `12,5 %` med en decimal och komma, och `–` om helhet är 0; `fun arsintervall(fran: Int, till: Int): String` ger `2010–2050`.
4. **Sammanfattning i klartext** (`regler/Sammanfattning.kt`): `fun sammanfatta(komponenter: List<Komponent>, byggnadensVarde: Long?, arsslut: Int): List<String>` ger högst sex korta svenska meningar: antal komponenter, summa av anskaffningsvärde, hur stor andel som är `UPPSKATTNING` eller `LAG`, avstämningens utfall (stämmer, eller differens i kronor), årets avskrivning totalt, och en sista mening om att allt är ett utkast. Varje mening är en egen uppgift eller ett par uppgifter, med test på exakt text.
5. **Jämförelse mot föregående år** (`regler/Jamforelse.kt`): `fun forandring(nu: List<Komponent>, foreg: List<Komponent>): List<Forandring>` där en komponent matchas på namn (utan hänsyn till versaler och blanksteg) och förändringen är `NY`, `BORTTAGEN` eller `ANDRAD` (anskaffningsvärde, nyttjandeperiod eller anskaffningsår). Test på alla tre och på oförändrad komponent (ingen förändring).
6. **Utskriftsdata** (`server/Utskrift.kt`): ren funktion som bygger raderna för utskriftssidan ur komponenter: en rad per komponent med grupp, namn, anskaffningsvärde, nyttjandeperiod, årets avskrivning och bokfört värde, formaterade med funktionerna i punkt 3, plus en summarad. Ingen HTML i den här uppgiften; bara en lista av radobjekt.
7. **Felmeddelanden** (`server/Felmeddelanden.kt`): `fun forklara(kod: String): String` som ger ett vänligt svenskt meddelande för serverns felkoder (läs vilka koder som finns i `JobbKo.kt` och `Application.kt`), med ett generiskt svar för okända koder som inte läcker koden. En uppgift för kartläggningen, en för texterna.

## Klart när

- Alla uppgifter är skrivna i `uppgifter/ny/` med exempel och test, och PR:en med dem är mergad.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna när uppgifterna har gjorts.

## Gör inte

- Skriva implementationen själv.
- Lägga till beroenden eller ändra modeller eller `nyttjandeperioder.csv`.
- Koppla in funktionerna i `Application.kt` eller användargränssnittet (det är ett senare uppdrag när funktionerna finns).
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
