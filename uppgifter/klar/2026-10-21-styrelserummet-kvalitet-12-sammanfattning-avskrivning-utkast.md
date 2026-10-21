# Uppgift: sammanfatta avskrivning och utkast

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-11-sammanfattning-osaker-och-avstamning.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Sammanfattning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SammanfattningTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt

## Uppgift
Utöka `sammanfatta` till **högst sex** meningar (behåll ordningen från tidigare uppgifter). Lägg till:

5. `"Avskrivningen för räkenskapsåret är A totalt."` där A är `kronor(summan av avskrivningFor(k, arsslut) för alla k)`.
6. `"Detta är ett utkast – kontrollera uppgifterna mot handlingarna innan beslut."` (alltid sist).

Listan ska aldrig ha fler än sex element.

## Exempel
- Två komponenter med avskrivning 10_000 + 5_000 kr, arsslut 2025 → `"Avskrivningen för räkenskapsåret är 15\u00A0000 kr totalt."`
- Sista mening exakt som ovan även för tom lista.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SammanfattningTest.avskrivningOchUtkast"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: underkänd 2026-10-21 av Cursor (PR #395). Ersatt av 2026-10-21-styrelserummet-kvalitet-12-sammanfattning-avskrivning-utkast-f2.md
