# Uppgift: Avskrivning med nyttjandeperiod 1 år

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallEnArTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/regler/K3.md

## Uppgift
Testa komponent med `nyttjandeperiodAr = 1`. Hela avskrivningsbart belopp ska avskrivas **samma år** som anskaffningsåret. Året före och efter ska ge 0.

Testa med och utan restvärde (t.ex. anskaffningsvärde 50_000, restvärde 5_000 → avskrivning 45_000 det enda året).

Vid fel: rätta högst i `Avskrivning.kt`.

## Exempel
- 50_000 kr, anskaffningsår 2024, period 1, restvärde 0 → 2024: 50_000; 2023 och 2025: 0.
- 50_000 kr, restvärde 5_000 → 2024: 45_000.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallEnArTest"

## Gör inte
- Ändra modellklasser eller andra testfiler.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #350). Ersatt av 2026-10-21-styrelserummet-k3-gransfall-03-avskrivning-period-ett-ar-f2.md
