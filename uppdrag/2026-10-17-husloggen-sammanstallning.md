# Uppdrag: Husloggen, sammanställning och underlag för försäljning

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och husloggen/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Loggen är underlag när bostaden säljs (APP.md). Det här uppdraget lägger till en sammanställning per år och typ, ett underlag som visar vilka poster som kan vara förbättringsutgifter vid försäljning, och export som PDF ("Husets historik") och CSV. Garantivalvets PDF- och CSV-export (`:karna`, `garantivalvet/`) är förebild. Garantivalvet är funktionsfryst och får inte ändras. Delar som båda behöver lyfts till `:karna` av Cursor.

Uppdraget skickas i förväg medan andra Husloggen-uppdrag kan vara i kö (Mattias 2026-10-16). Håll ändringarna små i `HusViewModel.kt` och `MainActivity.kt` och skriv konflikter under "Frågor till Claude".

## Skatteregler (kontrollerade mot Skatteverket 2026-10-17)

Visas som information, aldrig som råd, bara för Sverige. Siffrorna ligger på ett enda ställe i `regler/` med källa och kontrolldatum:

- Förbättringsutgifter (nybyggnad, tillbyggnad, ombyggnad och förbättrande reparation och underhåll) dras av vid beräkning av vinst vid försäljning av småhus, ägarlägenhet och bostadsrätt.
- Förbättringsutgifter räknas bara för de kalenderår då de sammanlagt uppgår till minst 5 000 kr.
- Reparation och underhåll räknas bara om utgiften gjordes under försäljningsåret eller de fem föregående åren (femårsregeln). Ny-, till- och ombyggnad omfattas inte av femårsgränsen.
- Avdrag får bara göras för den del som gör att bostaden är i bättre skick vid försäljningen än vid köpet; hänsyn tas till slitage. Appen bedömer inte detta, utan anger att användaren själv gör bedömningen. Har samma byggnadsdel reparerats flera gånger räknas bara den senaste utgiften.
- Källor: Skatteverket, "Försäljning av småhus eller ägarlägenhet" och "Reparationer och underhåll". Hjälpblankett SKV 2197. Lagrum: inkomstskattelagen 45 kap. (privatbostadsfastigheter) och 47 kap. (bostadsrätter). **Cursor verifierar kapitelnumren mot riksdagen.se innan de skrivs i appen och rättar raden i APP.md (som nu säger 46 kap. för bostadsrätt) om den är fel.** Rapportera i PR-beskrivningen.
- Appen anger aldrig ett skattebelopp och räknar inte ut vinsten.

## Uppdraget görs i två PR:er (ARBETSFLODE.md, "Uppdrag i två steg")

**PR 1, bara uppgiftsfiler.** Dela upp del B i uppgifter i `uppgifter/ny/` enligt `uppgifter/MALL.md` och CLAUDE.md punkt 4, med `beror-på` där de bygger på din kod. Öppna PR 1 först.

**PR 2, del A.** Frågor under "Frågor till Claude" i PR-beskrivningen.

## Del A (Cursor)

1. **Regler (`regler/`):** ren Kotlin utan Android. `summeraPerAr(poster)` ger summa i öre per år och typ. `kanVaraForbattring(post, forsaljningsar)` ger en av `Ja`, `KravBedomning` eller `Nej` enligt reglerna ovan: ny-, till- och ombyggnad → `Ja`; underhåll/reparation inom försäljningsåret och fem år bakåt → `KravBedomning`; äldre underhåll/reparation → `Nej`. `arStodjerGransbelopp(poster, ar)` är sant när årets förbättringsutgifter är minst 500000 öre. Exempel: försäljningsår 2026, reparation 2021-06-01 → `KravBedomning`; reparation 2020-12-31 → `Nej`; ombyggnad 2015 → `Ja`; år med 4999 kr → falskt, 5000 kr → sant.
2. **Export:** lyft den gemensamma PDF- och CSV-koden till `:karna` om den inte redan ligger där, utan att ändra Garantivalvets beteende. Testerna för alla appmoduler ska vara gröna.
3. **PDF "Husets historik":** poster per år med datum, beskrivning, typ, belopp, och summor per år och typ. Skatteavsnittet står sist, märkt som information. CSV med samma poster. Export är alltid gratis (CLAUDE.md punkt 6).
4. **Manifest:** ingen ny behörighet. Lista behörigheterna i PR-beskrivningen.

## Del B (uppgifter för den lokala modellen)

Förslag som du får justera. Högst 3 filer per uppgift och med test.

- **Strängar** (svenska och engelska) för fliken, skatteinformationen och exporten. Informationstexten: "Förbättringsutgifter kan dras av när bostaden säljs. Reparation och underhåll räknas bara för försäljningsåret och de fem åren före. Utgifterna ska sammanlagt vara minst 5 000 kr per år. Det här är information, inte skatteråd. Kontrollera alltid med Skatteverket." Test som `StringsNycklarTest`.
- **Flik eller skärm "Sammanställning":** år i listan, summor per typ, totalsumma. Välj försäljningsår (standard innevarande år) och visa per post `Ja`, `Kräver bedömning` eller `Nej` enligt regel 1. Tryckytor minst 48 dp, kontrast enligt CLAUDE.md.
- **Exportknappar** för PDF och CSV som anropar del A.
- **Utanför Sverige:** visa bara summor per år och typ, ingen skatteinformation (APP.md).

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Testerna täcker exemplen ovan, inklusive gränsfallen 2020-12-31 och 4999 kr.
- Ingen ny behörighet. Databasschemat är oförändrat. Ingen fil i `garantivalvet/` ändrar beteende.
- Kapitelnumren i `regler/` och APP.md är verifierade mot riksdagen.se.
- Uppgifterna för del B ligger i `uppgifter/ny/` via PR 1.

## Gör inte

- Ändra databasschemat eller Garantivalvets beteende.
- Ange skattebelopp, räkna ut vinst eller ge råd om vad användaren bör dra av.
- Lägga till beroenden utöver dem som finns i `:karna`.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
