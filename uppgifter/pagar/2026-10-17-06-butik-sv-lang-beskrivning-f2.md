# Uppgift: svensk lång butiksbeskrivning

## Vad som saknades
- **`husloggen/butik/sv/lang.txt` skrevs inte.** Endast commit «Uppgift startad» (flytt till `pagar/`). Ersätt hela innehållet — ta bort raden «Preliminär lång beskrivning…» och skriv den färdiga butikstexten.
- Texten ska vara **ren butikstext** (inga markdown-rubriker med `#`, inga kodblock, inga sökvägar). Strukturera med stycken och/eller punktlistor som i `garantivalvet/butik/sv/lang.txt`.
- Obligatoriska teman (alla ska finnas i texten):
  1. **Uppgifter** med påminnelser, **logg** med belopp och kvittobilder, **sammanställning** per år och typ.
  2. **Försäljning** — sammanställningen som underlag; skatteinformation om förbättringsutgifter som **information** med lagrum (**45 kap.** och **46 kap.** inkomstskattelagen, eller motsvarande formulering), **gäller Sverige**, uttryckligen **inte juridisk rådgivning** (t.ex. «ingen juridisk rådgivning» / «inte råd»).
  3. **Offline och integritet** — ingen internetbehörighet, inga konton, ingen reklam, ingen analys; data på telefonen tills export eller säkerhetskopia.
  4. **Gratis och Pro** — gratis upp till **15 poster** i loggen; Pro (engångsköp) tar bort gränsen; uppgifter, påminnelser, export och säkerhetskopia gratis.
- Högst **4 000 tecken** (inklusive radbrytningar). Inga `http`, `www`, juridiska löften, namngivna konkurrenter eller påhittade recensioner.
- Läs `husloggen/butik/sv/titel.txt`, `kort.txt` och `husloggen/APP.md` så tonen stämmer med appen.

uppdrag: uppdrag/2026-10-17-husloggen-butik-mork-valkomst.md
försök: 2
beror-på: 2026-10-17-05-butik-sv-titel-kort.md

## Ändra bara dessa filer
- husloggen/butik/sv/lang.txt

## Läs som förebild (ändra inte)
- husloggen/butik/sv/titel.txt
- husloggen/butik/sv/kort.txt
- garantivalvet/butik/sv/lang.txt
- husloggen/APP.md
- husloggen/src/test/kotlin/se/tmconnect/husloggen/butik/ButikTexterTest.kt

## Uppgift
Skriv **svensk lång beskrivning** i `lang.txt`, högst **4 000 tecken**. Strukturera tydligt (stycken eller punktlistor) med ungefär:

1. Vad appen gör — återkommande **uppgifter** med påminnelser, **logg** med belopp och kvittobilder, **sammanställning** per år och typ.
2. **Försäljning** — sammanställningen kan underlätta när bostaden säljs; skatteregler om förbättringsutgifter visas som **information** (med lagrum), **bara för Sverige**, aldrig som rådgivning.
3. Offline och integritet — ingen `INTERNET`-behörighet, inga konton, ingen reklam, ingen analys; data stannar på telefonen tills användaren exporterar eller sparar säkerhetskopia.
4. Gratis och Pro — gratis upp till 15 **poster** i loggen; Pro (engångsköp) tar bort gränsen. Uppgifter, påminnelser, export och säkerhetskopia är gratis.

**Gör inte:** lova juridiskt utfall; formulera inte text som juridisk rådgivning; inga `http` eller `www`; inga namngivna konkurrenter eller påhittade recensioner.

## Exempel
- `ButikTexterTest` passerar för `sv/lang.txt`.
- Texten nämner att skatteinformationen gäller Sverige och att det inte är råd.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra titel, kort beskrivning eller engelska filer.
- Lägga till beroenden.
- Ändra appkod eller manifest.
