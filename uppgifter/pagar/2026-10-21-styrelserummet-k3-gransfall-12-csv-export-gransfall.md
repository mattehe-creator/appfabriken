# Uppgift: CSV-export gränsfall tecken och tal

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/CsvExportGransfallTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/CsvExportTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Utkast.kt (konstant `UTKAST_RAD`)

## Uppgift
Testa `tillCsv(komponenter, arsslut)` i **ny** testfil (rör inte `CsvExportTest.kt`):
- Namn med **semikolon**, **citattecken** och **radbrytning** — korrekt citering enligt `csvFalt`.
- **Svenska tecken** i namn och källa (å, ä, ö) ska bevaras i UTF-8-text.
- **Talkolumner** (`anskaffningsvärde`, avskrivning, bokfört m.m.) får **inte** innehålla hårda mellanslag (U+00A0) eller tusentalsavgränsare — bara siffror.
- **Rubrikrad** och **utkastrad** (`UTKAST_RAD`) ska finnas; **tom lista** ger enbart rubrik + utkastrad (samma som befintligt beteende).

Vid fel: rätta högst i `CsvExport.kt`.

## Exempel
- Namn `Fönster; "special"\nrad`, grupp STOMME → fält citerat, semikolon inuti namnet escaped.
- Namn `Årsredovisning 2025` → läsbart i CSV utan korruption.
- `tillCsv(emptyList(), 2025)` → exakt två rader innehåll (rubrik + utkast), inga data rader.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*CsvExportGransfallTest"

## Gör inte
- Ändra `CsvExportTest.kt`, `GrupperaNamn`, `Formatering`, `Sammanfattning`, `Jamforelse`, `FelHantering`, `Utskrift` (andra parallella uppgifter).
- Lägga till beroenden.
