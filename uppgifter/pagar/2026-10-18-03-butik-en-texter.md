# Uppgift: engelska butikstexter

uppdrag: uppdrag/2026-10-18-flextid-butik-mork-valkomst.md
försök: 1
beror-på: 2026-10-18-02-butik-sv-lang-beskrivning.md

## Ändra bara dessa filer
- flextid/butik/en/titel.txt
- flextid/butik/en/kort.txt
- flextid/butik/en/lang.txt

## Läs som förebild (ändra inte)
- flextid/butik/sv/titel.txt
- flextid/butik/sv/kort.txt
- flextid/butik/sv/lang.txt
- flextid/APP.md
- flextid/src/test/kotlin/se/tmconnect/flextid/butik/ButikTexterTest.kt

## Uppgift
Skriv **engelska** Google Play-texter som motsvarar de svenska filerna efter uppgift 01 och 02:

1. `en/titel.txt` — högst **30 tecken** (arbetsnamn Flextid eller kort engelsk titel som stämmer med appen).
2. `en/kort.txt` — högst **80 tecken**.
3. `en/lang.txt` — högst **4 000 tecken**, samma budskap som svenska `lang.txt`.

Samma regler som svenska: offline, no accounts, no ads, no analytics, 15 free day entries, Pro one-time purchase, export/backup always free, no legal advice, no collective agreement claims.

**Gör inte:** `http`, `www`, named competitors, fake reviews.

## Exempel
- `ButikTexterTest` passerar för alla tre engelska filer.

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ButikTexterTest"

## Gör inte
- Ändra svenska filer eller appkod.
- Lägga till beroenden.
