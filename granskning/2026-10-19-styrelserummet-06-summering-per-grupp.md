beslut: underkänd
granskare: cursor
datum: 2026-10-19

## Kontrollerat
- Endast tillåtna filer ändrade (Summering.kt, SummeringTest.kt, uppgift flyttad till pagar) — ja, rätt sökvägar.
- Koden gör det uppgiften säger — nej: båda Kotlin-filerna är tomma (0 byte).
- Testerna jämför resultat mot exemplen — nej: inga tester.
- Inga nya beroenden, behörigheter eller nätverk — ja.
- Inga hårdkodade UI-strängar — ja (ingen UI-kod).
- JUnit 4 (org.junit) — ej tillämpligt (ingen testkod).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Summering per komponentgrupp

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 2
beror-på: 2026-10-19-styrelserummet-03-bokfort-varde-f2.md

## Vad som saknades
- `Summering.kt` och `SummeringTest.kt` committades som tomma filer (0 byte). Commit-meddelandet nämnde «okommittade ändringar efter Aider» — implementationen följde inte med in i git.
- Ingen `GruppSumma`, ingen `summeraPerGrupp`, inga tester. Uppgiften är därmed helt oimplementerad trots grönt bygge (tom testfil ger inga tester).

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt (ny/fyll i)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt (ny/fyll i)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt (JUnit 4, hjälpfunktion `komponent`)

## Uppgift
Implementera i paketet `se.tmconnect.styrelserummet.regler`:

`data class GruppSumma(val grupp: Komponentgrupp, val anskaffning: Long, val avskrivningAret: Long, val bokfort: Long)`

`fun summeraPerGrupp(komponenter: List<Komponent>, arsslut: Int): List<GruppSumma>`

Summera per `Komponentgrupp`:
- `anskaffning` = summan av `anskaffningsvarde` för alla komponenter i gruppen
- `avskrivningAret` = summan av `avskrivningFor(k, arsslut)` per komponent i gruppen
- `bokfort` = summan av `bokfortVarde(k, arsslut)` per komponent i gruppen

Returnera **en rad per grupp som förekommer** i listan (inga rader för grupper utan komponenter), sorterad i **enum-ordning** (`Komponentgrupp.entries` — t.ex. STOMME före EL).

Använd befintliga funktionerna `avskrivningFor` och `bokfortVarde` i samma paket; duplicera inte avskrivningslogik.

## Exempel
- Två komponenter i STOMME och en i EL, `arsslut = 2025`:
  - STOMME: `1_200_000` kr, anskaffningsår 2010, period 40 → avskrivning år 2025 = `30_000`, bokfört vid 2025 = `720_000`
  - STOMME: `100_000` kr, anskaffningsår 2020, period 3 → avskrivning år 2025 = `0` (perioden slut 2022), bokfört = `0`
  - EL: `500_000` kr, anskaffningsår 2015, period 25 → avskrivning år 2025 = `20_000`, bokfört = `280_000`
- Förväntat: **två** rader, ordning STOMME sedan EL:
  - STOMME: anskaffning `1_300_000`, avskrivningAret `30_000`, bokfort `720_000`
  - EL: anskaffning `500_000`, avskrivningAret `20_000`, bokfort `280_000`
- En tom lista → tom `List<GruppSumma>`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SummeringTest"

Testerna ska använda `org.junit.Test` och `org.junit.Assert.assertEquals` (som `BokfortVardeTest`), inte `kotlin.test`. Minst ett test ska bygga listan ovan och jämföra varje fält i varje `GruppSumma` mot exemplen.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Committa tomma filer — kör testkommandot lokalt före push.
```

## Frågor till Claude
- inga
