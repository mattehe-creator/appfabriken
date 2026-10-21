# Uppdrag: Styrelserummet, egna felkoder för lösenordsskyddad PDF och AI-fel

Läs CLAUDE.md, ARBETSFLODE.md, uppgifter/MALL.md och styrelserummet/APP.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

PR #410 kopplade in `Felmeddelanden.forklara(kod)` i hela webbtjänsten. Två följder är sämre för användaren än innan:

1. `ServerFel.KrypteradPdf` visas som `PDF_EJ_LASBAR` ("ingen läsbar text"), fast orsaken är att PDF:en är lösenordsskyddad.
2. Timeout och ogiltigt AI-svar visas båda som `BEARBETNING_MISSLYCKADES` (generisk text), fast de tidigare texterna i `FelHantering.kt` skilde dem åt och sa vad användaren kan göra.

## Beslut (Claude, 2026-10-21)

- Svar på Cursors frågor i PR #410: (1) Ja, en egen felkod och egen text för lösenordsskyddad PDF. (2) Nej, den generiska texten räcker inte; timeout och ogiltigt AI-svar får egna koder och texter som säger vad användaren kan göra (försök igen om en stund respektive försök igen eller ladda upp en tydligare handling).
- Texterna är svenska, korta och utan skuld. Inget visas som fakta om handlingens innehåll.
- Ändringen är liten och rör färre än 4 filer: Cursor gör den själv, eller delar upp den i en uppgift för den lokala modellen enligt MALL.md om den passar.

## Uppgift

1. Ny felkod `PDF_LOSENORD` i `Felkoder`, text: att PDF:en är lösenordsskyddad och att användaren kan spara en kopia utan lösenord och ladda upp den. `ServerFel.KrypteradPdf` mappas till den i `serverFelTillKod`.
2. Nya felkoder `AI_TIDSGRANS` och `AI_OGILTIGT_SVAR` med egna texter. Mappa dem från motsvarande `ServerFel` i `serverFelTillKod`. Okända fel ger fortfarande det generiska svaret.
3. HTTP-statuskoderna ändras inte.

## Klart när

- Servertester (Ktor test-host) jämför mot de exakta texterna för de tre koderna, och att en okänd kod ger det generiska svaret.
- `gradle :styrelserummet:test :styrelserummet-server:test` och övriga moduler är gröna.
- Frågor till Claude står i PR-beskrivningen.

## Gör inte

- Lägga till beroenden, persistens eller loggning av handlingarnas innehåll eller länkar.
- Ändra `K3.md`, modellerna eller `nyttjandeperioder.csv`.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`, `webb/`, övriga moduler.
