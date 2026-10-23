# Uppgift: Hela flödet med exempel-PDF och inspelad tolkare

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-11-inspelad-tolkare-f2.md

## Vad som saknades
- Ingen Kotlin-kod levererades — bara uppgiftsflytt till `pagar/`.
- Skapa filen `ExempelFlodeTest.kt` enligt stegen nedan.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelFlodeTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanSaknadKostnadTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelInspelningTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt

## Uppgift
Ny testklass `ExempelFlodeTest` med test `exempelHandingarGerAvstamdUtkastMedVarningForBalkong`.

Använd **JUnit 4** (`org.junit.Test`, `org.junit.Assert`).

1. Plan-PDF i minnet: om `exempelUnderhallsplanPdf()` finns i `ExempelPdf.kt`, använd den. Annars `skrivPdf(underhallsplanSidor())` (samma innehåll).
2. Årsredovisnings-PDF: `exempelArsredovisningPdf()` om den finns, annars `skrivPdf(arsredovisningSidor())`.
3. `planText = PdfLasare().lasSomText(planBytes).getOrThrow()`
4. `arText = PdfLasare().lasSomText(arBytes).getOrThrow()`
5. `utkast = byggUtkast(planText, arText, exempelForening(), inspeladTolkare(), nuAr = RAKENSKAPSAR)`
6. Assert: `utkast.avstamning!!.stammer == true` och `utkast.byggnadensVarde == BYGGNADENS_VARDE` (konstanten i `ExempelData.kt`).
7. Assert: `utkast.komponenter.size >= 13`.
8. Balkong: hitta komponenten `"Balkonger"`. Efter `byggUtkast` ska den ha fått uppskattat värde — assert `Sakerhet.UPPSKATTNING` (inte bara `LAG` från JSON).
9. Assert: `utkast.anmarkningar` innehåller text om saknad kostnad för balkong (t.ex. från inspelad JSON `"Kostnad saknas i planen för Balkonger."`) **eller** varning via `anmarkningarFor` (t.ex. `"uppskattat anskaffningsvärde"` och `"Balkonger"`). Använd `assertTrue(utkast.anmarkningar.any { ... })` med `contains(..., ignoreCase = true)`.

Importera från paketen `se.tmconnect.styrelserummet.underlag`, `se.tmconnect.styrelserummet.underlag.exempel`, `se.tmconnect.styrelserummet.pdf`, `se.tmconnect.styrelserummet.modell` efter behov.

## Exempel
- Avstämning stämmer trots saknad kostnad i planen; inspelad tolkare fyller i Balkonger; `byggUtkast` uppskattar resten så summan matchar byggnadens värde 27 000 000 kr.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*ExempelFlodeTest"

## Gör inte
- Ändra produktionskod om testet redan går grönt.
- Lägga in riktiga API-anrop.
- Använda `kotlin.test`.
