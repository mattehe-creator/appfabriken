# Uppgift: Avstämning exakt träff, 1 kr och extremvärden

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvstamningGransfallTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt
- uppgifter/klar/2026-10-19-styrelserummet-04-avstamning.md

## Uppgift
Testa `stamAv(komponenter, byggnadensVarde)`:
- **Exakt träff:** summa komponenter = byggnadens värde → `stammer == true`, `differens == 0`.
- **Skillnad 1 kr:** byggnad 10_000_000, komponenter 9_999_999 → `stammer == false`, `differens == 1`.
- **Tolerans enligt koden:** det finns ingen separat tolerans i `stamAv` — endast `differens == 0L` ger `stammer`. Testa att skillnad 1 kr **inte** räknas som stämmer (dokumentera beteendet i testnamn/kommentar).
- **Byggnadsvärde 0** med komponenter som summerar > 0 → negativ differens (`byggnadensVarde - summa`).
- **Negativ differens:** komponenter summerar högre än byggnad → differens < 0, `stammer == false`.

Vid fel: rätta högst i `Avstamning.kt`.

## Exempel
- Byggnad 6_000_000 + 4_000_000 i komponenter, byggnad 10_000_000 → stämmer.
- Byggnad 0, en komponent 100_000 → differens −100_000, stämmer false.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvstamningGransfallTest"

## Gör inte
- Ändra `ReglerTest.kt` eller modeller.
- Lägga till beroenden.
