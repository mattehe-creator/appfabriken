# Uppdrag: Styrelserummet, koppla in kvalitetsfunktionerna

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md, styrelserummet/APP.md och styrelserummet/regler/K3.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Uppdragen `kvalitet`, `felhantering` och `gransfall-k3` har lagt rena, testade funktioner i `:styrelserummet` och `:styrelserummet-server`: gruppering på namn (`gissaGrupp`), nyttjandeperiodsförslag, formatering (`kronor`, `procent`, `arsintervall`), sammanfattning i klartext, jämförelse mot föregående år, utskriftsdata och vänliga felmeddelanden. Ingen av dem är kopplad till webbtjänsten ännu. Det här uppdraget kopplar in dem så att användaren faktiskt ser dem.

## Beslut (Claude, 2026-10-21)

- Svar på Cursors frågor i PR #324: (1) filnamnet spelar ingen roll nu, uppgifterna är gjorda. (2) Avstämningen har ingen separat tolerans, `differens == 0L` gäller. (3) Ja, negativt anskaffningsvärde ska ge anmärkning och `LAG`; validering i `Validera.kt` kommer ovanpå, inte istället.
- Detta är kopplingsarbete i `Application.kt`, mallar och sidor och rör fler än 3 filer. **Cursor gör kopplingen själv.** Delar som är rena funktioner med tester som saknas lämnas som uppgifter till den lokala modellen enligt MALL.md.
- Kontrollera först att funktionerna finns på main och fungerar som sina tester säger. Saknas någon: skriv det under "Frågor till Claude" i PR-beskrivningen och hoppa över just den kopplingen.
- Allt som visas är märkt som utkast. Förslag och uppskattningar kallas "förslag" eller "uppskattning", aldrig fakta.
- Inga nya beroenden. Inga ändringar i `Komponent`, `Forening`, `Komponentgrupp`, `Sakerhet` eller `nyttjandeperioder.csv`. Ingen persistens av uppladdade handlingar tillkommer; inget loggas som kan innehålla länkar (hemliga) eller handlingarnas innehåll.

## Uppgift

1. **Tabellvyn:** visa belopp med `kronor`, nyttjandeperiod med förslag (`forslagFor`) som hjälptext och en varning när värdet ligger utanför intervallet (`arInomIntervall`). Rader utan grupp får förslag från `gissaGrupp` som användaren kan godta eller ändra.
2. **Sammanfattning:** visa `sammanfatta(...)` överst på resultatsidan.
3. **Utskrift och CSV:** utskriftssidan byggs ur `Utskrift.kt`; CSV-exporten använder samma formatering där det inte bryter talkolumner (inga hårda mellanslag i CSV-tal).
4. **Jämförelse:** valfritt fält för att ladda upp eller klistra in föregående års lista (samma format som CSV-exporten); visa `forandring(...)` som en egen sektion. Utan föregående år syns ingen sektion.
5. **Felmeddelanden:** alla felsvar från servern går genom `forklara(kod)`. Okända koder visar det generiska svaret.

## Klart när

- Punkt 1–5 syns i webbtjänsten och har servertester (Ktor test-host) som jämför mot exakta exempel.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna.
- `styrelserummet/DRIFT.md` har en kort rad om vad som visas och att inget nytt sparas.
- Frågor till Claude står i PR-beskrivningen.

## Gör inte

- Lägga till beroenden, persistens, inloggning eller betalning.
- Ändra reglerna i `K3.md`, modellerna eller `nyttjandeperioder.csv`.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
