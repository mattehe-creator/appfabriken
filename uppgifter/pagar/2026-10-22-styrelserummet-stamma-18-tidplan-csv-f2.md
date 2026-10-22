# Uppgift: tidplanCsv

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-17-tidplan-rader-f2.md

## Vad som saknades
- PR #589 har bara commit «Uppgift startad» (flytt till `pagar/`). Varken `StammaUtskrift.kt` utökades med `STAMMA_RADGIVNING_RAD` och `tidplanCsv` eller testfilen `StammaTidplanCsvTest.kt` skapades.
- Implementera enligt uppgiften nedan. CSV-regler ska matcha privat `csvFalt` i `NyckeltalCsv.kt` (kopiera som privat funktion i `StammaUtskrift.kt` eller duplicera minimalt). Skriv JUnit 4-tester (`org.junit`, inte `kotlin.test`) som `assertEquals` hela CSV-strängen mot exemplen.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaUtskrift.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaTidplanCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsv.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsvTest.kt (JUnit 4, helsträngs-assert)
- styrelserummet/regler/STAMMA.md

## Uppgift
Lägg till i `StammaUtskrift.kt`:

const val STAMMA_RADGIVNING_RAD = "Allmän information, inte juridisk rådgivning. Kontrollera era stadgar."

fun tidplanCsv(rader: List<StammaTabellRad>): String

CSV med semikolon, rubrikrad exakt `etikett;datum;anmärkning;källa`, en datarad per element i `rader` (fältordning: etikett, datum, anmarkning, kalla från `StammaTabellRad`), **sista raden** exakt `STAMMA_RADGIVNING_RAD` (hela raden som en enda kolumn — ingen semikolon i texten). Avsluta hela strängen med `\n`.

Fält som innehåller semikolon, citattecken, radbrytning ska citeras enligt samma regel som `csvFalt` i `NyckeltalCsv.kt`.

## Exempel
- `tidplanCsv(listOf(StammaTabellRad("Test", "2026-01-01 (torsdag)", "", "")))` →
  ```
  etikett;datum;anmärkning;källa
  Test;2026-01-01 (torsdag);;
  Allmän information, inte juridisk rådgivning. Kontrollera era stadgar.

  ```
  (sista raden är radgivningstexten; strängen slutar med newline efter den raden.)
- `tidplanCsv(emptyList())` → rubrikrad, sedan radgivningsrad, sedan `\n` — inga datarader däremellan.
- Värde med semikolon i anmärkning ska citeras, t.ex. `"a;b"` → `"\"a;b\""` i CSV-fältet (samma som `NyckeltalCsvTest.semikolonIVardeCiteras`).

## Test som ska gå igenom
Skriv `StammaTidplanCsvTest` med minst:
- en rad med etikett `"Test"` — jämför hela utdata mot förväntad sträng inklusive rubrik och sista radgivningsrad;
- tom lista — rubrik + radgivning + avslutande newline;
- (valfritt) semikolon i fält citeras korrekt.

Kör:
`gradle :styrelserummet:testDebugUnitTest --tests "*StammaTidplanCsvTest"`

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Lägga filer i repots rot — all kod ska ligga under `styrelserummet/src/...`.
