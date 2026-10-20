# Uppgift: Maskera inte org, bankgiro, belopp och datum

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt

## Uppgift
Säkerställ att `maskaPersonuppgifter` **inte** maskar:
- organisationsnummer `769612-3456`,
- bankgiro `123-4567`,
- belopp `1 234 567`, årtal `2024`, datum `2025-12-31`,
- fastighetsbeteckningar som `BRF Solen 1`.

Personnummer och telefon i samma text ska fortsatt maskas.

## Exempel
- Indata: `Org 769612-3456, bankgiro 123-4567` → oförändrat
- Indata: `Värde 1 234 567 kr, år 2024, per 2025-12-31, BRF Solen 1` → oförändrat
- Indata: `Org 769612-3456, kontakt 850709-1234, 070-123 45 67` → org oförändrat, personnummer och telefon maskade

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*MaskaPersonuppgifterTest.maskaInteFalskaTrffar"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
