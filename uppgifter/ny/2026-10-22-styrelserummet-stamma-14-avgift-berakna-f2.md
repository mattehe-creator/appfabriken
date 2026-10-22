# Uppgift: hösta avgift enligt promille

uppdrag: uppdrag/2026-10-22-styrelserummet-stammotidplan.md
försök: 2
beror-på: 2026-10-22-styrelserummet-stamma-13-prisbasbelopp-for-ar-f2.md

## Vad som saknades
- Varken `Overlatelseavgift.kt` eller `OverlatelseavgiftBeraknaTest.kt` skapades. PR #583 har bara commit «Uppgift startad» (flytt till `pagar/`).
- Implementera alla funktioner och enum enligt uppgiften nedan och skriv JUnit 4-tester som assertar exakt mot exemplen.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Overlatelseavgift.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/OverlatelseavgiftBeraknaTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/regler/STAMMA.md
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/StammaData.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StammaDataLasTest.kt (nycklar `referens_overlatelse_0`, `referens_pantsattning_0`)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/StegvisHojningTest.kt (JUnit 4-stil)

## Uppgift
Skapa `Overlatelseavgift.kt` i paketet `se.tmconnect.styrelserummet.regler`:

enum class Avgiftstyp { OVERLATELSE, PANTSATTNING, ANDRAHAND }

fun avgiftFranPromille(prisbasbelopp: Long, promilleAvPrisbasbelopp: Int): Long
fun hogstaOverlatelseavgift(prisbasbelopp: Long, data: Map<String, StammaRad>): Long
fun hogstaPantsattningsavgift(prisbasbelopp: Long, data: Map<String, StammaRad>): Long
fun hogstaAndrahandsavgift(prisbasbelopp: Long): Long

- `avgiftFranPromille`: beräkna `prisbasbelopp * promilleAvPrisbasbelopp / 1000`, **avrundat nedåt** till hela kronor. För icke-negativa heltal räcker heltalsdivision; vid behov använd `floor` på `Double` och konvertera till `Long`.
- Negativt `prisbasbelopp` eller `promilleAvPrisbasbelopp < 0` → kasta `IllegalArgumentException`.
- `hogstaOverlatelseavgift`: läs promille från `data["referens_overlatelse_0"]!!.varde` (standard-CSV: 25), anropa `avgiftFranPromille(prisbasbelopp, promille.toInt())`. Saknas nyckeln → `IllegalArgumentException`.
- `hogstaPantsattningsavgift`: samma med `referens_pantsattning_0` (standard: 10).
- `hogstaAndrahandsavgift`: **100 promille** (10 % enligt BRL 7 kap. 14 §), utan CSV — anropa `avgiftFranPromille(prisbasbelopp, 100)`.

## Exempel
- `avgiftFranPromille(59200, 25)` → `1480L`
- `avgiftFranPromille(58800, 10)` → `588L`
- `hogstaAndrahandsavgift(59200)` → `5920L`
- `avgiftFranPromille(59201, 25)` → `1480L` (nedåt)

Bygg gärna `data` för överlåtelse/pantsättning med `lasStammaData` och rader som i `StammaDataLasTest` (referenspromille 25 respektive 10).

## Test som ska gå igenom
Skriv `OverlatelseavgiftBeraknaTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`) — **inte** `kotlin.test`. Minst tester som assertar exakt `Long`-värden för alla fyra exemplen ovan.

Kör:
`gradle :styrelserummet:testDebugUnitTest --tests "*OverlatelseavgiftBeraknaTest*"`

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
