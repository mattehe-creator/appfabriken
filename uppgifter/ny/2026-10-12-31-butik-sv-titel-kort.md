# Uppgift: svensk butikstitel och kort beskrivning

uppdrag: uppdrag/2026-10-12-butiksmaterial.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/butik/sv/titel.txt
- garantivalvet/butik/sv/kort.txt

## Läs som förebild (ändra inte)
- garantivalvet/butik/README.md
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/butik/ButikTexterTest.kt
- garantivalvet/APP.md (om filen finns) eller CLAUDE.md punkt 6

## Uppgift
Skriv **svenska** Google Play-texter (huvudspråk i butiken):

1. `titel.txt` — appnamn/rubrik, högst **30 tecken**.
2. `kort.txt` — kort beskrivning, högst **80 tecken**.

Innehåll ska stämma med appen och får använda dessa säljargument: helt offline, ingen internetbehörighet, inga konton, ingen reklam, ingen analys, gratis upp till 15 köp, Pro (engångsköp) ger obegränsat antal köp, PDF-underlag och säkerhetskopia.

**Gör inte:** lova juridiskt utfall; nämna inte namngivna konkurrenter; inga påhittade betyg eller användarcitat; inga `http` eller `www`.

## Exempel
- `ButikTexterTest` passerar för `sv/titel.txt` och `sv/kort.txt`.
- Titeln är kort nog för Play (≤ 30 tecken).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra andra filer (inklusive engelska texter och `lang.txt`).
- Lägga till beroenden.
- Ändra appkod, manifest eller databasschema.
