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

## Omprövning 2026-10-17: app 3

Mattias frågade 2026-10-17 vad app 3 ska vara. Samma dag beslutade han att flera appar får vara i utveckling samtidigt (BESLUTSLOGG.md). Claude prövade de idéer som var kvar och åtta nya mot samma kriterier.

Det som styrde utöver kriterierna:
- Pro tar bara bort gränsen på 15 poster (CLAUDE.md punkt 6). Appen behöver poster som samlas över tid och blir värda något som historik, medan den som har lite klarar sig gratis.
- A och G vann på ett svenskt pengavärde som kräver dokumentation (reklamationsrätten, förbättringsutgifter) och på svag svensk konkurrens.
- Husloggens skelett finns: återkommande uppgifter med intervall och påminnelse, logg med typ, belopp, kvitto och bilder, sammanställning per år och typ, PDF, CSV och säkerhetskopia. Påminnelser, bilder, säkerhetskopia och Pro ligger i `:karna`.
- Play gick inte att söka direkt från Claudes miljö. Konkurrensen bedömdes med webbsökning på Play-listningar och är därför ungefärlig.

| # | Idé | Bygg | Konk. | Betal | Upptäck | Återanv. | Risk | Summa |
|---|---|---|---|---|---|---|---|---|
| K | **Djurloggen** — hund och katt: vaccinationer, avmaskning, veterinärbesök, försäkring och kostnader, med påminnelser | 4 | 3 | 3 | 4 | 5 | 4 | **37** |
| L | Billoggen — service, besiktning, däck, reparationer och kostnader; underlag vid försäljning | 4 | 2 | 3 | 4 | 5 | 5 | 36 |
| Q | Avtalskollen — abonnemang och avtal med bindningstid och uppsägning | 5 | 2 | 2 | 3 | 4 | 5 | 35 |
| N | Dödsboet — frister, uppgifter, tillgångar, lösöre och utlägg; underlag för bouppteckning | 3 | 5 | 4 | 1 | 3 | 3 | 34 |
| O | Säljloggen — begagnatförsäljning: inköp, försäljning och vinst per år | 5 | 3 | 2 | 2 | 4 | 3 | 34 |
| R | Körjournal utan GPS | 4 | 1 | 4 | 5 | 3 | 4 | 34 |
| M | Flytten — checklista mot flyttdagen och flyttlådor med innehåll och foto | 4 | 3 | 2 | 3 | 3 | 5 | 33 |
| P | Hyresloggen — felanmälan, temperatur och störningar som underlag för hyresreduktion | 4 | 4 | 2 | 1 | 4 | 4 | 33 |
| C | Laddloggen | 4 | 2 | 3 | 3 | 3 | 5 | 33 |
| F | Flextid | 4 | 3 | 3 | 2 | 3 | 4 | 33 |

Varför de andra inte valdes:
- L: Svenska alternativ finns redan. Serviceboken har servicebok och påminnelser, Digital Servicebook är gratis för bilägaren och verkstäderna för in servicen, Opus Bilprovning påminner om besiktning, och Fuelio och Drivvo räknar kostnader. Påminnelser om besiktning skickar besiktningsföretagen redan.
- Q: Bankerna visar abonnemang som dras från kontot (Swedbanks Abonnemangshjälpen), och det finns många gratis appar för prenumerationer. Få hushåll har fler än 15 avtal.
- N: Störst pengavärde och ingen app, men ingen söker efter det i Play, appen används en gång, och innehållet (ärvdabalken) är tungt och känsligt.
- O: Gränsen 50 000 kr gäller vinst, inte försäljning, och de flesta som säljer begagnat når den inte. Den som köper för att sälja vidare driver verksamhet och behöver bokföring. Den som söker på Vinted eller Tradera hittar plattformarnas egna appar.
- R: Driversnote och många svenska företagstjänster finns, och den som för körjournal vill att GPS gör jobbet. Vi använder inte plats, och 15 resor räcker inte till en hel app.
- M: Används en gång och har svagt pengavärde. Engelska appar som Movemark (offline, lådor med QR-etiketter) gör redan detta.
- P: Få söker efter det i Play, och få användare får fler än 15 poster.

Andra rundan samma dag (Mattias bad om fler alternativ):

| # | Idé | Bygg | Konk. | Betal | Upptäck | Återanv. | Risk | Summa |
|---|---|---|---|---|---|---|---|---|
| S | Familjelånet — lån och gåvor inom familjen, avbetalningar, skuldebrev och gåvobrev; gåva till barn räknas som förskott på arv om inget annat sägs (ärvdabalken 6 kap.) | 5 | 5 | 3 | 1 | 3 | 3 | 38 |
| T | Förrådet — lådor i förråd, vind och garage med innehåll och foto; även vid flytt | 5 | 3 | 2 | 3 | 4 | 5 | 37 |
| U | Barnens kostnader vid växelvis boende — vem betalade vad, saldo per månad | 4 | 4 | 3 | 2 | 3 | 3 | 34 |
| V | Andrahandsuthyrning av egen bostad — hyra, kostnadsbaserad hyra, schablonavdrag | 4 | 4 | 3 | 1 | 3 | 3 | 33 |
| W | Hästjournalen — hovslagare, vaccination, avmaskning, läkemedelsjournal | 4 | 2 | 3 | 3 | 5 | 3 | 33 |
| X | Båtloggen — service, sjösättning, upptagning, kvitton; underlag vid försäljning | 4 | 3 | 2 | 2 | 5 | 5 | 34 |

- S får höga poäng men ingen söker efter det i Play, och vikten 1 för upptäckbarhet underskattar det för en portfölj utan marknadsföring. Bra som funktion eller senare app om vi får trafik från de andra.
- T är den billigaste appen hittills men saknar pengavärde, och Sortly med flera finns. Kandidat om vi vill ha en snabb app med Husloggens delar.
- U: Delad ekonomi kräver att båda föräldrarna ser samma data, vilket inte går utan nätverk. 2houses och OurFamilyWizard finns.
- V: Reglerna för privatuthyrning kan ändras 2026 och Qasa och Samtrygg sköter mycket åt hyresvärden.
- W: Svenska appar finns redan (Equikura med läkemedelsjournal enligt Jordbruksverket, SVA:s MASK-app, Stallkoll), och många ryttare är barn.
- X: Engelska motsvarigheter (Vessly: offline, kvitto, PDF inför försäljning) och liten svensk sökvolym.

Valet står kvar: K före L.

## Val app 3: K, Djurloggen (arbetsnamn)

Claude valde 2026-10-17. När bygget startar bestämmer Mattias (BESLUTSLOGG.md 2026-10-17).

**Varför:**
- Stor målgrupp som lägger pengar på djuren. Jordbruksverkets register hade drygt 1,15 miljoner hundar vid årsskiftet 2024/25 (cirka 90 % av alla), och katterna uppskattas till 1,6 miljoner. Cirka 90 % av hundarna och 65 % av katterna är försäkrade (Agria och SKK 2026).
- Ingen svensk app för djurets journal och kostnader hittades i Play. Det som finns på svenska är klinikernas appar (Evidensia: bokning och journalen från egna besök) och videoveterinär (FirstVet). Liknande appar finns på engelska, till exempel ApPET.
- Pengavärde: försäkringen kan sätta ned ersättningen om djuret inte är vaccinerat enligt rekommendationerna. Hedvigs förköpsinformation (2025) anger till exempel att djuret ska vara vaccinerat enligt Sveriges Veterinärförbunds riktlinjer och FASS, och att ersättningen kan sättas ned om villkoren inte följts. Journalen visar vad som gjorts och när, oavsett klinik. Kostnader per år, med det försäkringen ersatt, visar vad djuret faktiskt kostar.
- Svensk vinkel: id-märkning och registrering hos Jordbruksverket (hund sedan 2001, katt sedan 2023; bara drygt 650 000 katter är registrerade). Senare även resor: rabiesvaccination minst 21 dagar före avresa med EU-pass.
- Husloggens skelett används igen. Det nya är en tabell för djuren och ett val av djur överst.
- Gratisgränsen träffar rätt. Två djur ger på ett år fler än 15 poster (vaccinationer, avmaskningar, veterinärbesök, kostnader), medan den som bara vill ha påminnelser klarar sig gratis.
- Inga regler som ändras varje år. Skelettet kan bli nästa app (häst).

**Svagheter att känna till:**
- Pengavärdet är svagare än i A och G. Det finns ingen lagstadgad rätt, bara försäkringsvillkor och kostnadskoll.
- Konkurrensen är svag på svenska men stark på engelska, och klinikkedjornas appar kan växa.
- Vaccination och avmaskning är veterinärens sak. Färdiga förslag visas "enligt veterinärens råd" med intervall som går att ändra, aldrig som regel, och appen ger inga doser eller läkemedelsråd. Förslagen kontrolleras mot SVA:s och Sveriges Veterinärförbunds aktuella rekommendationer och de största försäkringsbolagens vaccinationsvillkor innan uppdraget för förslagen skrivs.
- Barn tycker om djur. Appen riktar sig till vuxna djurägare: målgrupp 18+ i Play Console och ingen design för barn.
- CLAUDE.md punkt 6 säger "ingen hälsodata". Claude läser det som användarens hälsa, inte djurets, och djurets journal lämnar aldrig telefonen. Mattias bekräftar tolkningen.
- Flera djur måste finnas i 1.0, till skillnad från flera bostäder i Husloggen.

## Omfång för Djurloggen 1.0

Gratis:
- Djur: namn, art (hund, katt, annat), ras, födelsedatum, chipnummer, registrerad hos Jordbruksverket, passnummer, försäkringsbolag, försäkringsnummer, förnyelsedatum och foto. Flera djur.
- Återkommande skötsel: namn, intervall, nästa datum och "markera som gjord" med ett tryck (skapar en post). Färdiga förslag med intervall som går att ändra: vaccination, avmaskning, fästing- och loppskydd, kloklippning, tandkontroll och förnyelse av försäkringen.
- Påminnelse före nästa datum.
- Logg: datum, typ (vaccination, behandling, veterinärbesök, försäkring, kostnad), vad, belopp, varav ersatt av försäkringen, klinik eller utförare, anteckning, kvitto och bilder.
- Sammanställning per djur, år och typ, totalt och egen kostnad. PDF "Journal" per djur (för veterinär, djurvakt, försäkringsbolag eller ny ägare), CSV och säkerhetskopia.
- Högst 15 poster i loggen. Djur, skötsel och påminnelser är obegränsade.

Pro (engångsköp, 49 kr): obegränsad logg.

Inte i 1.0: kvittoavläsning, vikt och viktkurva, självrisk per försäkringsperiod, avel och löp, foderschema, resor (rabies och EU-pass), delning, widgets, häst.

## Portföljordning

1. A Garantivalvet (funktionsfryst, väntar på Google Play)
2. G Husloggen (påbörjad 2026-10-16)
3. K Djurloggen (vald 2026-10-17). Bygget startar när Mattias bestämmer. B som egen app faller bort; mätarställningar kan bli en funktion i G.

Närmaste kandidat därefter: L Billoggen, med Laddloggen (C) som funktion.
