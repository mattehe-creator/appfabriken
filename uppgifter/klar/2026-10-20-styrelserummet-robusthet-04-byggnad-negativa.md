# Uppgift: Negativa belopp i årsredovisningen

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt

## Uppgift
Utöka `parseBelopp` och/eller `hittaByggnadensVarde` så att negativa tal kan skrivas med minus eller inom parentes, t.ex. `(1 234)` eller `-500 tkr`. Returnera negativt `Long` i kronor när raden avser negativt värde.

## Exempel
- Indata: `parseBelopp("(1 234)", iTusentals = false)` → `-1234`
- Indata: `parseBelopp("-500", iTusentals = true)` → `-500000`
- Indata: `Byggnader och mark (2 500) tkr` → `Belopp(-2500000, iTusentals = true)`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HittaByggnadensVardeTest.hittaByggnadensVardeNegativa"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: underkänd 2026-10-20 av Cursor (PR #297). Ersatt av 2026-10-20-styrelserummet-robusthet-04-byggnad-negativa-f2.md
