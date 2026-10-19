# Status

Uppdaterad 2026-10-19.

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
9. Mörkt läge, välkomstskärm, butiksmaterial, skärmbilder, Data safety och integritetspolicy. Nästa att skriva.
10. Mattias provar på telefon.

## Nu: Styrelserummet (webbtjänst för bostadsrättsföreningar)

Mattias 2026-10-19: bygg tjänsten istället för att vänta på landningssidans test. Första produkten är K3-komponentlistan ur underhållsplan och årsredovisning (utkast till revisor). Egna regler i `styrelserummet/APP.md` (webb, INTERNET; CLAUDE.md punkt 5 och 6 gäller Android-apparna). Landningssidan `webb/k3/sida.html` ligger kvar och mäter intresset under tiden.

1. `2026-10-19-styrelserummet-k3-karna`: modul `:styrelserummet` (ren Kotlin/JVM), datamodell, avskrivningsregler, avstämning, CSV. Cursor verifierar K3-reglerna mot BFN:s text. Skickat 2026-10-19.
2. `uppdrag/vantar/2026-10-19-styrelserummet-underlag`: läsa PDF, maska personuppgifter, tolkning via Anthropic, utkast. Skickas när 1 är mergat.
3. `uppdrag/vantar/2026-10-19-styrelserummet-webb`: Ktor-server, uppladdning, redigerbar tabell, CSV och utskrift, `/integritet`, Dockerfile. Skickas när 2 är mergat.
4. Därefter, Mattias: värd (konto), `ANTHROPIC_API_KEY` som hemlighet, betalkonto, domän. Betalning blir eget uppdrag.

## Pausat (Mattias 2026-10-19)

- Skolinkorgen (`:prov-aviseringar`) och Röstfångst (`:rostfangst`): elva uppgifter ligger i `uppgifter/pausad/`. Del A är mergad (PR #273 och #274), modulerna finns kvar. Återupptas genom att flytta tillbaka uppgifterna till `uppgifter/ny/`.
- NIS2-paketet: avfört (konkurrerar med Mattias arbete).

## Senare

- Husloggen efter 1.0: mätarställningar, flera bostäder, dokument (manualer, ritningar), plan för framtida kostnader.
- App 3 väljs efter testerna i Idéurval 2.
- Mätaren i panelen (`release.json`) följer Garantivalvet tills den är publicerad. Claude uppdaterar den när ett steg ändras.
