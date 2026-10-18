# Uppdrag: Flextid, PDF-månadsrapport

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och flextid/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Flextid (app 3) har grund, Pro-gräns, påminnelser med inställbar tid, mörkt läge, välkomstskärm och butiksmaterial mergade. Export finns som CSV per månad och JSON-säkerhetskopia. Många vill ha en utskrivbar månadsrapport att lämna till chef eller spara, och en PDF är lättare att läsa än en CSV. Husloggen och Garantivalvet har redan PDF-export med Androids inbyggda `PdfDocument`; mönstret återanvänds. Detta är nästa funktion som inte väntar på Mattias prov.

## Uppgift

1. Knapp "Exportera PDF" bredvid CSV-exporten för vald månad. Filen sparas via systemets filväljare (som CSV).
2. PDF:en innehåller: månad och år, en tabell med en rad per registrerad dag (datum, veckodag, start, slut, rast, arbetad tid, utfall mot normaltid, typ), summa arbetad tid, summa utfall för månaden och saldot vid månadens slut (ingångssaldo plus alla dagars utfall till och med månadens sista dag). Dagar utan registrering visas inte.
3. All beräkning och formatering av raderna och summorna ligger i `regler/` som ren Kotlin så den går att enhetstesta. Själva ritningen med `PdfDocument` ligger separat. Flera sidor när raderna inte får plats.
4. Strängar på svenska och engelska i `strings.xml`. Tider i 24-timmarsformat, utfall med tecken (+0:30, -1:15).
5. Export är alltid gratis, även över gratisgränsen.

## Exempel för testerna

- Tre dagar i oktober 2026: måndag 08:00-16:30 rast 30 (arbetad 8:00, utfall 0:00), tisdag 07:30-17:00 rast 30 (arbetad 9:00, utfall +1:00), onsdag 08:00-15:00 rast 30 (arbetad 6:30, utfall -1:30). Summa arbetad 23:30, summa utfall -0:30. Med ingångssaldo +2:00 blir saldot vid månadens slut +1:30.
- En dag med typ som inte är Arbete (till exempel semester) visas med typ och utfall +0:00 och räknas inte in i summa arbetad tid.
- Dag med start men utan slut: raden visas med "-" för slut och arbetad tid, och räknas inte in i summorna.
- Månad utan registrerade dagar: PDF med rubrik, text "Inga registrerade dagar" och saldot oförändrat.
- Dagar i andra månader tas inte med i tabellen men ingår i saldot om de ligger före månadens slut.

## Hur Cursor arbetar

Tungt (PdfDocument-ritning, filväljare) gör Cursor själv. Dela ut ren logik (rader, summor, formatering) och strängar som uppgifter till den lokala modellen enligt MALL.md. Skriv frågor under "Frågor till Claude" i PR-beskrivningen.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Tester täcker exemplen ovan.
- Ingen ny behörighet, inget nytt beroende.
- `flextid/APP.md` nämner PDF-exporten under Regler. Butikstexterna i `flextid/butik/` är oförändrade.

## Gör inte

- Ändra Garantivalvet, Husloggen eller `:karna` mer än vad en delad PDF-hjälpare kräver.
- Lägga in avtalstext, rådgivning om vad avtal eller lag tillåter eller någon unik identifierare av användaren i rapporten.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
