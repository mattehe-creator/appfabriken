# Flextid (arbetsnamn)

Regler för appen utöver CLAUDE.md. Val och omfång i IDEURVAL.md (idé F). Modul `:flextid`, paket `se.tmconnect.flextid`. Appnamn och applicationId beslutar Mattias vid första publiceringen (CLAUDE.md punkt 9).

## Vad appen gör

Enkel, helt offline flextidsregistrering: stämpla in och ut per dag, rast, och saldo mot en normaltid som användaren själv anger. Appen antar inget kollektivavtal och ger ingen rådgivning om vad avtal eller lag tillåter.

## Begrepp

- **Dag**: en kalenderdag med valfri start- och sluttid, rast i minuter, typ och anteckning.
- **Normaltid**: hur många minuter per arbetsdag som räknas som “fullgjort” (standard 480).
- **Utfall**: skillnaden mellan arbetad tid och dagens normaltid för typ Arbete. Typ Flexledig ger minus dagens normaltid. Semester, sjuk, helgdag och annan ledighet ger 0. Röda dagar och lediga aftnar har ingen normaltid (BESLUTSLOGG 2026-10-24).
- **Saldo**: ingångssaldo plus summan av alla registrerade dagars utfall.

## Regler

- All beräkning ligger i `regler/` (arbetad tid, utfall, saldo, vecka måndag–söndag, validering av tider).
- Gratisgräns: högst 15 dagposter (rader i `dag`) totalt, definierad i `regler/GratisGrans.kt`. Pro (engångsköp via `:karna`) tar bort gränsen. Befintlig data raderas aldrig. Export, CSV, PDF-månadsrapport och säkerhetskopia är alltid gratis.
- Dagar utan registrering i det förflutna räknas inte (ingen automatisk minusbokning).
- Slut före start samma dag tillåts inte; formuläret visar fel.
- Arbetsdagar styrs av en bitmask (måndag–söndag) i inställningar.
- Påminnelser (WorkManager, `:karna`): valfri “Glöm inte stämpla ut” efter normaltid när dagen har start men inte slut; valfri “Glöm inte stämpla in” på arbetsdagar vid valt klockslag (standard 08:00, 05:00–12:00) om ingen start registrerats. Inställningarna ligger under Inställningar; notiskanal och `POST_NOTIFICATIONS` enligt samma mönster som `:husloggen`.
- Export: CSV och utskrivbar PDF-månadsrapport för vald månad via systemets filväljare. Säkerhetskopia och återställning som JSON med versionsnummer.
- Inga nätverksbehörigheter, ingen plats, inga konton.

## Inte i detta uppdrag

Avtalsmallar, färdig appikon (väntar butiksunderlag).
