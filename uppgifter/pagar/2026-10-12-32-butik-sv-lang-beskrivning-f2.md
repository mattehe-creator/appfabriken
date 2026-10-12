# Uppgift: svensk lång butiksbeskrivning

uppdrag: uppdrag/2026-10-12-butiksmaterial.md
försök: 2
beror-på: 2026-10-12-31-butik-sv-titel-kort-f2.md

## Vad som saknades
- `garantivalvet/butik/sv/lang.txt` ska innehålla den **färdiga** svenska långbeskrivningen för Google Play (högst 4 000 tecken efter `trim()`), inte platshållartext. Försök 1 lämnade nästan samma text som på `main`, inklusive meningen som börjar med «Platshållare: den långa svenska butiksbeskrivningen skrivs i uppgift 32».
- Beskrivningen ska vara tydligt strukturerad (stycken eller punktlistor) och täcka alla fyra punkter i uppgiften: vad appen gör; reklamationsrätt enligt konsumentköplagen (2022:260) som **information** (inte rådgivning) för köp i **Sverige**; offline och integritet; gratis (15 köp) och Pro (engångsköp: obegränsat, PDF, säkerhetskopia).
- Ton och fakta ska stämma med `garantivalvet/butik/sv/titel.txt`, `kort.txt` och uppdraget — utan http/www, utan juridiska löften och utan påhittade recensioner.

## Ändra bara dessa filer
- garantivalvet/butik/sv/lang.txt

## Läs som förebild (ändra inte)
- garantivalvet/butik/sv/titel.txt
- garantivalvet/butik/sv/kort.txt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/butik/ButikTexterTest.kt
- uppdrag/2026-10-12-butiksmaterial.md (beslut om butikstexter)

## Uppgift
Ersätt hela innehållet i `lang.txt` med **svensk lång beskrivning**, högst **4 000 tecken**. Strukturera tydligt (stycken eller punktlistor) med ungefär:

1. Vad appen gör — håll koll på köp, garantitid, kvitton och påminnelser.
2. **Reklamationsrätt** enligt konsumentköplagen (2022:260) som **information**, inte rådgivning; gäller köp i **Sverige** efter lagens ikraftträdande; appen visar även garantitid.
3. Offline och integritet — ingen internetbehörighet, inga konton, ingen reklam, ingen analys; data stannar på telefonen tills användaren exporterar.
4. Gratis och Pro — gratis upp till 15 köp; Pro (engångsköp) låser upp obegränsat antal, PDF-underlag och säkerhetskopia.

**Gör inte:** lova juridiskt utfall eller formulera text som juridisk rådgivning; inga `http` eller `www`; inga namngivna konkurrenter eller påhittade recensioner; ingen markdown eller kodblock i filen — bara ren butikstext.

**Tekniskt:** Spara under `garantivalvet/butik/sv/lang.txt`, inte i repots rot. Ta bort all platshållartext.

## Exempel
- Filen börjar med en säljande ingress om appen, inte med «Platshållare».
- Texten nämner uttryckligen att reklamationsinformationen gäller Sverige och att appen inte ger juridisk rådgivning.
- `ButikTexterTest` passerar för `sv/lang.txt` (längd ≤ 4 000, inga http/www).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra titel, kort beskrivning eller engelska filer.
- Lägga till beroenden.
- Ändra appkod eller manifest.
