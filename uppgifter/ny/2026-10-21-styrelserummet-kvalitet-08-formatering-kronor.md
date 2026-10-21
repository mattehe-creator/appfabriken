# Uppgift: formatera kronor

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FormateringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt (belopp med mellanslag)

## Uppgift
`fun kronor(belopp: Long): String`

- Positiva och noll: absolutbelopp med hårt mellanslag (U+00A0) som tusentalsavgränsare, sedan `" kr"`.
- Negativa: unicode-minus (U+2212) före formaterat absolutbelopp, sedan `" kr"`.
- `0` → `"0 kr"` (inget tusentalsavgränsare).

## Exempel
- `kronor(1_234_567)` → `"1\u00A0234\u00A0567 kr"`
- `kronor(0)` → `"0 kr"`
- `kronor(-500)` → `"\u2212500 kr"`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FormateringTest.kronor"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
