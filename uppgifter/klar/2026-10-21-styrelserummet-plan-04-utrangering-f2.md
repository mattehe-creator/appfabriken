# Uppgift: Utrangeringsbelopp vid utbyte

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-plan-03-hela-listan.md

## Vad som saknades
Försök 1 flyttade bara uppgiftsfilen till `uppgifter/pagar/` och skapade varken `Utbyte.kt` eller `UtbyteTest.kt`. Implementera funktionen och JUnit-tester enligt avsnitten nedan.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Utbyte.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/UtbyteTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt

## Uppgift
Skapa `Utbyte.kt` i paketet `se.tmconnect.styrelserummet.regler`:

fun utrangeringsbelopp(k: Komponent, utbytesAr: Int): Long

Importera `Komponent` från `se.tmconnect.styrelserummet.modell`.

När en komponent byts ut år `utbytesAr` kostnadsförs det som återstår av den gamla: bokfört värde vid slutet av året före utbytet.

- `utbytesAr <= k.anskaffningsar`: returnera hela `k.anskaffningsvarde`.
- Annars: returnera `bokfortVarde(k, utbytesAr - 1)` (samma paket; inkluderar eventuellt restvärde).

## Exempel
Bygg `Komponent` i testerna som i `BokfortVardeTest` (privat `komponent(...)`-hjälpare med `Komponentgrupp.STOMME`, `Sakerhet.HOG`, `kalla = "test"`).

| anskaffningsvärde | period | anskaffningsår | restvärde | utbytesår | utrangeringsbelopp |
|-------------------|--------|----------------|-----------|-----------|-------------------|
| 300 000 | 10 | 2020 | 0 | 2026 | 120 000 |
| 1 200 000 | 40 | 2010 | 0 | 2026 | 720 000 |
| 300 000 | 10 | 2020 | 0 | 2035 | 0 |
| 500 000 | 10 | 2018 | 50 000 | 2030 | 50 000 |
| 300 000 | 10 | 2020 | 0 | 2020 | 300 000 |

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*UtbyteTest"

Skriv minst fem `@Test` med `org.junit.Test` och `org.junit.Assert.assertEquals` som anropar `utrangeringsbelopp` för varje rad i tabellen ovan. Använd inte `kotlin.test`.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (hela implementationen Utbyte.kt och UtbyteTest.kt — försök 2 saknade kod)
