# Uppgift: engelska butikstexter

uppdrag: uppdrag/2026-10-12-butiksmaterial.md
försök: 1
beror-på: 2026-10-12-32-butik-sv-lang-beskrivning.md

## Ändra bara dessa filer
- garantivalvet/butik/en/titel.txt
- garantivalvet/butik/en/kort.txt
- garantivalvet/butik/en/lang.txt

## Läs som förebild (ändra inte)
- garantivalvet/butik/sv/titel.txt
- garantivalvet/butik/sv/kort.txt
- garantivalvet/butik/sv/lang.txt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/butik/ButikTexterTest.kt

## Uppgift
Skriv **engelska** Google Play-texter som motsvarar den svenska långa beskrivningen i innehåll och ton:

1. `titel.txt` — högst **30 tecken**.
2. `kort.txt` — högst **80 tecken**.
3. `lang.txt` — högst **4 000 tecken**.

På engelska ska det tydligt framgå att regler om **consumer claims (Konsumentköplagen)** gäller **köp i Sverige** och att avsnittet är **information, not legal advice**; i övrigt beskriver appen garantitid och köp som vanligt.

Samma tillåtna säljargument som i CLAUDE.md/BESLUTSLOGG: offline, no INTERNET permission, no accounts, no ads, no analytics; free up to 15 purchases; Pro one-time unlock for unlimited purchases, PDF export and backup.

**Gör inte:** juridiska löften; `http`/`www`; named competitors; fake ratings or quotes.

## Exempel
- `ButikTexterTest` passerar för alla tre filer under `en/`.
- `lang.txt` mentions Sweden and that consumer-claim text is informational only.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra svenska filer eller butiksbilder.
- Lägga till beroenden.
- Ändra appkod eller manifest.
