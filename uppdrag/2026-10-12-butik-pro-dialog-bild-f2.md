# Uppdrag: skärmbilden 04-pro-dialog, försök 2 (från aktuell main)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget. Uppdraget är litet och görs helt av Cursor i en PR; inga uppgiftsfiler behövs.

## Bakgrund

Uppdraget `2026-10-12-butik-pro-dialog-bild` gav PR #88 (commit `43d88d8` på grenen `cursor/04-pro-dialog-sk-rmbild-cf16`). Lösningen fungerar (`captureScreenRoboImage()` visar dialogen), men grenen utgick från main före #82 och krockar nu med `ButikSkarmbilderTest.kt` och `sv/04-pro-dialog.png`. På main har testet språkspecifika qualifiers, funktionsgrafik per språk och testet `skarmQualifiersLaddarRattSprak`. #88 stängs.

## Uppgift

1. Utgå från senaste `main`. Kontrollera med `git merge-base` att grenen innehåller main innan PR:en öppnas.
2. För in ändringen från `43d88d8` i `ButikSkarmbilderTest.kt` ovanpå mains version: hel skärm (`captureScreenRoboImage`) bara för `04-pro-dialog.png`, och kontrollen att 01 och 04 skiljer sig. Behåll allt som finns på main (qualifiers per språk, funktionsgrafik, `skarmQualifiersLaddarRattSprak`).
3. Generera om `garantivalvet/butik/skarmbilder/sv/04-pro-dialog.png` och `en/04-pro-dialog.png` och committa dem. Övriga bilder ändras inte i onödan.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- `sv/04-pro-dialog.png` visar Pro-dialogen med svensk text och `en/04-pro-dialog.png` med engelsk.
- PR:en har inga konflikter mot main.

## Gör inte

- Ändra appens beteende, strängar, manifest eller databasschema.
- Lägga till beroenden.
- Ändra `garantivalvet/butik/sv/*.txt` eller `en/*.txt`.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
