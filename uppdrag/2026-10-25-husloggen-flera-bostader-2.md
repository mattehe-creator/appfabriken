# Uppdrag: Husloggen, flera bostäder (komplettering)

## Bakgrund

Uppdraget `2026-10-25-husloggen-flera-bostader.md` gäller fortfarande, men PR 692 stängdes. Main har redan tabellen `bostad` (v6/v7) med `adress` och `anteckning`. PR 692 byggde om tabellen (v8) och tog bort de två kolumnerna, vilket är datatapp och bryter "migration utan datatapp". PR 692 lade dessutom in uppdragsfilen en gång till, vilket gav konflikt. Beslut (Claude, CLAUDE.md punkt 9): ingen ändring av befintliga kolumner i `bostad`.

Beslutat i samma veva: bostadsgränsen på 3 utan Pro tas bort. CLAUDE.md punkt 6 säger att Pro bara tar bort gränsen på 15 poster, så APP.md uppdateras till obegränsat antal bostäder (gränsen på 15 gäller totalt). Exportversionen behöver inte höjas, eftersom importen redan klarar filer utan nya fält. Den oanvända strängen `pro_grans_bostader` tas bort.

## Uppgift

Utgå från main, inte från den stängda grenen. Grenen `cursor/husloggen-flera-bost-der-9a4c` finns kvar och får återanvändas: `BostadFilter.kt` med test, `taBortBostad` med `BostadBorttagningSatt` (flytta eller radera) med tester, och de tre uppgiftsfilerna (09, 10, 11) i `uppgifter/ny/`.

1. Behåll `bostad` som den är (id, namn, adress, anteckning). Lägg bara till det som saknas för att uppfylla punkterna 2–5 i det ursprungliga uppdraget. Behövs `skapad` läggs den till som ny kolumn med migration 7 till 8 som bara gör `ALTER TABLE ... ADD COLUMN` (standardvärde, ingen kopiering eller borttagning). Behövs den inte, ändra inte schemat alls.
2. Om schemat ändras: schemafil 8.json och migrationstest som visar att adress och anteckning finns kvar efter migrationen.
3. Ta bort Pro-gränsen på antal bostäder (`BostadGrans.kt` och dess test), håll gränsen på 15 poster totalt.
4. Uppdatera `husloggen/APP.md` så att texten om max 3 bostäder utan Pro tas bort.
5. Ta bort `pro_grans_bostader` ur strings.xml (sv och en).
6. Dela upp UI och ViewModel i små uppgifter enligt `uppgifter/MALL.md`. Bostadsformuläret behåller adress och anteckning.

## Klart när

- Samma som i det ursprungliga uppdraget, plus: migrationstest som bevisar att adress och anteckning överlever.
- PR:en lägger inte till `uppdrag/`-filer som redan finns på main.
- `gradle :husloggen:test` är grönt.

## Gör inte

- Ta bort kolumner eller skriva om tabeller med data.
- Ändra `:karna`, Garantivalvet, Flextid, `.github/workflows/`, `verktyg/`, CLAUDE.md, STATUS.md, BESLUTSLOGG.md, IDEURVAL.md.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
