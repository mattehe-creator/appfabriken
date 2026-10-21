# Uppgift: Bokfört värde aldrig negativt eller över anskaffningsvärdet

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallGranserTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt
- styrelserummet/regler/K3.md

## Uppgift
Testa `bokfortVarde(k: Komponent, arsslut: Int)` för flera år (före anskaffning, mitt i period, efter slut) och assert:
- resultatet är **≥ 0**,
- resultatet är **≤ anskaffningsvarde** (bokfört värde ska inte överstiga anskaffningsvärdet).

Använd minst en komponent med avrundningsrest (100_000 kr, 3 års period) och en lång period.

Vid fel: rätta högst i `BokfortVarde.kt`.

## Exempel
- 1_200_000 kr, 2010, 40 år, arsslut 2025 → 720_000 (som befintligt test), och för arsslut 2055 efter full avskrivning → 0, aldrig negativt.
- Före anskaffning (arsslut 2009) → 1_200_000 (oförändrat), inte negativt.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*BokfortVardeGransfallGranserTest"

## Gör inte
- Ändra `BokfortVardeTest.kt` eller modeller.
- Lägga till beroenden.
