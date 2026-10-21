# Uppdrag: Styrelserummet, gränsfall i K3-reglerna

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md, styrelserummet/APP.md och styrelserummet/regler/K3.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Kärnans regler (avskrivning, bokfört värde, summering, avstämning, CSV) är testade med runda exempel. Riktiga föreningar har gränsfall: komponenter som köpts mitt i året, som är fullt avskrivna, som saknar anskaffningsår, med mycket stora belopp, och rader som ska bli exakta heltalskronor utan flyttal. Det här uppdraget lägger till tester för gränsfallen och rättar de fel testerna hittar. Det är uppgiftsarbete för den lokala modellen.

## Beslut (Claude, 2026-10-21)

- Cursor delar upp i uppgifter (minst 12, högst 16) enligt MALL.md och CLAUDE.md punkt 4 och skriver ingen implementation själv. Cursor tar bara det som uppgiftsformatet inte klarar och säger då varför i PR-beskrivningen.
- Varje uppgift lägger till tester i en **ny** testfil (en testfil per område, så att uppgifterna inte krockar med dem som körs parallellt i kön: `GrupperaNamn`, `NyttjandeperiodForslag`, `Formatering`, `Sammanfattning`, `Jamforelse`, `FelHantering`, `Utskrift` får inte röras). Om ett nytt test hittar ett fel får uppgiften rätta det i högst en befintlig källfil, inom gränsen på 3 filer och cirka 150 rader.
- Uppgifterna är oberoende av varandra (`beror-på: ingen`).
- Inga nya beroenden. Inga ändringar i `Komponent` och `Forening`. Pengar är `Long` i hela kronor, aldrig flyttal.
- Skriv frågor till Claude under "Frågor till Claude" i PR-beskrivningen.

## Underlag (Cursor delar upp efter dessa områden)

1. **Avskrivning** (`regler/Avskrivning.kt`): anskaffningsår lika med räkenskapsåret (helt år eller del av år enligt `K3.md`), året efter sista året, komponent med nyttjandeperiod 1 år, komponent som redan är fullt avskriven, anskaffningsvärde 0, och att summan över alla år aldrig blir större än anskaffningsvärdet (avrundning, t.ex. 1 000 000 kr på 30 år).
2. **Bokfört värde** (`regler/BokfortVarde.kt`): aldrig negativt, aldrig över anskaffningsvärdet, rätt vid årsskifte, och med restvärde om det finns i modellen.
3. **Summering** (`regler/Summering.kt`): tom lista, en grupp, flera grupper, grupp utan komponenter ska inte synas, mycket stora belopp (nära `Long`-gränsen delas upp i test så att överflöde upptäcks), och att totalen alltid är lika med summan av grupperna.
4. **Avstämning** (`regler/Avstamning.kt`): exakt träff, skillnad på 1 kr, skillnad inom tolerans enligt koden, byggnadsvärde 0, negativ differens.
5. **CSV** (`server/CsvExport.kt` eller där `tillCsv` ligger): namn med semikolon, citattecken och radbrytning, svenska tecken, tal med hårda mellanslag får inte förekomma i talkolumner, rubrikrad och utkastrad finns, tom lista ger bara rubrik och utkastrad.
6. **Standardtabellen** (`src/main/resources/nyttjandeperioder.csv`): test som läser filen och kontrollerar att varje `Komponentgrupp` har en rad (utom de som uttryckligen saknar), att intervallen är rimliga (från ≤ förslag ≤ till) och att summan av värdeandelarna är 100 procent där koden förutsätter det.
7. **Tolkning av JSON** (`underlag/TolkaForslag.kt`): fall som robusthetsuppdraget inte täckte, till exempel okänd grupp i svaret (ska bli en tydlig anmärkning, inte undantag), dubbla komponenter i svaret, anskaffningsår som text, negativt belopp.

## Klart när

- Alla uppgifter är skrivna i `uppgifter/ny/` med exempel och test, och PR:en med dem är mergad.
- `gradle :styrelserummet:test :styrelserummet-server:test` är grönt när uppgifterna är gjorda.

## Gör inte

- Skriva implementationen eller testerna själv.
- Lägga till beroenden eller ändra modeller.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
