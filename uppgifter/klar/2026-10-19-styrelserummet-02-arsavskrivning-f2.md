# Uppgift: Årsavskrivning

## Vad som saknades (försök 1)
- Inga Kotlin-filer skapades. Endast uppgiftsfilen flyttades till `uppgifter/pagar/`.
- Du ska skapa **båda** filerna under modulen `styrelserummet` (inte i repots rot):
  - `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt`
  - `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt`
- Package: `se.tmconnect.styrelserummet.regler` (samma som `Validera.kt` och övriga regler).

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 2
beror-på: 2026-10-19-styrelserummet-01-validera-komponent.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Validera.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/ReglerTest.kt
- styrelserummet/regler/K3.md

## Uppgift
Implementera top-level-funktionen:

`fun avskrivningFor(k: Komponent, ar: Int): Long`

Linjär avskrivning från `anskaffningsar`, ett helt räkenskapsår per kalenderår.

- Avskrivningsbart belopp = `k.anskaffningsvarde - k.restvarde`.
- Normal årsbelopp = avskrivningsbart / `k.nyttjandeperiodAr`, avrundat till närmaste hela krona (`kotlin.math.roundToLong()` eller `Math.round(...).toLong()` på Double).
- **Sista avskrivningsåret** = `k.anskaffningsar + k.nyttjandeperiodAr - 1`. Det året ska returnera resten så att summan av `avskrivningFor` för alla år från anskaffningsår till och med sista året blir **exakt** avskrivningsbart belopp (samma idé som `fordela` lägger öresrest på en post).
- Före `k.anskaffningsar` och efter sista avskrivningsåret: returnera `0L`.

## Exempel
- Komponent: anskaffningsvärde 1_200_000, anskaffningsår 2010, nyttjandeperiod 40, restvärde 0 → `avskrivningFor(..., 2010)` … `(..., 2049)` = 30_000 varje år; `(..., 2009)` och `(..., 2050)` = 0.
- Komponent: 100_000, anskaffningsår 2020, period 3, restvärde 0 → 33_333 (2020), 33_333 (2021), 33_334 (2022); övriga år enligt reglerna ovan = 0.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningTest"

Skriv minst två `@Test` i `AvskrivningTest` som bygger `Komponent` (se privat `komponent()` i `ReglerTest.kt`) och använder **JUnit 4** (`org.junit.Test`, `org.junit.Assert.assertEquals`) — inte `kotlin.test`. Varje test ska `assertEquals` mot exakt de belopp som i exemplen ovan (inklusive 0 före/efter perioden för minst ett fall).

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-19 inget
