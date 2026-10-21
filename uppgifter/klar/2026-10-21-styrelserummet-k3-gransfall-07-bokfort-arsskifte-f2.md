# Uppgift: Bokfört värde vid årsskifte

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Filen `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallArsskifteTest.kt` skapades inte.
- `gradle :styrelserummet:test --tests "*BokfortVardeGransfallArsskifteTest"` kan inte köras utan testklassen; uppgiftens exempel (90_000 kr vid årsskifte 2024→2025) är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallArsskifteTest.kt (ny)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt (endast om testerna avslöjar fel)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt (endast om testerna avslöjar fel — **max en** av de två produktionsfilerna i samma PR)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt (privat `komponent(...)`, JUnit 4-mönster)

## Uppgift
Skapa testklassen `BokfortVardeGransfallArsskifteTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` / `assertTrue` — **inte** `kotlin.test`).

Verifiera att bokfört värde **minskar eller stannar på restvärde** när `arsslut` ökar med ett år, och att skillnaden mellan två på varandra följande årsslut (inom avskrivningsperioden) motsvarar `avskrivningFor(k, det senare året)`.

Testa särskilt årsskiftet **då anskaffningsåret börjar**: jämför `arsslut = anskaffningsår − 1` med `arsslut = anskaffningsår` (före första avskrivning vs efter första årets avskrivning).

Gärna minst ett test som loopar några år i följd och assertar att `bokfortVarde(k, ar+1) <= bokfortVarde(k, ar)` och att `bokfortVarde(k, ar) - bokfortVarde(k, ar+1) == avskrivningFor(k, ar+1)` när året `ar+1` ligger i avskrivningsperioden.

Vid fel i produktionskoden: rätta **högst en** av `BokfortVarde.kt` och `Avskrivning.kt`.

## Exempel (ska finnas som assert i tester)
- 90_000 kr, anskaffningsår 2025, nyttjandeperiod 3, restvärde 0:
  - `bokfortVarde(k, 2024)` = **90_000**L
  - `bokfortVarde(k, 2025)` = **60_000**L
  - skillnad **30_000**L = `avskrivningFor(k, 2025)`.

Använd **Long** för kronor i assert; kopiera `komponent(...)`-hjälparen från `BokfortVardeTest.kt`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*BokfortVardeGransfallArsskifteTest"

(Cursor kör `:styrelserummet:testDebugUnitTest` vid granskning.)

## Gör inte
- Ändra befintliga testfiler (`BokfortVardeTest.kt`, `AvskrivningTest.kt` m.fl.).
- Lägga till beroenden.
- Lägga filer i repots rot.
- Ändra både `BokfortVarde.kt` och `Avskrivning.kt` i samma PR.

utfall: godkänd 2026-10-21 (Cursor skrev hela testklassen; filen var tom)
