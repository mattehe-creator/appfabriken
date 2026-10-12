# Uppdrag: skärmbilden 04-pro-dialog visar inte dialogen (rättelse)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget. Uppdraget är litet och görs helt av Cursor i en PR; inga uppgiftsfiler behövs.

## Bakgrund

Efter #82 är `garantivalvet/butik/skarmbilder/sv/04-pro-dialog.png` byte för byte lika med `01-lista.png`, och likadant under `en/`. `proDialog()` i `ButikSkarmbilderTest.kt` sätter `proDialogSynlig = true`, men dialogen ritas i ett eget fönster och kommer inte med när bara innehållsvyn fångas.

## Uppgift

1. Fånga hela skärmen för `04-pro-dialog.png` (till exempel Roborazzis `captureScreenRoboImage()`), så att Pro-dialogen syns ovanpå listan. Övriga bilder ändras inte i onödan.
2. Lägg till en kontroll som gör att felet inte kan komma tillbaka tyst, till exempel att dialogens text (`pro_forklaring` eller köpknappen) finns i semantikträdet, eller att filerna 01 och 04 skiljer sig.
3. Generera om `04-pro-dialog.png` för sv och en och committa dem.

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler, och `verifyRoborazziDebug` är grönt.
- `04-pro-dialog.png` visar Pro-dialogen på rätt språk under både `sv/` och `en/`.

## Gör inte

- Ändra appens beteende, strängar, manifest eller databasschema.
- Lägga till beroenden.
- Ändra `garantivalvet/butik/sv/*.txt` eller `en/*.txt`.
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
