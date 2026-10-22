# Uppdrag: Styrelserummet, indexering i Fondplan och förändring mot föregående år i Nyckeltal

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

De lokala uppgifterna `fond-06-indexering` (PR #494, `FondIndexering.kt`: `indexeratBelopp`, `indexeradeAtgarder`) och `nyckeltal-06-forandring` (PR #496, `NyckeltalForandring.kt`: `forandringTiondelsProcent`, `formateraForandring`) är mergade men används inte av verktygen `/fondplan` och `/nyckeltal` eller deras API. Funktionerna ska kopplas in så att de fria verktygen blir användbara på riktigt: en fondplan som räknar med prisuppräkning, och nyckeltal som visar förändring mot föregående år.

## Uppgift

1. Fondplan: ett valfritt fält för antagen årlig prisuppräkning i procent (heltal eller en decimal, 0-10, tomt = 0) i formuläret och i `POST /api/fondplan`. När det är satt används `indexeradeAtgarder` så att framtida åtgärder räknas upp, och resultatet (tabell, summa per år, nödvändig årlig avsättning) bygger på de uppräknade beloppen. Utan värde ska resultatet vara oförändrat mot i dag.
2. Nyckeltal: ett valfritt andra set indata för föregående år. När det finns visas förändringen per nyckeltal med `formateraForandring` bredvid värdet; saknas värdet eller är föregående år noll visas strecket som funktionen ger. Utan föregående år ska resultatet vara oförändrat.
3. Felmeddelanden på svenska i samma form som övriga fel (`{"fel": ...}`) för värden utanför intervallet.
4. Texterna ligger där övriga verktygstexter ligger. Ingen text på engelska.

## Klart när

- Servertester: fondplan med och utan prisuppräkning (uppräknat belopp för minst ett exempel jämfört för hand), nyckeltal med och utan föregående år (en ökning, en minskning, noll i föregående år), värde utanför 0-10 ger felmeddelande.
- Befintliga tester för verktygen, gränserna (60 anrop per timme, 64 kB) och handlingsjobben går fortfarande igenom.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna.
- Cursor delar upp arbetet i små uppgifter för den lokala modellen enligt MALL.md. Frågor till Claude står i PR-beskrivningen.

## Gör inte

- Lägga till beroenden, persistens, cookies, analys eller loggning av indata.
- Ändra beräkningsreglerna i `FondIndexering.kt` och `NyckeltalForandring.kt`, `K3.md`, modellerna, `nyttjandeperioder.csv` eller betalflödet.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
