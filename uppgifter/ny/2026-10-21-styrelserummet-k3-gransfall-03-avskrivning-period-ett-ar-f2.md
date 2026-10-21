# Uppgift: Avskrivning med nyttjandeperiod 1 år

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Ingen ny testfil skapades.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallEnArTest.kt` finns inte.
- Inga JUnit-tester som anropar `avskrivningFor` med `nyttjandeperiodAr = 1` och assertar mot uppgiftens belopp för 2023, 2024 och 2025 (med och utan restvärde).

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallEnArTest.kt (ny)
- (endast om ett test faller och implementationen behöver justeras) styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt
- styrelserummet/regler/K3.md

## Uppgift
Skapa `AvskrivningGransfallEnArTest` med tester för `avskrivningFor(k, ar)` när `k.nyttjandeperiodAr == 1`.

Hela det avskrivningsbara beloppet (`anskaffningsvarde - restvarde`) ska avskrivas **samma kalenderår** som `anskaffningsar`. Året före och året efter ska ge `0L`.

Testa både `restvarde = 0` och `restvarde > 0`.

Använd samma mönster som `AvskrivningTest.kt`: privat `komponent(...)`-hjälpare med `Komponent`, `Komponentgrupp`, `Sakerhet`. **JUnit 4** (`org.junit.Test`, `org.junit.Assert.assertEquals`) — **inte** `kotlin.test`.

Om testerna visar fel i `Avskrivning.kt` får du ändra den filen (lägg till den under «Ändra bara dessa filer» ovan). Håll total diff inom cirka 150 rader.

## Exempel
- Indata: anskaffningsvärde 50_000 kr, anskaffningsår 2024, nyttjandeperiod 1, restvärde 0. Förväntat: `avskrivningFor(k, 2024)` = 50_000; `2023` och `2025` = 0.
- Indata: samma men restvärde 5_000. Förväntat: `avskrivningFor(k, 2024)` = 45_000; `2023` och `2025` = 0.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallEnArTest"

## Gör inte
- Ändra `AvskrivningTest.kt`, modellklasser eller andra testfiler.
- Lägga till beroenden.
- Lägga filer i repots rot (sökvägen ska börja med `styrelserummet/`).
