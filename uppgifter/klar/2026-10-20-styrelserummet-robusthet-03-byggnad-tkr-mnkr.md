# Uppgift: Byggnadens värde i tkr och mnkr

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt

## Uppgift
Utöka `fun hittaByggnadensVarde(text: String): Belopp?` så att enheter **tkr** och **mnkr** (med eller utan mellanslag, t.ex. `12 345 tkr`, `12,3 mnkr`) räknas om till kronor. Sätt `Belopp.iTusentals` till `true` för tkr och `false` för mnkr (mnkr multipliceras med 1_000_000). Befintliga rader med `tusen kronor` / `tkr` ska fortsatt fungera.

## Exempel
- Indata: `Byggnader och mark 12 345 tkr` → `Belopp(12345000, iTusentals = true)`
- Indata: `Byggnader 12,3 mnkr` → `Belopp(12300000, iTusentals = false)`
- Indata: `Byggnader och mark 45 000 tusen kronor` → oförändrat som idag

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*HittaByggnadensVardeTest.hittaByggnadensVardeTkrMnkr"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: godkänd 2026-10-20 (JUnit-import, mnkr-decimaler, borttagen oanvänd variabel)
