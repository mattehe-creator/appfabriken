# Uppgift: Avskrivningsplan som CSV

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-plan-05-ersatt-f2.md

## Vad som saknades
Försök 1 skapade tomma filer `AvskrivningsplanCsv.kt` och `AvskrivningsplanCsvTest.kt` utan kod. Implementera funktionen och minst två JUnit 4-tester enligt nedan. Fyll i de befintliga filerna — skapa inte dubbletter på annan sökväg.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanCsv.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningsplanCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivningsplan.kt

## Uppgift
Skapa `AvskrivningsplanCsv.kt` i paketet `se.tmconnect.styrelserummet.regler`:

fun avskrivningsplanCsv(plan: List<PlanAr>): String

Samma format som `tillCsv` i `CsvExport.kt`: semikolon som avgränsare, `\n` mellan rader, belopp som heltal utan mellanslag eller tusentalsavgränsare, och sist raden `UTKAST_RAD` (importera från `se.tmconnect.styrelserummet.underlag.UTKAST_RAD`) följd av `\n`.

Rubrikrad exakt: `år;avskrivning;bokfört värde vid årets slut`. Därefter en rad per `PlanAr` i samma ordning som listan: `år;avskrivning;bokfört värde vid årets slut` med `PlanAr.ar`, `PlanAr.avskrivning` och `PlanAr.bokfortVarde` som heltal i textform.

## Exempel
- `[PlanAr(2026, 60000, 780000), PlanAr(2027, 60000, 720000)]` →
  `"år;avskrivning;bokfört värde vid årets slut\n2026;60000;780000\n2027;60000;720000\n" + UTKAST_RAD + "\n"`
- Tom lista → exakt `"år;avskrivning;bokfört värde vid årets slut\n$UTKAST_RAD\n"`

## Test som ska gå igenom
Skriv `AvskrivningsplanCsvTest.kt` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`). Minst:
- ett test som jämför hela strängen mot exemplen med två `PlanAr`;
- ett test för tom lista mot exakt förväntad sträng (som `CsvExportTest.tomListaHarRubrikOchUtkastRad`).

gradle :styrelserummet:testDebugUnitTest --tests "*AvskrivningsplanCsvTest"

## Gör inte
- Ändra andra filer.
- Ändra `CsvExport.kt`.
- Lägga till beroenden.
- Använda `kotlin.test`.
