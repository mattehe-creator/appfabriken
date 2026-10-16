# Status

Uppdaterad 2026-10-16.

## Klart: Grundplattan (2026-10-08–09)

Flödet går hela vägen utan att Mattias rör koden: Claude skriver uppdrag, Cursor delar upp och gör det tunga, den lokala modellen (Aider + Ollama `qwen3-coder:30b`) skriver uppgifterna, Cursor granskar, workflowet `cursor-beslut` mergar, Claude granskar i efterhand. Se ARBETSFLODE.md.

På Mattias dator: körskriptet i Schemaläggaren (när datorn är oanvänd 10 min, och kl. 01), startfilen "Starta lokal modell" och panelen "Appfabriken panel" på skrivbordet.

## Väntar: Garantivalvet 1.0

Funktionsfryst 2026-10-16 (BESLUTSLOGG.md). Inget nytt byggs förrän appen är publicerad. Fel från Mattias prov och från Googles granskning går före allt annat i kön.

- Klart: köp, lista och formulär; kvittofoto och avläsning på enheten (ML Kit, alla fyra rotationer, kända kedjor, öppet köp och garantitid ur kvittot); påminnelser; reklamationsrätt enligt konsumentköplagen; gratisgräns 15 köp och Pro i `:karna`; reklamationshjälp, reklamationslogg och uppföljning; PDF, CSV och hemförsäkringsunderlag; säkerhetskopia, import och automatisk säkerhetskopia; sök och filter; mörkt läge; välkomstskärm; ny design; release-bygge (targetSdk 36, R8, workflow Release, `butik/PUBLICERA.md`); butiksmaterial i `garantivalvet/butik/`. Detaljer i BESLUTSLOGG.md.
- Kvar, Mattias:
  1. Prova på telefon (APK från `senaste-main`): kvittoavläsning med riktiga kvitton, rotera, Välj PDF, Reklamera med bilder och AI-hjälp, automatisk säkerhetskopia, hemförsäkringsunderlag, välkomstskärmen.
  2. Integritetspolicyn på en publik adress (FRAGOR.md).
  3. Play Console: kontot godkänns (pågår), internt testspår, produkten `pro`, prova köpet, butikstexter och Data safety.
- Efter publicering (1.1): byt till de delar som Husloggen har lyft till `:karna`.

## Nu: Husloggen 1.0 (app 2)

Val och omfång i IDEURVAL.md (omprövning 2026-10-16), regler i `husloggen/APP.md`. Ett uppdrag i taget; nästa skrivs när förra uppdragets del A är mergad.

1. `2026-10-16-husloggen-grund`: modul, databas version 1, flikarna Att göra och Logg. Klart 2026-10-16.
2. Uppgifter: formulär, intervall och nästa datum, färdiga förslag, markera som gjord med ett tryck (skapar en post). Klart 2026-10-16.
3. Loggen: formulär för utfört arbete och utgifter, lista per år, detaljvy, ändra och ta bort. Klart 2026-10-16.
4. Kvittofoto och bilder på poster (flera bilder, `BildLager` i `:karna`). `2026-10-16-husloggen-bilder`, skickat 2026-10-16. Avläsning (OCR) är inte med; tas i eget uppdrag om den behövs.
5. Påminnelser före nästa datum. Cursor lyfter påminnelsemönstret till `:karna`.
6. Gratisgräns (15 poster) och Pro.
7. Sammanställning per år och typ, underlag för förbättringsutgifter vid försäljning, PDF "Husets historik" och CSV. Kontrollera reglerna hos Skatteverket och i inkomstskattelagen innan uppdraget skrivs.
8. Säkerhetskopia, import och automatisk säkerhetskopia. Cursor lyfter dem till `:karna`.
9. Mörkt läge, välkomstskärm, butiksmaterial, skärmbilder, Data safety och integritetspolicy.
10. Mattias provar på telefon.

## Senare

- Husloggen efter 1.0: mätarställningar, flera bostäder, dokument (manualer, ritningar), plan för framtida kostnader.
- App 3 väljs när Garantivalvet har varit ute i 90 dagar (IDEURVAL.md).
- Mätaren i panelen (`release.json`) följer Garantivalvet tills den är publicerad. Claude uppdaterar den när ett steg ändras.
