# Uppgift: CSV-export gränsfall tecken och tal

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/CsvExportGransfallTest.kt` är tom efter försök 1 (Aider-commit utan innehåll).
- Ingen testklass `CsvExportGransfallTest`; uppgiftens exempel och krav på talkolumner/specialtecken är overifierade.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/CsvExportGransfallTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt (endast om testerna visar fel i `tillCsv` / `csvFalt`)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/CsvExportTest.kt (JUnit 4, privat `komponent(...)`, mönster för assert)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Utkast.kt (konstant `UTKAST_RAD`)

## Uppgift
Skapa testklassen `CsvExportGransfallTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert` — **inte** `kotlin.test`). Testa **endast** `tillCsv(komponenter, arsslut)` i denna fil; rör **inte** `CsvExportTest.kt`.

Minst följande tester (gärna fler om det klargör kraven):

1. **Kombinerad citering:** namn `Fönster; "special"\nrad`, grupp STOMME — CSV-fältet för namn ska vara citerat enligt `csvFalt` (semikolon och citattecken escaped, radbrytning inuti citat).
2. **Svenska tecken:** namn `Årsredovisning 2025` och källa med å/ä/ö (t.ex. `årsredovisning.pdf`) ska finnas i CSV-strängen oförändrade (UTF-8-text, ingen korruption).
3. **Talkolumner:** för en komponent med kända heltal (återanvänd samma värden som i `CsvExportTest.komponent` eller motsvarande) ska data raden **inte** innehålla U+00A0 (hårt mellanslag) eller tusentalsavgränsare (t.ex. mellanslag eller `1 200 000`) i kolumnerna anskaffningsvärde, avskrivning året eller bokfört värde — bara siffror som i befintliga `CsvExportTest`.
4. **Rubrik och utkast:** CSV börjar med exakt samma rubrikrad som `CsvExportTest.tomListaHarRubrikOchUtkastRad` och slutar med `UTKAST_RAD` + radbrytning.
5. **Tom lista:** `tillCsv(emptyList(), 2025)` ska vara **exakt** lika med strängen i `CsvExportTest.tomListaHarRubrikOchUtkastRad` (rubrik + utkastrad, inga datarader).

Vid fel i produktionskoden: rätta **högst** i `CsvExport.kt`.

## Exempel (ska finnas som assert eller motsvarande kontroll)
- Namn `Fönster; "special"\nrad`, grupp STOMME → fält citerat, semikolon inuti namnet escaped.
- Namn `Årsredovisning 2025` → läsbart i CSV utan korruption.
- `tillCsv(emptyList(), 2025)` → exakt två rader innehåll (rubrik + utkast), inga data rader.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*CsvExportGransfallTest"

## Gör inte
- Ändra `CsvExportTest.kt`, `GrupperaNamn`, `Formatering`, `Sammanfattning`, `Jamforelse`, `FelHantering`, `Utskrift`.
- Lägga till beroenden.
- Lägga filer i repots rot.

utfall: godkänd 2026-10-21 (återställde CsvExportTest.kt; trimEnd i rubrikOchUtkast)
