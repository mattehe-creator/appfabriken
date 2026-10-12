# Status

Uppdaterad 2026-10-12 09:15.

## Klart: Grundplattan (2026-10-08–09)

Flödet går hela vägen utan att Mattias rör koden: Claude skriver uppdrag, Cursor delar upp och gör det tunga, den lokala modellen (Aider + Ollama `qwen3-coder:30b`) skriver uppgifterna, Cursor granskar, workflowet `cursor-beslut` mergar, Claude granskar i efterhand. Se ARBETSFLODE.md.

På Mattias dator: körskriptet i Schemaläggaren (när datorn är oanvänd 10 min, och kl. 01), startfilen "Starta lokal modell" och panelen "Appfabriken panel" på skrivbordet.

## Nu: Garantivalvet 1.0

Omfång i IDEURVAL.md.

- Klart: köp i databasen, lista sorterad efter garantislut, formulär med validering, detaljvy med ändra och ta bort, formatering av pris och datum (uppdrag `2026-10-08-kop-lista-formular`).
- Klart: foto av kvittot med förhandsvisning, miniatyr, helskärm med zoom och dela, ikon i listan (uppdrag `2026-10-09-kvittofoto`, uppgift 01–09).
- Klart: påminnelser 30 dagar före garantislut (uppdrag `2026-10-09-paminnelser`, uppgift 10–11). WorkManager och DataStore, notisbehörighet begärs först när användaren slår på påminnelser.
- Klart: reklamationsrätt enligt konsumentköplagen (uppdrag `2026-10-09-reklamationsratt`, uppgift 12–17). Frister kontrollerade mot riksdagen.se: 3 år felansvar (4 kap. 14 §), 2 år presumtion (4 kap. 17 §), 2 månaders reklamation (5 kap. 2 §), gäller köp från 2022-05-01.
- Klart: gratisgräns (15 köp) och Pro via Google Play Billing, i `:karna` (uppdrag `2026-10-11-pro-gratisgrans`, uppgift 18–21).
- Klart: reklamationsunderlag som PDF (Pro) och `allowBackup=false` (uppdrag `2026-10-11-export-pdf`, uppgift 22–25).
- Klart: säkerhetskopia till fil och import (Pro), uppdrag `2026-10-11-export-sakerhetskopia`, uppgift 26–30 och 34.
- Mattias provade 2026-10-12. Resultat: PDF och Dela gör ingenting (Pro-dialogen ritas inte på detaljsidan, och Pro kan inte köpas utanför Play), och utseendet håller inte för försäljning.
- Kvar i 1.0, i ordning:
  1. Fel från provet: Pro-dialog på alla skärmar och testläge för Pro i debug-APK:n (uppdrag `2026-10-12-provfel-pdf-pro`, Cursor).
  2. Ny design: fullständigt tema, startsidan är listan, inställningar under kugghjul, nytt formulär, ny detaljsida med statuskort och tidslinje, nya skärmbilder (uppdrag `2026-10-12-ny-design`).
  2b. Del B (uppgift 38–46) klar 2026-10-12. Skärmbilderna genereras om (uppdrag `2026-10-12-skarmbilder-efter-del-b`, Cursor).
  3. Mattias provar igen på telefon.
  4. Play Console: internt testspår, produkten `pro`, prova köpet.
- Klart: butiksmaterial i `garantivalvet/butik/` (texter, skärmbilder, Data safety) och datum/pris i enhetens språk (#79–#94).

- Mätaren i panelen (procent klart, beräknat releasedatum, tokens) styrs av `release.json`. Claude uppdaterar den när ett steg ändras.

## Senare

- App 2: Husets underhållslogg. App 3: Mätarställning (IDEURVAL.md).
- Uppdrag i två steg (ARBETSFLODE.md) från och med påminnelser.
