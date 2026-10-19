# Uppdrag: Styrelserummet, webbservern (ny start ovanpå main)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget.

## Bakgrund

Den fullständiga specen står i `uppdrag/vantar/2026-10-19-styrelserummet-webb.md`. Läs den och följ den (beslut, rutter, gränser, integritet, Del A punkt 1-5, Klart när, Gör inte).

Tidigare försök: PR #279 (gren `cursor/styrelserummet-webbtj-nst-k3-7da6`) innehöll kärna och underlag på nytt och konfliktar nu med main, där `:styrelserummet` (k3-kärna, #277) och dokumentanalysen (#278) redan är mergade. #279 stängs av Claude.

## Uppgift

Starta från aktuell main. Återanvänd serverdelen från grenen ovan (modulen `:styrelserummet-server`, statiskt UI, `/integritet`, `Dockerfile`, `styrelserummet/DRIFT.md`, serverns tester) genom att hämta de filerna därifrån. Ta INTE med något under `styrelserummet/` som redan finns på main (kärna, underlag, `APP.md`, `regler/K3.md`, `nyttjandeperioder.csv`) - använd main:s versioner och anpassa servern till deras faktiska API.

Uppgiftsfiler för den lokala modellen (länk-id, rensningsregel, maskning) läggs i `uppgifter/ny/` enligt MALL.md, men bara om motsvarande kod inte redan finns på main.

Skriv frågor till Claude under "Frågor till Claude" i PR-beskrivningen. Gör Del A-PR:en (kod) som en egen PR, uppgifts-PR:en separat.

## Klart när

- `./gradlew :styrelserummet:test :styrelserummet-server:test` är gröna lokalt, och bygget i Actions är grönt.
- PR:en ändrar inga filer som redan finns på main under `styrelserummet/` utöver nödvändiga beroenden i `settings.gradle.kts`.
- Med falsk tolkare går det att ladda upp en PDF, ändra tabellen, ladda ned CSV och radera jobbet.

## Gör inte

- Allt under "Gör inte" i specen ovan.
- Duplicera kärna eller underlag.
