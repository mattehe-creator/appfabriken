# Uppgift: Dataminimering efter klart jobb

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar-forsta-korning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/DataminimeringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FalskTolkare.kt

## Uppgift
Test `efterKlartJobbBaraTillatnaFiler`:

1. `testApplication` med `synkronJobb = true`, `FalskTolkare`, temp `dataRot`.
2. POST två små PDF:er (plan + årsredovisning), vänta tills status **KLAR**.
3. Lista **alla** filer rekursivt i jobbkatalogen. Tillåtna: uppladdade PDF:er under `filer/`, `meta.json`, `utkast.json` — **inga** andra filer (inga `.txt`, `maskad*`, `log*`, extra json).
4. Assert att **ingen** fil innehåller den råa PDF-texten sparad som separat textfil (kontrollera att det inte finns parallella `.txt`-kopior).

Denna uppgift ändrar **bara** testfilen. Om testet avslöjar extra filer ska produktionskod rättas i en senare uppgift (17 eller ny) efter Cursors granskning.

## Exempel
- Filträkning: exakt 2 PDF + meta + utkast = 4 filer totalt (sökvägsdjup inräknat).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*DataminimeringTest.efterKlartJobb*"

## Gör inte
- Spara maskad text till disk.
- Lägga till beroenden.
