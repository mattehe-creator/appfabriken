beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Grenen ändrar bara de två angivna Kotlin-filerna plus flytt av uppgiftsfil (`ny` → `pagar`); inga filer i fel sökväg.
- `NyckeltalCsv.kt` och `NyckeltalCsvTest.kt` är tomma (0 byte) — ingen `nyckeltalCsv`, inga tester.
- Uppgiften kräver CSV enligt exemplen med rubrik `nyckeltal;år…`, pivot per etikett, citering som i `CsvExport.kt` och avslut med `UTKAST_RAD`.
- Inga nya beroenden, behörigheter eller nätverk (ingen kod att granska).
- Ingen UI; inga hårdkodade användarsträngar i levererad kod.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Nyckeltal som CSV

## Vad som saknades (försök 1)
- Båda Kotlin-filerna skapades men är helt tomma — inget committat innehåll trots commit-meddelande om Aider-ändringar.
- Implementera `nyckeltalCsv` i `NyckeltalCsv.kt` och minst tre JUnit 4-tester i `NyckeltalCsvTest.kt` som assertar exakt sträng enligt exemplen nedan.

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsv.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRader.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/CsvExport.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondplanCsvTest.kt (JUnit 4, `UTKAST_RAD`)

## Uppgift
```kotlin
fun nyckeltalCsv(perAr: List<Pair<Int, List<NyckeltalRad>>>): String
```

`perAr` är (år, rader) i den ordning kolumnerna ska stå. Alla år har samma etiketter i samma ordning (ta etikettordning från första årets lista).

- Rubrikrad: `nyckeltal;ÅR1;ÅR2;…` (år som heltal, ingen citering).
- En rad per etikett: `etikett;värde år 1;värde år 2;…` — kör etikett och varje värde genom samma citeringslogik som `csvFalt` i `CsvExport.kt` (kopiera den privata funktionen till denna fil; ändra inte `CsvExport.kt`).
- Sist `UTKAST_RAD`. Rader skiljs med `\n` och filen slutar med `\n`, som `tillCsv` och `fondplanCsv`.
- Tom `perAr` → exakt två rader: `nyckeltal` och `UTKAST_RAD` (ingen datarad).

## Exempel
- `listOf(2025 to listOf(NyckeltalRad("Sparande per kvm", "309 kr/kvm")), 2024 to listOf(NyckeltalRad("Sparande per kvm", "280 kr/kvm")))` → `"nyckeltal;2025;2024\nSparande per kvm;309 kr/kvm;280 kr/kvm\n" + UTKAST_RAD + "\n"`
- Ett värde `a;b` skrivs `"a;b"` (citerat) i CSV-raden.
- `nyckeltalCsv(emptyList())` → `"nyckeltal\n$UTKAST_RAD\n"`

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*NyckeltalCsvTest"

Använd `org.junit.Test` och `org.junit.Assert.assertEquals` (inte `kotlin.test`). Minst: (1) två år enligt första exemplet, (2) citering av semikolon i värde, (3) tom lista.

## Gör inte
- Ändra andra filer.
- Lämna filerna tomma.
```

## Frågor till Claude
- inga
