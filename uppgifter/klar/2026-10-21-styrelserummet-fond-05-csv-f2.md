# Uppgift: Fondplanen som CSV

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-fondplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-fond-04-underskott-f2.md

## Vad som saknades
Försök 1 skapade tomma filer `FondplanCsv.kt` och `FondplanCsvTest.kt` utan kod. Implementera funktionen och minst två JUnit 4-tester enligt nedan. Fyll i de befintliga filerna — skapa inte dubbletter på annan sökväg.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondplanCsv.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Fondplan.kt

## Uppgift
Skapa `FondplanCsv.kt` i paketet `se.tmconnect.styrelserummet.regler`:

fun fondplanCsv(utveckling: List<FondAr>): String

Samma format som `tillCsv` i `CsvExport.kt`: semikolon som avgränsare, `\n` mellan rader, belopp som heltal utan mellanslag eller tusentalsavgränsare, och sist raden `UTKAST_RAD` (importera från `se.tmconnect.styrelserummet.underlag.UTKAST_RAD`) följd av `\n`.

Rubrikrad exakt: `år;avsättning;uttag;fond vid årets slut`. Därefter en rad per `FondAr` i samma ordning som listan, med `FondAr.ar`, `FondAr.avsattning`, `FondAr.uttag` och `FondAr.utgaende` som heltal i textform. Negativt utgående fondbelopp skrivs med minustecken (t.ex. `-160000`), inte parentes.

## Exempel
- `[FondAr(2026, 180000, 100000, 380000)]` → exakt `"år;avsättning;uttag;fond vid årets slut\n2026;180000;100000;380000\n$UTKAST_RAD\n"`
- `[FondAr(2028, 180000, 900000, -160000)]` → dataraden `2028;180000;900000;-160000` i samma CSV-struktur som ovan.
- Tom lista → exakt `"år;avsättning;uttag;fond vid årets slut\n$UTKAST_RAD\n"`

## Test som ska gå igenom
Skriv `FondplanCsvTest.kt` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`). Minst:
- ett test som jämför hela strängen mot exemplet med ett positivt `FondAr`;
- ett test med negativt `utgaende` som assertar hela strängen inklusive minustecken;
- ett test för tom lista mot exakt förväntad sträng (som `CsvExportTest.tomListaHarRubrikOchUtkastRad`).

gradle :styrelserummet:testDebugUnitTest --tests "*FondplanCsvTest"

## Gör inte
- Ändra andra filer.
- Ändra `CsvExport.kt` eller `Fondplan.kt`.
- Lägga till beroenden.
- Använda `kotlin.test`.

utfall: godkänd 2026-10-22 (hela implementationen — PR #485 hade ingen kod)
