# Uppgift: Avstämning mot byggnadens värde

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 1
beror-på: 2026-10-19-styrelserummet-01-validera-komponent.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvstamningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt

## Uppgift
`data class Avstamning(val summa: Long, val differens: Long, val stammer: Boolean)`

`fun stamAv(komponenter: List<Komponent>, byggnadensVarde: Long): Avstamning`

- `summa` = summan av `anskaffningsvarde` för alla komponenter.
- `differens` = `byggnadensVarde - summa`.
- `stammer` = true när `differens == 0L`.

## Exempel
- Byggnad 10_000_000, komponenter 6_000_000 + 4_000_000 → stammer true, differens 0.
- Byggnad 10_000_000, summa 9_999_000 → stammer false, differens 1_000.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvstamningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
