# Uppgift: Hela flödet med exempel-PDF och inspelad tolkare

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-11-inspelad-tolkare-f2.md

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelFlodeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanSaknadKostnadTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt

## Uppgift
Test `exempelHandingarGerAvstamdUtkastMedVarningForBalkong`:

1. `planText = PdfLasare().lasSomText(exempelUnderhallsplanPdf()).getOrThrow()`
2. `arText = PdfLasare().lasSomText(exempelArsredovisningPdf()).getOrThrow()`
3. `utkast = byggUtkast(planText, arText, exempelForening(), inspeladTolkare(), nuAr = RAKENSKAPSAR)`
4. Assert: `utkast.avstamning!!.stammer == true` och `utkast.byggnadensVarde == BYGGNADENS_VARde`
5. Assert: minst 13 komponenter (planens poster med kostnad); om balkong får uppskattat värde ska den ha `Sakerhet.UPPSKATTNING`
6. Assert: `utkast.anmarkningar` innehåller en text om saknad kostnad för balkong (från inspelad JSON) eller motsvarande varning via `anmarkningarFor`

## Exempel
- Avstämning stämmer trots saknad kostnad i planen (AI/tolkaren fyller i enligt inspelning).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelFlodeTest"

## Gör inte
- Ändra produktionskod om testet redan går grönt.
- Lägga in riktiga API-anrop.
