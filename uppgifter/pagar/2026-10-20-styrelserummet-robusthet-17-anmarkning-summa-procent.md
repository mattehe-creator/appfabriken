# Uppgift: Anmärkning vid extrem avstämning i procent

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt

## Uppgift
Utöka `anmarkningarFor` med **två separata** anmärkningstexter (tydliga och olika formuleringar) när `utkast.byggnadensVarde` är satt och summan av komponenternas `anskaffningsvarde`:
- är **över 150 procent** av byggnadens redovisade värde, eller
- är **under 50 procent** av byggnadens redovisade värde.

Detta är utöver befintlig avstämningsanmärkning om exakt differens.

## Exempel
- Byggnad `10_000_000`, komponentsumma `16_000_000` → anmärkning om att summan överstiger 150 procent.
- Byggnad `10_000_000`, summa `4_000_000` → anmärkning om att summan understiger 50 procent.
- Summa `10_000_000` → inga procent-anmärkningar.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AnmarkningarTest.anmarkningSummaProcent"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
