# Uppgift: Test av franExportCsv (Styrelserummet-server)

uppdrag: uppdrag/claude/2026-10-27-testtackning-kvall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/CsvImportTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/CsvImport.kt (`franExportCsv`)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/CsvExport.kt (`tillCsv`, kolumnerna)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/ApiModell.kt (`KomponentDto`)

## Uppgift
`franExportCsv` läser in en CSV som tjänsten själv har exporterat (förra årets lista) för jämförelse. Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`) i paketet `se.tmconnect.styrelserummet.server`. Importera `Komponent`, `Komponentgrupp` och `Sakerhet` från `se.tmconnect.styrelserummet.modell`.

Rubrikraden i exemplen: `val rubrik = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"`.

## Exempel
- `rubrik + "\nTak plåt;Tak;1 200 000;2010;40;750000;Underhållsplan s. 4;HOG\nUTKAST. Beslutas av förening och revisor."` ger en komponent: `Komponent("Tak plåt", Komponentgrupp.TAK, 1_200_000, 2010, 40, 0, "Underhållsplan s. 4", Sakerhet.HOG)`.
- Samma text med `"﻿"` först ger samma resultat.
- Grupp `FASAD` (enum-namnet) ger `Komponentgrupp.FASAD`; grupp `Garage` (okänt) ger `Komponentgrupp.OVRIGT`.
- Säkerhet `okänd` ger `Sakerhet.MEDEL`.
- En rad med 7 fält hoppas över; en rad med anskaffningsvärdet `x` hoppas över.
- Citerad cell med semikolon: `"\"Fönster; trapphus\";Fönster och dörrar;10;2000;30;0;k;LAG"` ger namnet `Fönster; trapphus` och gruppen `FONSTER_DORRAR`.
- Cellen `'=SUMMA` som namn ger namnet `=SUMMA`.
- Bara rubrikraden ger en tom lista; tom text ger en tom lista.
- Fram och tillbaka: `franExportCsv(tillCsv(listOf(KomponentDto("Hiss", "HISS", 500_000, 2015, 25, 0, "UH-plan", "HOG")), 2026))` ger en komponent med namn `Hiss`, grupp `HISS`, värde 500 000, år 2015, period 25, källa `UH-plan` och säkerhet `HOG`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*CsvImportTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
