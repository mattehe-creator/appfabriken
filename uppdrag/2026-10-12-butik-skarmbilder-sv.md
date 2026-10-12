# Uppdrag: svenska skärmbilder och funktionsgrafik per språk (rättelse)

Läs CLAUDE.md, ARBETSFLODE.md och uppgifter/MALL.md först och följ dem. Ändra inget utöver uppdraget. Uppdraget är litet och görs helt av Cursor i en PR; inga uppgiftsfiler behövs.

## Bakgrund

Del A av `uppdrag/2026-10-12-butiksmaterial.md` är mergad (#79). Efterhandsgranskningen visar att skärmbilderna i `garantivalvet/butik/skarmbilder/sv/` har engelskt gränssnitt ("Warranty Vault", "Unlock Pro", "In warranty"); bara datumformat och påhittade köp är svenska. Trolig orsak: `ButikSkarmbilderBas.captureSkarm` anropar `RuntimeEnvironment.setQualifiers(skarmQualifiers())` efter `ställInSprak()`, och qualifiers saknar språk, så resurserna faller tillbaka på standardspråket. `funktionsgrafik.png` har samma fel: engelskt appnamn med svensk rad. Svenska är huvudspråk i butiken.

## Uppgift

1. Sätt språket i qualifiers, till exempel `sv-rSE-w360dp-h640dp-port-xxhdpi` och `en-rGB-w360dp-h640dp-port-xxhdpi`, eller på annat sätt som håller. Lägg till en kontroll i testet att en känd sträng renderas på rätt språk (till exempel att `R.string.app_name` eller en knapptext skiljer sig mellan sv och en), så att felet inte kan komma tillbaka tyst.
2. Generera om alla åtta skärmbilder och committa dem.
3. Funktionsgrafiken görs per språk: `garantivalvet/butik/funktionsgrafik-sv.png` och `funktionsgrafik-en.png`, 1024 × 500, appnamn och kort rad på respektive språk från appens strängresurser. Ta bort `funktionsgrafik.png` och uppdatera `garantivalvet/butik/README.md`.
4. `garantivalvet/butik/data-safety.md`: nämn uttryckligen att behörigheten `com.android.vending.BILLING` finns i det sammanslagna manifestet och kommer från Play Billing-biblioteket (svar på frågan i #79).

## Klart när

- `gradle testDebugUnitTest` och `gradle assembleDebug` är gröna för alla moduler.
- Skärmbilderna under `sv/` visar svenskt gränssnitt och under `en/` engelskt.
- Funktionsgrafiken finns för sv och en, på rätt språk.

## Gör inte

- Ändra appens beteende, strängar i appen, manifest eller databasschema.
- Lägga till beroenden.
- Ändra `garantivalvet/butik/sv/*.txt` eller `en/*.txt` (de skrivs av uppgift 31–33).
- Ändra `.github/workflows/`, `verktyg/`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md` eller `release.json`.
