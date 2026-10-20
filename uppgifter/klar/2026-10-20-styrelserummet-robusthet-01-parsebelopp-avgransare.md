# Uppgift: parseBelopp med olika tusentalsavgränsare

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt

## Uppgift
Utöka `internal fun parseBelopp(text: String, iTusentals: Boolean): Long?` så att tusentalsavgränsare kan vara:
- vanligt mellanslag (som idag),
- hårt mellanslag (U+00A0),
- smalt mellanslag (U+2009),
- punkt mellan siffergrupper (t.ex. `12.345.678`).

När både punkt och komma förekommer ska komma tolkas som decimal (`1.234,5`). Decimaltecken ska fortfarande fungera som idag (`12,3`). Befintliga exempel ska ge samma resultat.

## Exempel
- Indata: `parseBelopp("12\u00A0345", iTusentals = false)` → `12345`
- Indata: `parseBelopp("12.345.678", iTusentals = false)` → `12345678`
- Indata: `parseBelopp("1.234,5", iTusentals = true)` → `1234500`
- Indata: `parseBelopp("45 000", iTusentals = true)` → `45000000` (oförändrat)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HittaByggnadensVardeTest.parseBeloppAvgransare"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: godkänd 2026-10-20 (test 12,345 utan tusental; förenklad parseBelopp)
