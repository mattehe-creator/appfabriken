# Uppdrag: Styrelserummet, räknekärnan för K3-komponentlistan

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Styrelserummet är tjänsten för bostadsrättsföreningar (webb, självbetjäning, inga samtal). Första produkten: en komponentlista enligt K3 ur föreningens underhållsplan och årsredovisning, färdig att lämna till revisor eller ekonomisk förvaltare. Landningssidan finns i `webb/k3/sida.html` och lovar: ladda upp underhållsplan och årsredovisning, få ett utkast till komponentindelning med anskaffningsvärden och nyttjandeperioder. Mattias beslutade 2026-10-19 att bygga tjänsten istället för att vänta på landningssidans test (BESLUTSLOGG.md).

Det här uppdraget bygger den del som inte beror på AI, server eller hosting: räkneregler och datamodell, helt testbara utan nätverk. Nästa uppdrag (`2026-10-19-styrelserummet-underlag.md`) läser handlingarna, därefter kommer webben (`2026-10-19-styrelserummet-webb.md`).

## Beslut (Claude, 2026-10-19)

- Styrelserummet är en webbtjänst, inte en Android-app. CLAUDE.md punkt 5 (offline, inget INTERNET, Room) och punkt 6 (gratis med Pro på 15 poster, appen riktad till konsument) gäller Garantivalvet och Husloggen, inte Styrelserummet. Styrelserummet får egna regler i `styrelserummet/APP.md`.
- Ny modul `:styrelserummet`: ren Kotlin/JVM (`kotlin("jvm")`, JVM 17, samma Kotlin-version som övriga), inga Android-beroenden, inga andra beroenden än testbiblioteket som redan används i portföljen. Läggs i `settings.gradle.kts`. Bygget i GitHub Actions ska köra dess tester tillsammans med övriga moduler.
- Belopp som `Long` i hela kronor. Ingen flyttalsräkning på pengar.
- Domänkunskap (K3 för bostadsrättsföreningar, komponentavskrivning) ska **verifieras av Cursor mot källan**, inte tas ur minnet: BFNAR 2012:1 Årsredovisning och koncernredovisning (K3) och BFN:s vägledning för bostadsrättsföreningar. Sammanfatta reglerna med egna ord i `styrelserummet/regler/K3.md` med hänvisning till kapitel och punkt. Det som är osäkert eller kräver revisorns bedömning listas under rubriken "Osäkert" i den filen och i PR-beskrivningen, så att Claude kan avgöra det.
- Tjänsten ger ett **utkast**. Revisorn eller föreningen beslutar. Det ska stå i all utdata.

## Del A (Cursor)

1. Modul, `build.gradle.kts`, `settings.gradle.kts`, `styrelserummet/APP.md` (produkten, målgrupp, avstegen ovan, att det är utkast) och `styrelserummet/regler/K3.md`.
2. Datamodell i paketet `se.tmconnect.styrelserummet.modell`:
   - `Forening(namn, byggnadsar: Int?, raknearStartManad: Int = 1)`
   - `Komponent(namn, grupp: Komponentgrupp, anskaffningsvarde: Long, anskaffningsar: Int, nyttjandeperiodAr: Int, restvarde: Long = 0, kalla: String, sakerhet: Sakerhet)`
   - `Komponentgrupp` (enum, sv-namn): STOMME, FASAD, TAK, FONSTER_DORRAR, STAMMAR, EL, VARME, VENTILATION, HISS, TVATTSTUGA, MARK_OCH_GARAGE, OVRIGT
   - `Sakerhet` (enum): HOG, MEDEL, LAG; plus `UPPSKATTNING` för värden som tjänsten har fördelat själv.
3. Standardtabell `styrelserummet/src/main/resources/nyttjandeperioder.csv`: grupp, typisk nyttjandeperiod (intervall och förslag i år), standardandel av byggnadens värde (procent, summerar till 100), källa. Verifiera mot BFN:s vägledning och branschpraxis, ange källa per rad, markera osäkra rader. Användaren kan alltid ändra värdena; tabellen är förslag.
4. Dela upp resten i uppgifter för den lokala modellen (se Del B).

## Del B (uppgifter för den lokala modellen)

Förslag, justera fritt. Högst 3 filer per uppgift och med test. Alla i paketet `se.tmconnect.styrelserummet.regler`.

- **Validera komponent**: `validera(k: Komponent, nuAr: Int): List<Fel>`. Fel vid nyttjandeperiod under 1, negativt värde, restvärde över anskaffningsvärde, anskaffningsår före 1800 eller efter nuAr + 1.
- **Årsavskrivning**: `avskrivningFor(k: Komponent, ar: Int): Long`. Linjär från anskaffningsåret, ett helt räkenskapsår per år, `(anskaffningsvarde - restvarde) / nyttjandeperiodAr`, avrundat till hela kronor; sista året tar resten så att summan blir exakt avskrivningsbart belopp. Före anskaffningsåret och efter sista året: 0. Exempel: 1 200 000 kr, år 2010, 40 år ger 30 000 per år. 100 000 kr, år 2020, 3 år ger 33 333 (2020), 33 333 (2021), 33 334 (2022).
- **Bokfört värde**: `bokfortVarde(k: Komponent, arsslut: Int): Long`. Anskaffningsvärde minus ackumulerade avskrivningar till och med `arsslut`, aldrig under restvärdet. Exempel: samma fönster, arsslut 2025: 1 200 000 − 16 × 30 000 = 720 000. arsslut 2009: 1 200 000.
- **Avstämning**: `stamAv(komponenter: List<Komponent>, byggnadensVarde: Long): Avstamning(summa: Long, differens: Long, stammer: Boolean)`. Stämmer när differensen är 0.
- **Fördela värde**: `fordela(total: Long, andelar: List<Pair<Komponentgrupp, Int>>): Map<Komponentgrupp, Long>` där andelarna är hela procent som summerar till 100. Summan av resultatet är exakt `total`; öresrest läggs på den största posten. Fel (exception med tydligt meddelande) om andelarna inte summerar till 100.
- **Summering per grupp**: `summeraPerGrupp(komponenter, arsslut): List<GruppSumma(grupp, anskaffning, avskrivningAret, bokfort)>`, sorterad i gruppernas ordning.
- **CSV-export**: `tillCsv(komponenter, arsslut): String`. Semikolon som skiljetecken (öppnas rätt i svensk Excel), rubrikrad på svenska, citattecken runt fält som innehåller semikolon, citattecken eller radbrytning (citattecken dubbleras), heltal utan tusentalsavgränsare, sista raden "UTKAST. Beslutas av förening och revisor." Test på alla tre citatfallen.

## Klart när

- `gradle :styrelserummet:test` och övriga moduler är gröna i GitHub Actions.
- `K3.md` har källhänvisningar och en sektion "Osäkert" (får vara tom, men Cursor ska ha tittat).
- Standardtabellen summerar till 100 procent och varje rad har källa.

## Gör inte

- Lägga till Android, nätverk, AI eller någon server. Det kommer i nästa uppdrag.
- Ändra `garantivalvet/`, `husloggen/`, `flextid/`, `prov-aviseringar/`, `rostfangst/` eller `:karna`.
- Skriva något om K3 från minnet utan källa.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`.
