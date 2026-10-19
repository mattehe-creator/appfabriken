# Uppgift: Bokfört värde

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 1
beror-på: 2026-10-19-styrelserummet-02-arsavskrivning.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt

## Uppgift
`fun bokfortVarde(k: Komponent, arsslut: Int): Long`

Anskaffningsvärde minus summan av `avskrivningFor(k, ar)` för alla år från anskaffningsår till och med `arsslut`. Resultatet ska **aldrig** understiga `restvarde`.

## Exempel
- 1_200_000 kr, år 2010, period 40 → vid arsslut 2025: 1_200_000 − 16×30_000 = 720_000.
- Samma komponent, arsslut 2009 (före anskaffningsår): 1_200_000.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*BokfortVardeTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
