# Uppgift: sortera köp efter garantislut

uppdrag: uppdrag/2026-10-08-kop-databas-lista-formular.md
försök: 1
beror-på: 2026-10-08-01-garantitid.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KopSortering.kt (ny)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KopSorteringTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Garanti.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/data/Kop.kt

## Uppgift
Ren Kotlin i paketet `se.tmconnect.garantivalvet.regler`. Använd `garantiSlut` från `Garanti.kt`.

`fun sorteraEfterGaranti(lista: List<Kop>, idag: LocalDate): List<Kop>`

- Sortera så att köp vars garanti **inte** är utgången (`idag` ≤ garantislut) kommer först, med **närmaste garantislut** överst.
- Köp med utgången garanti (`idag` > garantislut) kommer **efter** alla aktiva, sorterade med **senast utgångna** sist (dvs minst nyligen utgångna först bland de utgångna).
- Vid lika garantislut: stabil ordning efter `vad` (alfabetiskt, `String.compareTo`).

## Exempel
- Tre köp med slut 2026-12-01, 2026-06-01, 2027-01-01; `idag = 2026-05-01` → ordning: 2026-06-01, 2026-12-01, 2027-01-01.
- Ett utgånget (slut 2025-01-01) och ett aktivt (slut 2026-12-01); `idag = 2026-05-01` → det aktiva först.
- Två utgångna: slut 2024-01-01 och 2023-06-01; `idag = 2026-05-01` → 2024-01-01 före 2023-06-01 (senast utgången sist).

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopSorteringTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
