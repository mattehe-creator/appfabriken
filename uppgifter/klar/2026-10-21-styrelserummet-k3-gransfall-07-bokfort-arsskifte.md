# Uppgift: Bokfört värde vid årsskifte

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallArsskifteTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt

## Uppgift
Verifiera att bokfört värde **minskar (eller stannar på restvärde)** när `arsslut` går från ett år till nästa, och att skillnaden mellan två på varandra följande årsslut motsvarar avskrivningen det senare året (för år inom avskrivningsperioden).

Testa särskilt årsskiftet **då anskaffningsåret börjar** (arsslut = anskaffningsår − 1 vs anskaffningsår).

Vid fel: rätta högst i `BokfortVarde.kt` eller `Avskrivning.kt` (max en av dem i samma PR).

## Exempel
- 90_000 kr, anskaffningsår 2025, period 3, restvärde 0:
  - `bokfortVarde(..., 2024)` = 90_000
  - `bokfortVarde(..., 2025)` = 60_000
  - skillnad 30_000 = `avskrivningFor(..., 2025)`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*BokfortVardeGransfallArsskifteTest"

## Gör inte
- Ändra befintliga testfiler.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #358). Ersatt av 2026-10-21-styrelserummet-k3-gransfall-07-bokfort-arsskifte-f2.md
