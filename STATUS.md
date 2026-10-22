# Status

Uppdaterad 2026-10-22 morgon.

## Klart: Grundplattan (2026-10-08–09)

Flödet går hela vägen utan att Mattias rör koden: Claude skriver uppdrag, Cursor delar upp och gör det tunga, den lokala modellen (Aider + Ollama `qwen3-coder:30b`) skriver uppgifterna, Cursor granskar, workflowet `cursor-beslut` mergar, Claude granskar i efterhand. Se ARBETSFLODE.md.

På Mattias dator: körskriptet i Schemaläggaren (när datorn är oanvänd 10 min, och kl. 01), startfilen "Starta lokal modell" och panelen "Appfabriken panel" på skrivbordet.

## Väntar: Garantivalvet 1.0

Funktionsfryst 2026-10-16 (BESLUTSLOGG.md). Inget nytt byggs förrän appen är publicerad. Fel från Mattias prov och från Googles granskning går före allt annat i kön.

- Klart: köp, lista och formulär; kvittofoto och avläsning på enheten (ML Kit, alla fyra rotationer, kända kedjor, öppet köp och garantitid ur kvittot); påminnelser; reklamationsrätt enligt konsumentköplagen; gratisgräns 15 köp och Pro i `:karna`; reklamationshjälp, reklamationslogg och uppföljning; PDF, CSV och hemsäkringsunderlag; säkerhetskopia, import och automatisk säkerhetskopia; sök och filter; mörkt läge; välkomstskärm; ny design; release-bygge (targetSdk 36, R8, workflow Release, `butik/PUBLICERA.md`); butiksmaterial i `garantivalvet/butik/`. Detaljer i BESLUTSLOGG.md.
- Kvar, Mattias:
  1. Prova på telefon (APK från `senaste-main`): kvittoavläsning med riktiga kvitton, rotera, Välj PDF, Reklamera med bilder och AI-hjälp, automatisk säkerhetskopia, hemsäkringsunderlag, välkomstskärmen.
  2. Integritetspolicyn på en publik adress (FRAGOR.md).
  3. Play Console: kontot godkänns (pågår), internt testspår, produkten `pro`, prova köpet, butikstexter och Data safety.
- Efter publicering (1.1): byt till de delar som Husloggen har lyft till `:karna`.

## Nu: Husloggen 1.0 (app 2)

Val och omfång i IDEURVAL.md (omprövning 2026-10-16), regler i `husloggen/APP.md`. Uppdragen skrivs i förväg, flera i taget, så att Cursor och den lokala modellen alltid har arbete i kön (Mattias 2026-10-16, ersätter "ett uppdrag i taget"). Uppdrag som rör samma filer (`HusViewModel.kt`, `MainActivity.kt`) lägger Cursor så att konflikterna blir små. Spåret är fortfarande ett i taget: bara Husloggen är i aktiv utveckling (CLAUDE.md punkt 10).

1. `2026-10-16-husloggen-grund`: modul, databas version 1, flikarna Att göra och Logg. Klart 2026-10-16.
2. Uppgifter: formulär, intervall och nästa datum, färdiga förslag, markera som gjord med ett tryck (skapar en post). Klart 2026-10-16.
3. Loggen: formulär för utfört arbete och utgifter, lista per år, detaljvy, ändra och ta bort. Klart 2026-10-16.
4. Kvittofoto och bilder på poster (flera bilder, `BildLager` i `:karna`). `2026-10-16-husloggen-bilder`, del A mergad, uppgifter pågår. Avläsning (OCR) är inte med; tas i eget uppdrag om den behövs.
5. Påminnelser före nästa datum. Cursor lyfter påminnelsemönstret till `:karna`. `2026-10-16-husloggen-paminnelser`, skickat 2026-10-16.
6. Gratisgräns (15 poster) och Pro. `2026-10-16-husloggen-pro-gratisgrans`, skickat 2026-10-16.
7. Sammanställning per år och typ, underlag för förbättringsutgifter vid försäljning, PDF "Husets historik" och CSV. Skattereglerna kontrollerade mot Skatteverket 2026-10-17 (5 000 kr per år, femårsregeln för reparation och underhåll). `2026-10-17-husloggen-sammanstallning`, skickat 2026-10-17. Kapitelnumren i APP.md (46 kap. för bostadsrätt kan vara fel, troligen 47 kap.) verifierar Cursor.
8. Säkerhetskopia, import och automatisk säkerhetskopia. Cursor lyfter dem till `:karna`. `2026-10-16-husloggen-sakerhetskopia`, skickat 2026-10-16.
9. Mörkt läge, välkomstskärm, butiksmaterial, skärmbilder, Data safety och integritetspolicy. Klart 2026-10-21 (Claude): butikstexter sv/en, Data safety, funktionsgrafik, skärmbilder, policy som webbsida (publiceras när GitHub Pages är påslaget). Navigeringen har fyra knappar (Att göra, Logg, Översikt, Mer).
10. Mattias provar på telefon.

2026-10-22 (Claude): sök och filter i loggen, jämförelse mot året innan och årsprognos för mätare, Kommande år. Köat: senaste kostnad per uppgift och uppskattad kostnad för kommande tolv månader.

## Nu: Styrelserummet (webbtjänst för bostadsrättsföreningar)

Mattias 2026-10-19: bygg tjänsten istället för att vänta på landningssidans test. Första produkten är K3-komponentlistan ur underhållsplan och årsredovisning (utkast till revisor). Egna regler i `styrelserummet/APP.md` (webb, INTERNET; CLAUDE.md punkt 5 och 6 gäller Android-apparna). Landningssidan `webb/k3/sida.html` ligger kvar och mäter intresset under tiden.

1. `2026-10-19-styrelserummet-k3-karna`: modul `:styrelserummet`, datamodell, K3-regler, standardtabell. Mergad (PR #277, 2026-10-20).
2. `2026-10-19-styrelserummet-underlag`: PDF till text, maskning, tolkning via Anthropic, `byggUtkast`. Mergad (PR #278). Cursor tog uppdraget direkt, trots `uppdrag/vantar/`, och skrev själv de hjälpfunktioner som uppgifterna 01, 04, 05 och 08-12 gällde; de är flyttade till `uppgifter/klar/`. Kvar för den lokala modellen: 02 årsavskrivning, 03 bokfört värde, 06 summering per grupp, 07 CSV.
3. Webbtjänsten: mergad (PR #287, uppdraget `2026-10-19-styrelserummet-webb-server`): Ktor-server, uppladdning, redigerbar tabell, CSV, utskrift, `/integritet`, Dockerfile, `styrelserummet/DRIFT.md`. De lokala uppgifterna 01, 02, 03, 06 och 07 är klara och mergade. Dubbletten #290 stängd, #279 stängd.
   Före driftsättning: `2026-10-20-styrelserummet-driftklar` mergat (PR #292, 2026-10-20); klientadressen läses från sista adressen i `X-Forwarded-For` (PR #293, mergad). Robusthetsuppdraget (tåligare läsning av riktiga handlingar) är klart: 26 lokala uppgifter mergade natten 20-21 oktober. Nästa: `2026-10-21-styrelserummet-kvalitet` (gruppering på namn, formatering, sammanfattning i klartext, jämförelse mot föregående år, utskriftsdata, felmeddelanden) skickat 2026-10-21 så att kön inte står tom. Kvar är Mattias steg (FRAGOR.md) och att provköra med riktig API-nyckel och riktiga handlingar (proxyadress, säkerhetshuvuden, `/halsa`, inga länkar i loggar). Bakgrund: servern ser bara proxyns adress bakom en värd, så gränsen 5 jobb per IP och timme måste läsa `X-Forwarded-For` från värdens proxy, annars delar alla besökare på gränsen. Oanvända beroenden (`ktor-server-cors`, `call-logging`) tas bort. Kontrollera att länkarna (hemliga) inte hamnar i loggar.
   Läget 2026-10-21 kväll: kvalitet, gränsfall, felhantering och kopplingen av kvalitetsfunktionerna är mergade (249 klara uppgifter, kön tom). Nästa: `2026-10-21-styrelserummet-provklar` (miljökontroll vid start, kontrast, påhittade handlingar genom hela flödet, lagringens robusthet, dataminimering, kostnadsskydd) skickat så att kön inte står tom i natt och så att den första riktiga provkörningen går att lita på.
   2026-10-22 dag (Claude): avgiftskalkyl `/avgift` (#531), kontroll av underlaget på nyckeltalssidan (#535).
   2026-10-22 natt (Claude): betalning med Stripe Checkout byggd och mergad (av tills nyckel finns, se DRIFT.md); utskriften rättad för CSP; pris på startsidan. Gratisverktygen Nyckeltal och Fondplan mergade (#490, skydd och SEO #491). Avskrivningsplanen finns i utskriften och som CSV (#492). Värd (Fly.io, Stockholm) beslutad men inte incheckad, väntar på Mattias.
   2026-10-21 kväll (Claude): avskrivningsplan tio år framåt, utrangering vid utbyte och plan som CSV, som uppgifter för den lokala modellen; Claude kopplar in planen i utskrift och CSV-rutt.
4. Därefter, Mattias (se FRAGOR.md): bolagsuppgifter till integritetssidan, värd (konto), `ANTHROPIC_API_KEY` som hemlighet, betalkonto, domän. Betalning blir eget uppdrag.

## Nu: Flextid (app 3, byggd av Cursor 2026-10-18)

Grund, Pro, påminnelser, PDF och butiksmaterial finns. 2026-10-21/22 (Claude): svenska helgdagar, gå hem-tid, semesterår och månadens normaltid mergade (PR #448); saldot rör sig inte under en påbörjad dag. Vecka och månad visar arbetat av normaltid (#493). Release-bygget provkört. 2026-10-22: årsöversikt per månad (saldo vid månadens slut, CSV) köad för den lokala modellen. Publiceringsguide i `flextid/butik/PUBLICERA.md`. Integritetspolicy skriven. Kvar: Mattias provar på telefon; appnamn och applicationId vid publicering.

## Pausat (Mattias 2026-10-19)

- Skolinkorgen (`:prov-aviseringar`) och Röstfångst (`:rostfangst`): elva uppgifter ligger i `uppgifter/pausad/`. Del A är mergad (PR #273 och #274), modulerna finns kvar. Återupptas genom att flytta tillbaka uppgifterna till `uppgifter/ny/`.
- NIS2-paketet: avfört (konkurrerar med Mattias arbete).

## Senare

- Husloggen efter 1.0: mätarställningar, flera bostäder, dokument (manualer, ritningar), plan för framtida kostnader.
- App 3 väljs efter testerna i Idéurval 2.
- Mätaren i panelen (`release.json`) följer Garantivalvet tills den är publicerad. Claude uppdaterar den när ett steg ändras.
