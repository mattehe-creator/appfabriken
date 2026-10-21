# Uppgift: Summering tom lista, grupper och dold tom grupp

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringGransfallGrupperTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Testa `summeraPerGrupp(komponenter, arsslut)`:
- **Tom lista** → tom resultatlista.
- **En grupp** med flera komponenter → en `GruppSumma` med summerade fält.
- **Flera grupper** → en rad per grupp som har minst en komponent, i enum-ordning (`Komponentgrupp.entries`).
- **Grupp utan komponenter** (t.ex. HISS saknas i listan) → ska **inte** finnas i resultatet.

Vid fel: rätta högst i `Summering.kt`.

## Exempel
- Tom lista, arsslut 2025 → `emptyList()`.
- STOMME + EL + TAK med kända belopp → exakt 3 grupper, ingen rad för VENTILATION om den saknas.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SummeringGransfallGrupperTest"

## Gör inte
- Ändra `SummeringTest.kt`.
- Lägga till beroenden.
