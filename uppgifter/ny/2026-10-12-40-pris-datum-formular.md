# Uppgift: pris och köpdatum i formuläret

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Formatering.kt

## Uppgift
1. **Köpdatum**: Ersätt datum-knappen med `OutlinedTextField` som visar formaterat datum (`formateraDatum`) och är **read-only**; `trailingIcon` med kalenderikon öppnar befintlig `DatePickerDialog`. Etikett `falt_datum`.

2. **Pris**: `OutlinedTextField` med `KeyboardType.Decimal`, suffix « kr » (ny sträng `falt_pris_suffix` eller hårdkoda suffix endast i `strings.xml` som «kr»), etikett `falt_pris`. Behåll validering via `valideraKopFormular` / `tolkaPrisTillOre`.

## Exempel
- Köpdatum 2025-03-10 → fältet visar lokalt medium-format, tryck på kalenderikon → dialog.
- Pris «1299,50» → sparas som ore som idag.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FormularValideringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
