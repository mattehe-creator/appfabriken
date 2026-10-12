# Uppgift: svensk lång butiksbeskrivning

uppdrag: uppdrag/2026-10-12-butiksmaterial.md
försök: 1
beror-på: 2026-10-12-31-butik-sv-titel-kort.md

## Ändra bara dessa filer
- garantivalvet/butik/sv/lang.txt

## Läs som förebild (ändra inte)
- garantivalvet/butik/sv/titel.txt
- garantivalvet/butik/sv/kort.txt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/butik/ButikTexterTest.kt

## Uppgift
Skriv **svensk lång beskrivning** i `lang.txt`, högst **4 000 tecken**. Strukturera tydligt (stycken eller punktlistor) med ungefär:

1. Vad appen gör — håll koll på köp, garantitid, kvitton och påminnelser.
2. **Reklamationsrätt** enligt konsumentköplagen (2022:260) som **information**, inte rådgivning; gäller köp i **Sverige** efter lagens ikraftträdande; appen visar även garantitid.
3. Offline och integritet — ingen internetbehörighet, inga konton, ingen reklam, ingen analys; data stannar på telefonen tills användaren exporterar.
4. Gratis och Pro — gratis upp till 15 köp; Pro (engångsköp) låser upp obegränsat antal, PDF-underlag och säkerhetskopia.

**Gör inte:** lova juridiskt utfall eller formulera text som juridisk rådgivning; inga `http` eller `www`; inga namngivna konkurrenter eller påhittade recensioner.

## Exempel
- `ButikTexterTest` passerar för `sv/lang.txt`.
- Texten nämner att reklamationsinformationen gäller Sverige och att det inte är rådgivning.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra titel, kort beskrivning eller engelska filer.
- Lägga till beroenden.
- Ändra appkod eller manifest.
