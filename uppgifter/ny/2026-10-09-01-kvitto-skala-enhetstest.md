# Uppgift: enhetstest för kvittonedskalning

uppdrag: uppdrag/2026-10-09-foto-av-kvittot.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalningTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoSkalning.kt

## Uppgift
Skriv enhetstester för **`beraknaNedskaladStorlek(bredd, hojd, maxLangstaSida = 2000)`**.

## Exempel
- Indata: bredd 4000, höjd 3000. Förväntat: 2000 × 1500 (längsta sidan 2000).
- Indata: bredd 800, höjd 600. Förväntat: 800 × 600 (ingen ändring).
- Indata: bredd 3000, höjd 3000. Förväntat: 2000 × 2000.
- Indata: bredd 0, höjd 100. Förväntat: 0 × 100.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KvittoSkalningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
