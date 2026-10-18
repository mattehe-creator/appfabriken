# Uppgift: svensk lång butiksbeskrivning

uppdrag: uppdrag/2026-10-18-flextid-butik-mork-valkomst.md
försök: 1
beror-på: 2026-10-18-01-butik-sv-titel-kort-f2.md

## Ändra bara dessa filer
- flextid/butik/sv/lang.txt

## Läs som förebild (ändra inte)
- flextid/butik/sv/titel.txt
- flextid/butik/sv/kort.txt
- flextid/APP.md
- flextid/src/test/kotlin/se/tmconnect/flextid/butik/ButikTexterTest.kt

## Uppgift
Skriv **svensk lång beskrivning** i `lang.txt`, högst **4 000 tecken**. Strukturera tydligt (stycken eller punktlistor) med ungefär:

1. Vad appen gör — stämpla in och ut, rast, saldo mot egen normaltid, veckovy, valfria påminnelser (stämpla in kl. 08:00 på arbetsdagar respektive stämpla ut efter normaltid).
2. Export och säkerhetskopia — CSV för vald månad och JSON-säkerhetskopia via systemets filväljare; alltid gratis.
3. Offline och integritet — ingen `INTERNET`-behörighet, inga konton, ingen reklam, ingen analys; data stannar på telefonen tills användaren exporterar.
4. Gratis och Pro — gratis upp till 15 dagposter; Pro (engångsköp) tar bort gränsen. Appen antar inget kollektivavtal och ger ingen rådgivning.

**Gör inte:** lova juridiskt utfall; formulera inte text som juridisk rådgivning; inga `http` eller `www`; inga namngivna konkurrenter eller påhittade recensioner.

## Exempel
- `ButikTexterTest` passerar för `sv/lang.txt`.
- Texten nämner offline och att export/säkerhetskopia är gratis.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra titel, kort beskrivning eller engelska filer.
- Lägga till beroenden.
- Ändra appkod eller manifest.

utfall: godkänd 2026-10-18 (fullbordade avklippt lang.txt)
