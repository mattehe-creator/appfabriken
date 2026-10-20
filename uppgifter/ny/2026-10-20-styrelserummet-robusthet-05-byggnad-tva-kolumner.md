# Uppgift: Två kolumner och årtalsrubrik

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt

## Uppgift
Utöka `hittaByggnadensVarde` för tabellrader med **två belopp** (innevarande år och föregående år): välj alltid **första** beloppet efter rubriken `Byggnader och mark` / `Byggnader`. En rubrikrad som bara innehåller årtal (t.ex. `2024 2023`) ska inte tolkas som belopp.

## Exempel
- Indata:
  ```
  Byggnader och mark
  2024 2023
  12 500 000 11 800 000
  ```
  Förväntat: `Belopp(12500000, iTusentals = false)`
- Indata: `Byggnader och mark 2024 2023` (inga belopp) → `null`
- Indata: `Byggnader 10 000 000` (en kolumn) → oförändrat

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HittaByggnadensVardeTest.hittaByggnadensVardeTvaKolumner"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
