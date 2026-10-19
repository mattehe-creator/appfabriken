# Uppgift: Summering per komponentgrupp

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 1
beror-på: 2026-10-19-styrelserummet-03-bokfort-varde-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt

## Uppgift
`data class GruppSumma(val grupp: Komponentgrupp, val anskaffning: Long, val avskrivningAret: Long, val bokfort: Long)`

`fun summeraPerGrupp(komponenter: List<Komponent>, arsslut: Int): List<GruppSumma>`

Summera per `Komponentgrupp`: anskaffningsvärden, `avskrivningFor` för år `arsslut`, och `bokfortVarde` vid arsslut. Returnera en rad per grupp som förekommer i listan, sorterad i **enum-ordning** (`Komponentgrupp.entries`).

## Exempel
- Två komponenter i STOMME och en i EL → tre rader i enum-ordning (STOMME före EL).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SummeringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
