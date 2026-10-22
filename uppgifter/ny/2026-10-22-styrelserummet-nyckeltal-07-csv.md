# Uppgift: Nyckeltal som CSV

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsv.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRader.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt

## Uppgift
```kotlin
fun nyckeltalCsv(perAr: List<Pair<Int, List<NyckeltalRad>>>): String
```

`perAr` är (år, rader) i den ordning de ska stå. Alla år har samma etiketter i samma ordning.

- Rubrikrad: `nyckeltal;ÅR1;ÅR2;…`.
- En rad per etikett: `etikett;värde år 1;värde år 2;…`.
- Sist `UTKAST_RAD`. Rader skiljs med `\n` och filen slutar med `\n`, som `tillCsv`.
- Fält med semikolon, citattecken eller radbrytning citeras som i `CsvExport.kt` (dubbla citattecken). Kopiera den privata hjälpfunktionen hellre än att ändra CsvExport.kt.
- Tom lista → bara rubriken `nyckeltal` och `UTKAST_RAD`.

## Exempel
- [(2025, [("Sparande per kvm", "309 kr/kvm")]), (2024, [("Sparande per kvm", "280 kr/kvm")])] → `"nyckeltal;2025;2024\nSparande per kvm;309 kr/kvm;280 kr/kvm\n" + UTKAST_RAD + "\n"`
- Ett värde "a;b" skrivs `"a;b"` med citattecken.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalCsvTest"

## Gör inte
- Ändra andra filer.
