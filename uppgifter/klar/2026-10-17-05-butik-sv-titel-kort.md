# Uppgift: svensk butikstitel och kort beskrivning

uppdrag: uppdrag/2026-10-17-husloggen-butik-mork-valkomst.md
försök: 1
beror-på: cursor:2026-10-17-husloggen-butik-mork-valkomst

## Ändra bara dessa filer
- husloggen/butik/sv/titel.txt
- husloggen/butik/sv/kort.txt

## Läs som förebild (ändra inte)
- husloggen/butik/README.md
- husloggen/APP.md
- husloggen/src/test/kotlin/se/tmconnect/husloggen/butik/ButikTexterTest.kt

## Uppgift
Skriv **svenska** Google Play-texter (huvudspråk i butiken). Appnamnet i butiken är preliminärt **Husloggen** (arbetsnamn tills Mattias bestämmer vid publicering):

1. `titel.txt` — rubrik, högst **30 tecken**.
2. `kort.txt` — kort beskrivning, högst **80 tecken**.

Innehåll ska stämma med appen: underhålls**uppgifter** med påminnelser, **logg** över utfört arbete och utgifter (med kvittobilder), **sammanställning** som underlag vid försäljning (skatteinformation som information, bara Sverige). Säljargument: helt offline, ingen internetbehörighet, inga konton, ingen reklam, ingen analys. Gratis upp till **15 poster** i loggen; **Pro** (engångsköp) tar bort gränsen. Uppgifter och påminnelser är obegränsade.

**Gör inte:** lova juridiskt utfall; nämna inte namngivna konkurrenter; inga påhittade betyg eller citat; inga `http` eller `www`.

## Exempel
- `ButikTexterTest` passerar för `sv/titel.txt` och `sv/kort.txt`.
- Titeln är kort nog för Play (≤ 30 tecken).

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra andra filer (inklusive engelska texter och `lang.txt`).
- Lägga till beroenden.
- Ändra appkod, manifest eller databasschema.

utfall: godkänd 2026-10-17 (inget)
