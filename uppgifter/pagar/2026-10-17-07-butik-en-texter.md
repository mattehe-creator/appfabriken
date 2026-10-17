# Uppgift: engelska butikstexter

uppdrag: uppdrag/2026-10-17-husloggen-butik-mork-valkomst.md
försök: 1
beror-på: 2026-10-17-06-butik-sv-lang-beskrivning-f2.md

## Ändra bara dessa filer
- husloggen/butik/en/titel.txt
- husloggen/butik/en/kort.txt
- husloggen/butik/en/lang.txt

## Läs som förebild (ändra inte)
- husloggen/butik/sv/titel.txt
- husloggen/butik/sv/kort.txt
- husloggen/butik/sv/lang.txt
- husloggen/APP.md
- husloggen/src/test/kotlin/se/tmconnect/husloggen/butik/ButikTexterTest.kt

## Uppgift
Skriv **engelska** Google Play-texter som motsvarar den svenska långa beskrivningen:

1. `titel.txt` — högst **30 tecken** (arbetsnamnet Husloggen / House Log får användas preliminärt).
2. `kort.txt` — högst **80 tecken**.
3. `lang.txt` — högst **4 000 tecken**, samma teman som svenska: tasks, log, receipts, sale summary, offline/privacy, free tier and Pro.

Nämn uttryckligen att **tax information in the app applies to Sweden only** and is **information, not advice**.

**Gör inte:** lova juridiskt utfall; inga `http` eller `www`; inga namngivna konkurrenter eller påhittade recensioner.

## Exempel
- `ButikTexterTest` passerar för alla tre filer under `en/`.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra svenska filer eller appkod.
- Lägga till beroenden.
