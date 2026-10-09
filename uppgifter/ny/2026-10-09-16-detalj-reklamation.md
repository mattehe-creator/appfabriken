# Uppgift: reklamationsrätt i detaljvyn

uppdrag: uppdrag/2026-10-09-reklamationsratt.md
försök: 1
beror-på: 2026-10-09-12-reklamation-strangar.md, cursor:2026-10-09-reklamationsratt

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Konsumentkoplagen.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListRad.kt

## Uppgift
Efter garanti-raden i **`KopDetaljSkarm`**:

1. Om `reklamationSlut(kop.kopdatum)` inte är null: visa `detalj_reklamation_till` med formaterat slutdatum, sedan `detalj_reklamation_rubrik`, `detalj_reklamation_forklaring` och `detalj_reklamation_vagledning` (lämplig typografi, t.ex. bodyMedium för förklaring).
2. Om `reklamationSlut` är null och `kop.kopdatum` är före `KONSUMENTKOP_IKRAFT` (2022-05-01): visa enbart `detalj_reklamation_aldre_lag` (ingen datumrad).
3. Om köpet är från 2022-05-01 eller senare men `reklamationSlut` ändå null — ska inte hända; visa inget extra.

Uppdatera garanti-statusraden till samma `kopStatus` och strängar/färger som listraden (uppgift 15), om det inte redan är gjort i samma fil av tidigare steg.

## Exempel
- Köp 2024-03-15: rad «Reklamationsrätt till …» med datum 2027-03-15 plus förklaring och vägledningstext.
- Köp 2022-04-30: ingen datumrad; kort text om äldre lag.
- Köp 2024-01-10: reklamationsblock syns; inget om äldre lag.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopStatusTest" --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
