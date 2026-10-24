# Uppgift: Test av mätarvärden som text

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/MatareTolkningTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareTolkning.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/BeloppTolkningTest.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `org.junit.Assert.assertEquals`, `assertNull`) för `tolkaMatareVardeTillTusendel(text: String): Long?` och `matareVardeTillFaltText(tusendel: Long): String`. Ett test per rad nedan går bra, eller några grupperade.

## Exempel
- `tolkaMatareVardeTillTusendel("12345")` → `12_345_000L`
- `tolkaMatareVardeTillTusendel("12345,6")` → `12_345_600L`
- `tolkaMatareVardeTillTusendel("1 234,567")` → `1_234_567L`
- `tolkaMatareVardeTillTusendel("0.05")` → `50L`
- `tolkaMatareVardeTillTusendel("1,2345")` → `null` (fyra decimaler)
- `tolkaMatareVardeTillTusendel("-3")` → `null`
- `tolkaMatareVardeTillTusendel("")` → `null`
- `tolkaMatareVardeTillTusendel("abc")` → `null`
- `tolkaMatareVardeTillTusendel("1000000000")` → `null` (över 999 999 999)
- `matareVardeTillFaltText(12_345_600L)` → `"12345,6"`
- `matareVardeTillFaltText(50L)` → `"0,05"`
- `matareVardeTillFaltText(12_345_000L)` → `"12345"`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*MatareTolkningTest"

## Gör inte
- Ändra andra filer.
- Ändra `MatareTolkning.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
