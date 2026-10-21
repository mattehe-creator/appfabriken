# Uppgift: formatera procent och årsintervall

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-08-formatering-kronor-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FormateringTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt (`kronor`)

## Uppgift
`fun procent(del: Long, helhet: Long): String`

- Om `helhet == 0L`: returnera `"–"` (unicode en-dash U+2013).
- Annars: en decimal, svensk komma som decimaltecken, avslutas med `" %"` (mellanslag före procenttecken).
- Avrunda till närmaste tiondel (t.ex. `1/3` → `"33,3 %"`).

`fun arsintervall(fran: Int, till: Int): String`

- `"$fran\u2013$till"` med en-dash (U+2013) mellan årtalen.

## Exempel
- `procent(1, 8)` → `"12,5 %"`
- `procent(0, 0)` → `"–"`
- `arsintervall(2010, 2050)` → `"2010\u20132050"`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FormateringTest.procentOchArsintervall"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
