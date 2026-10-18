# Idéurval

Upprättat 2026-10-08 av Claude. Valet godkändes av Mattias 2026-10-08.

## Kriterier

Poäng 1–5 per kriterium, multiplicerat med vikten. Max 50.

| Kriterium | Vikt | Vad som ger 5 |
|---|---|---|
| Byggbar med lokal modell | 3 | Få skärmar, ren CRUD, inget nätverk, inga ovanliga API:er |
| Låg konkurrens | 2 | Få bra appar för just detta, särskilt på svenska |
| Betalvilja | 2 | Löser ett problem som kostar användaren pengar eller tid |
| Upptäckbarhet | 1 | Tydliga sökord som folk faktiskt skriver i Play |
| Återanvändning i portföljen | 1 | Mönstret (lista + datum + påminnelse + export) kan bli nästa app |
| Låg policyrisk | 1 | Inga känsliga behörigheter, ingen hälsodata, inte barn som målgrupp |

Underlag: små appar tjänar i regel mest på freemium med engångsköp eller prenumeration; reklam ger en liten app i storleksordningen några hundralappar i månaden och kräver tusentals dagliga användare. Betalda appar utan publik säljer dåligt. Därför bedöms alla idéer som freemium med ett engångsköp (Pro), utan reklam.

## Idéer

| # | Idé | Bygg | Konk. | Betal | Upptäck | Återanv. | Risk | Summa |
|---|---|---|---|---|---|---|---|---|
| A | **Garantivalvet** — kvitton, garantier och reklamationsfrister med påminnelser | 4 | 4 | 3 | 3 | 5 | 5 | **39** |
| B | Mätarställning — el, vatten, fjärrvärme; förbrukning per period | 5 | 3 | 2 | 3 | 4 | 5 | 37 |
| G | Husets underhållslogg — filter, sotning, OVK, takrännor; återkommande påminnelser | 4 | 3 | 3 | 3 | 5 | 5 | 37 |
| C | Laddloggen — elbilsladdning hemma och publikt, kostnad, kWh/100 km | 4 | 3 | 3 | 3 | 3 | 5 | 35 |
| F | Flextid — flex- och komptid efter svenska avtalsregler | 4 | 3 | 3 | 2 | 3 | 4 | 33 |
| J | Packlistor — återanvändbara listor för resor | 5 | 1 | 1 | 2 | 4 | 5 | 30 |
| D | Frysinventering med bäst före | 4 | 1 | 2 | 2 | 4 | 5 | 29 |
| E | Glosförhör för skolbarn | 4 | 2 | 2 | 3 | 3 | 2 | 28 |
| H | Elpris per timme | 3 | 1 | 1 | 3 | 2 | 3 | 21 |
| I | Sopkalender | 2 | 3 | 1 | 3 | 1 | 3 | 20 |

Avförda direkt: H och I kräver nätverk och extern data som ändras, vilket bryter mot direktivet om offline och gör underhållet löpande. E riktar sig till barn och drar in Googles familjepolicy.

## Val: A, Garantivalvet

**Varför:**
- Konkret pengavärde. Den som har kvittot och vet att reklamationsfristen inte har gått ut får pengar tillbaka. Det gör ett engångsköp lätt att motivera.
- Svensk vinkel. Generella garanti-appar finns, men få som räknar med konsumentköplagens reklamationsrätt i tre år. Det ger sökord som "reklamation", "kvitto" och "garanti" på svenska med svagare konkurrens än på engelska.
- Byggbar i små steg: Room-tabell, lista, formulär, foto via systemets väljare, en daglig kontroll i WorkManager och export till PDF med Androids inbyggda `PdfDocument`.
- Mönstret återanvänds. B och G är samma skelett (post + datum + påminnelse + export) och blir app 2 och 3.

**Svagheter att känna till:**
- Användaren måste komma ihåg att fotografera kvittot vid köpet. Appen måste göra det snabbt, annars används den inte.
- Det juridiska innehållet måste vara korrekt och formulerat som information, inte rådgivning. Reglerna gäller Sverige; i andra länder visar appen bara garantitiden.
- Fristerna (reklamation inom tre år från mottagandet, och hur länge ett fel antas ha funnits vid köpet) ska kontrolleras mot lagtexten i konsumentköplagen (2022:260) på riksdagen.se innan uppdraget för regelmotorn skrivs. Siffrorna i appen hämtas från ett enda ställe i koden, med lagrumshänvisning.

## Omfång för version 1.0

Gratis:
- Lägg till köp: vad, var, datum, pris (valfritt), garantitid i månader, foto av kvittot, anteckning.
- Lista sorterad efter närmaste frist, med status: garanti gäller, reklamationsrätt gäller, utgånget.
- Påminnelse 30 dagar innan en garanti eller reklamationsfrist går ut (dagligt jobb, notis).
- Visa kvittot i helskärm och dela bilden.
- Läs av kvittot på enheten och föreslå butik, datum och pris i formuläret (tillagt 2026-10-12, uppdrag `2026-10-12-kvittoavlasning`).
- Högst 15 köp.

Pro (engångsköp, förslag 49 kr):
- Obegränsat antal köp.
- Reklamationsunderlag: PDF med kvittot och köpuppgifterna, färdig att mejla till butiken.
- Säkerhetskopia till och från fil som användaren väljer.

Inte i 1.0: molnsynk, delning mellan användare, widgets.

## Omprövning 2026-10-16: app 2

Mattias beslutade 2026-10-15 att app 2 ska påbörjas medan Garantivalvet väntar på Google. Claude prövade om ordningen ovan fortfarande gäller.

Vad som har ändrats sedan 2026-10-08:
- Garantivalvet växte från sex punkter till ett trettiotal, och det tunga gjordes av Claude och Cursor, inte av den lokala modellen. Bygg-kriteriet väger därför tyngre i praktiken, och app 2 får ett snävt 1.0.
- Delarna som kan återanvändas finns nu, men i `:garantivalvet` (kvittofoto och avläsning, påminnelser, PDF, säkerhetskopia, mörkt läge, välkomstskärm). `:karna` har bara tema och Pro.
- Garantivalvet har ingen marknadsdata än. Valet bygger fortfarande på bedömningar.
- B: el och fjärrvärme läses av på distans i Sverige, och nätbolagens appar visar förbrukningen. Behovet av att läsa av själv är litet.
- C: bilens och laddboxens appar visar redan kWh och kostnad.
- G: den som säljer sin bostad får dra av förbättringsutgifter vid vinstberäkningen (inkomstskattelagen 45 kap. 11–12 §§ för fastighet; minst 5 000 kr per år; förbättrande reparationer och underhåll bara de senaste fem åren före försäljningen). En logg med kvitton har alltså ett konkret pengavärde, på samma sätt som reklamationsrätten i A. Dokumenterat underhåll är också ett underlag för köparen. På engelska finns många nya underhållsappar 2026; på svenska använder villaägare Excel, kalendern eller Todoist (byggahus.se).

| # | Idé | Bygg | Konk. | Betal | Upptäck | Återanv. | Risk | Summa (2026-10-08) |
|---|---|---|---|---|---|---|---|---|
| G | **Husloggen** — husets underhåll med påminnelser, och logg med kvitton som underlag vid försäljning | 4 | 4 | 4 | 3 | 5 | 5 | **41** (37) |
| C | Laddloggen | 4 | 2 | 3 | 3 | 3 | 5 | 33 (35) |
| F | Flextid | 4 | 3 | 3 | 2 | 3 | 4 | 33 (33) |
| B | Mätarställning | 5 | 2 | 1 | 2 | 4 | 5 | 32 (37) |

## Val app 2: G, Husloggen (arbetsnamn)

**Varför:**
- Störst återanvändning: kvittofoto med avläsning, påminnelser, PDF, säkerhetskopia och Pro finns redan och har provats på telefon.
- Konkret pengavärde i Sverige (förbättringsutgifter) och en tydlig säljrad: dokumentera nu, betala mindre skatt och sälj lättare sen.
- Samma köpare som A (den som har ordning på kvitton och sina pengar), så apparna kan hänvisa till varandra.
- Inget nätverk, ingen plats, inga känsliga behörigheter.

**Svagheter att känna till:**
- Betalviljan hänger på historiken, vars värde ofta ligger år fram i tiden. Påminnelser ensamma säljer inte.
- Den verkliga konkurrenten är kalendern. Att lägga in en uppgift måste gå snabbare än där: färdiga förslag och "markera som gjord" med ett tryck.
- Skattereglerna kontrolleras mot Skatteverket och lagtexten innan uppdraget för sammanställningen skrivs, och visas som information, inte rådgivning.

## Omfång för Husloggen 1.0

Gratis:
- Återkommande uppgifter: namn, intervall i månader, nästa datum, markera som gjord med ett tryck. Färdiga förslag att välja (filter, takrännor, brandvarnare, golvbrunnar, värmepump m.fl.) med intervall som går att ändra; sotning, brandskyddskontroll och service anges "enligt kommunen" eller "enligt tillverkaren", inte som lagkrav.
- Logg över utfört arbete och utgifter: datum, vad, typ (underhåll, reparation, ny-, till- eller ombyggnad), belopp, utförare, anteckning, kvittofoto med avläsning, bilder.
- Påminnelse före nästa datum.
- Sammanställning per år och typ; i Sverige underlag för förbättringsutgifter vid försäljning. PDF "Husets historik", CSV och säkerhetskopia.
- Högst 15 poster i loggen. Uppgifter och påminnelser är obegränsade.

Pro (engångsköp, 49 kr): obegränsad logg.

Inte i 1.0: flera bostäder, mätarställningar, dokument (manualer, ritningar), plan för framtida kostnader, delning, widgets.

## Portföljordning

1. A Garantivalvet (funktionsfryst, väntar på Google Play)
2. G Husloggen (påbörjad 2026-10-16)
3. Väljs när Mattias vill, utan väntetid efter publicering. B som egen app faller bort; mätarställningar kan bli en funktion i G.

## Potentiella idéer (inte beslutade)

- 2026-10-18: **Djurloggen** (arbetsnamn), journal och kostnadslogg för hund och katt: vaccinationer, avmaskning, veterinärbesök med kvitto, påminnelser, kostnad per år efter försäkringens ersättning. 37 poäng i molnsessionens prövning av tio idéer; logg för bilen tvåa med 36, i praktiken lika. Argument: stor målgrupp, mest återanvändning av Husloggen, försäkringsvillkor kan kräva vaccinationer enligt rekommendation. Invändningar: språket är ingen vallgrav mot engelska gratisappar, försäkringsbolagen kan bygga samma sak, låg betalvilja för djurappar, risk för Googles policy om repetitivt innehåll om apparna blir för lika. Siffrorna i underlaget är inte kontrollerade. Startas inte förrän de gemensamma delarna ligger i `:karna`.

## Idéurval 2 (2026-10-18): efter research

Underlag: sju research-områden i claude.ai-projektet (`research/01`–`04`, `06`–`08`; 05 struket tills vidare). Resultaten ligger i projektet, inte i repot.

### Vad researchen säger
- En ny konsumentapp når 650 000 kr/år med under 5 % sannolikhet. Android med freemium är den svagaste kombinationen (0,9 % konvertering mot 10,7 % med hård betalvägg). Garantivalvet och Husloggen räknas därför som övning och portfölj, inte som vägen till målet.
- B2B når målet med 40–200 kunder. Hindret är försäljning och förtroende, inte kod.
- Billig kod är ingen fördel längre. Fördelen måste vara domänkunskap, ett tvång hos kunden (regel, tidsfrist) eller en kanal.
- De bästa luckorna finns där användaren inte är köparen (skolplattformar) eller där en regel kräver omdöme som plattformarna saknar.
- Alla områden rekommenderar samma metod: testa manuellt (Claude gör tjänsten för hand) eller med en landningssida innan något byggs.

### Kortlista

| # | Idé | Kund | Varför | Största risk | Test före bygge |
|---|---|---|---|---|---|
| 1 | Styrelserummet: K3-assistent som ingång, sedan AI-sekreterare (protokoll, kallelser, frågor mot stadgarna) | Självförvaltade brf och samfälligheter (30 000 + 40 000) | K3 tvingande, första årsredovisningen våren 2027; ingen svensk AI-produkt för styrelser; AI gör jobbet | Boappa (2 000+ föreningar) lägger till AI; revisorn gör K3; andel självförvaltade okänd | Landningssida och Google-annonser nov 2026–feb 2027; manuellt protokolltest med 5–10 styrelser |
| 2 | Bevispaket för NIS2-leverantörer: intervju ger policy, riskanalys, incidentrutin och svar på kunders säkerhetsformulär | Företag med 5–49 anställda som levererar till verksamheter under cybersäkerhetslagen | Mattias kunskap; kunden har ett tvång; 70–80 % av frågorna överlappar | EU minskar kedjekraven; Cyberday är billigt; förtroende säljer | 10 intervjuer, sedan erbjud 5 företag ett manuellt paket för 2 500 kr. Tre betalande av fem = gå vidare |
| 3 | Skolinkorgen: en inkorg och kalender för förskola, skola och fritid ur aviseringar på telefonen | Föräldrar i Sverige | Plattformarna har usla betyg; Öppna skolplattformen visar betalvilja; allt kan tolkas lokalt | Plattformarna ändrar sig; Play-policy för notisavläsning; bara Android | Teknisk logg av aviseringar på Mattias telefon i två veckor |
| 4 | Röstfångst: säg det, så hamnar det i kalender, uppgifter eller svar | Mattias först, sedan yrkesgrupper | Mattias egen smärta; Gemini svag på påminnelser 2026 | Systemassistenterna blir bra inom 12–24 mån; trångt | Minsta Android-version, Mattias använder den 30 dagar |

Parkerade: reparationsstöd i Garantivalvet 1.1 (när svensk lag finns), sekretessäker offline-anteckning, upphandlingsdata för agenter (MCP), Lens-ersättare, budget utan bank, röst till ÄTA för hantverkare (mättat enligt område 06), lönekartläggning, Djurloggen. Avförda: säkerhetsskyddsstöd (för nära Mattias anställning), sprutjournal och idrottsmedlemssystem (gratis alternativ finns).

### Gränser
- Inga kunder där Mattias arbetsgivare har tillsyn eller avtal, och inget som rör säkerhetsskyddsklassade uppgifter.
- Tekniska vägval från område 08 (funktioner med tydliga in- och utdata, lokal AI först med moln som reserv, persondata lokalt) gäller när en idé går till bygge.
