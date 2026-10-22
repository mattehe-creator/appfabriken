# Uppgift: stamma.csv med prisbasbelopp

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/resources/stamma.csv (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/resources/nyttjandeperioder.csv
- styrelserummet/regler/STAMMA.md

## Uppgift
Skapa `stamma.csv` med semikolon som avgränsare och rubrikrad:

`typ;ar;varde;enhet;kalla`

Rader (exakta belopp):

| typ | ar | varde | enhet | kalla |
|---|---|---:|---|---|
| prisbasbelopp | 2020 | 47300 | kr | SCB prisbasbelopp 2020 |
| prisbasbelopp | 2021 | 47600 | kr | SCB prisbasbelopp 2021 |
| prisbasbelopp | 2022 | 48300 | kr | SCB prisbasbelopp 2022 |
| prisbasbelopp | 2023 | 52500 | kr | SCB prisbasbelopp 2023 |
| prisbasbelopp | 2024 | 57300 | kr | SCB prisbasbelopp 2024 |
| prisbasbelopp | 2025 | 58800 | kr | SCB prisbasbelopp 2025 |
| prisbasbelopp | 2026 | 59200 | kr | SCB prisbasbelopp 2026 |
| referens_overlatelse | 0 | 25 | promille_av_pbb | STAMMA.md Osäkert — typisk stadgarnivå 2,5 %, inte lagtak |
| referens_pantsattning | 0 | 10 | promille_av_pbb | STAMMA.md Osäkert — typisk stadgarnivå 1 %, inte lagtak |

Test läser filen från classpath (`javaClass.getResourceAsStream("/stamma.csv")`) och kontrollerar att det finns minst 7 rader med `prisbasbelopp` och att 2026 = 59200.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*StammaCsvTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
