# Uppgift: svensk butikstitel och kort beskrivning

uppdrag: uppdrag/2026-10-18-flextid-butik-mork-valkomst.md
försök: 1
beror-på: cursor:2026-10-18-flextid-butik-mork-valkomst

## Ändra bara dessa filer
- flextid/butik/sv/titel.txt
- flextid/butik/sv/kort.txt

## Läs som förebild (ändra inte)
- flextid/butik/README.md
- flextid/APP.md
- flextid/src/test/kotlin/se/tmconnect/flextid/butik/ButikTexterTest.kt

## Uppgift
Skriv **svenska** Google Play-texter (huvudspråk i butiken). Appnamnet i butiken är preliminärt **Flextid** (arbetsnamn tills Mattias bestämmer vid publicering):

1. `titel.txt` — rubrik, högst **30 tecken**.
2. `kort.txt` — kort beskrivning, högst **80 tecken**.

Innehåll ska stämma med appen: stämpla in och ut per dag, rast, saldo mot egen normaltid, veckoöversikt, valfria påminnelser, CSV-export och säkerhetskopia som JSON. Appen antar inget kollektivavtal och ger ingen rådgivning. Säljargument: helt offline, ingen internetbehörighet, inga konton, ingen reklam, ingen analys. Gratis upp till **15 dagposter**; **Pro** (engångsköp) tar bort gränsen. Export och säkerhetskopia är alltid gratis.

**Gör inte:** lova juridiskt utfall eller vad avtal tillåter; nämna inte namngivna konkurrenter; inga påhittade betyg eller citat; inga `http` eller `www`.

## Exempel
- `ButikTexterTest` passerar för `sv/titel.txt` och `sv/kort.txt`.
- Titeln är kort nog för Play (≤ 30 tecken).

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra andra filer (inklusive engelska texter och `lang.txt`).
- Lägga till beroenden.
- Ändra appkod, manifest eller databasschema.
