# Uppgift: garantitid som chips

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GarantitidVal.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/GarantitidValTest.kt (ny)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/FormularValidering.kt

## Uppgift
1. **`GarantitidVal.kt`**: enum eller sealed class med val `FORVALD_MANADER = 24`, val `CHIP_MANADER = listOf(12, 24, 36)`, och `fun manaderFranVal(chipIndex: Int, annatText: String): Int?` där index 0–2 är fasta månader och index 3 («Annat») tolkar `annatText` som positivt heltal.

2. **`KopFormularSkarm.kt`**: Ersätt garantitid-textfältet med `FilterChip`/`SingleChoiceSegmentedButtonRow` (M3) för 12, 24, 36 mån och «Annat». Standardval **24** vid nytt köp. När «Annat» är valt, visa kort numeriskt fält under. Vid spara, skicka valt antal månader till `onSpara` som idag.

3. Strängar `garanti_chip_12`, `garanti_chip_24`, `garanti_chip_36`, `garanti_chip_annat` (sv/en) — lägg i `strings.xml` om de saknas efter del A (räknas mot filgränsen; annars hårdkoda inte).

## Exempel
- Nytt formulär → chip 24 markerat.
- Användaren väljer «Annat» och skriver «18» → sparar med `garantiManader = 18`.
- `manaderFranVal(1, "")` → 24.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GarantitidValTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: underkänd 2026-10-12 av Cursor (PR #101). Ersatt av 2026-10-12-39-garantitid-chips-f2.md
