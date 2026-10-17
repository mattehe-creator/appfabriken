# Uppgift: svensk lång butiksbeskrivning

uppdrag: uppdrag/2026-10-17-husloggen-butik-mork-valkomst.md
försök: 1
beror-på: 2026-10-17-05-butik-sv-titel-kort.md

## Ändra bara dessa filer
- husloggen/butik/sv/lang.txt

## Läs som förebild (ändra inte)
- husloggen/butik/sv/titel.txt
- husloggen/butik/sv/kort.txt
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

utfall: underkänd 2026-10-17 av Cursor (PR #235). Ersatt av 2026-10-17-06-butik-sv-lang-beskrivning-f2.md
