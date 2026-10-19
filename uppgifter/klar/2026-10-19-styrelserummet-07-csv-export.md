# Uppgift: CSV-export av komponentlista

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 1
beror-på: 2026-10-19-styrelserummet-03-bokfort-varde-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/CsvExportTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt

## Uppgift
`fun tillCsv(komponenter: List<Komponent>, arsslut: Int): String`

- Fältskiljare: **semikolon** (`;`).
- Rubrikrad på svenska (namn, grupp, anskaffningsvärde, anskaffningsår, nyttjandeperiod, restvärde, avskrivning året, bokfört värde, källa, säkerhet — formulera kort och tydligt).
- Heltal utan tusentalsavgränsare.
- Fält som innehåller semikolon, citattecken (`"`) eller radbrytning ska omslutas med citattecken; `"` i text dubbleras (RFC 4180).
- Sista raden i filen (egen rad, inte CSV-kolumn): `UTKAST. Beslutas av förening och revisor.`

## Exempel
- Namn `Test; AB` → fältet citeras.
- Källa med `"citat"` → dubblerade citattecken inuti fältet.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*CsvExportTest"

Testa uttryckligen alla tre citatfallen (semikolon, citattecken, radbrytning).

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-19 (hela implementationen — lokal modell levererade tomma filer)
