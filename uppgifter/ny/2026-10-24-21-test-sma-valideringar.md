# Uppgift: Test av små valideringar i Husloggen

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/SmaValideringarTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/DokumentValidering.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareValidering.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/BeloppTolkning.kt

## Uppgift
JUnit 4-tester för `valideraDokument(titel, harFil)`, `valideraMatare(namn, enhet)` och `tolkaValfrittOreHeltal(text)`.

## Exempel
- `valideraDokument("Kvitto tak", true)` → `null`
- `valideraDokument("  ", true)` → `DokumentFel.TitelSaknas`
- `valideraDokument("", false)` → `DokumentFel.TitelSaknas` (titeln kontrolleras först)
- `valideraDokument("Manual", false)` → `DokumentFel.FilSaknas`
- `valideraMatare("El", "kWh")` → `null`
- `valideraMatare(" ", "kWh")` → `MatareFel.NamnSaknas`
- `valideraMatare("El", "")` → `MatareFel.EnhetSaknas`
- `tolkaValfrittOreHeltal("")` → `null`
- `tolkaValfrittOreHeltal(" 1500 ")` → `1500L`
- `tolkaValfrittOreHeltal("0")` → `0L`
- `tolkaValfrittOreHeltal("-1")` → `null`
- `tolkaValfrittOreHeltal("12,5")` → `null`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SmaValideringarTest"

## Gör inte
- Ändra andra filer.
