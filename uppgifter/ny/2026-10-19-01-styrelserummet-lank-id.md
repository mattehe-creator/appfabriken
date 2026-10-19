# Uppgift: Jobb-länk och id-validering

uppdrag: uppdrag/vantar/2026-10-19-styrelserummet-webb.md
försök: 1
beror-på: cursor:styrelserummet-webbtj-nst-k3-7da6

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbLank.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbLankTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/ApiTest.kt

## Uppgift
Säkerställ att `genereraJobbId()` returnerar 32 tecken hex (128 bitar) och att `arGiltigtJobbId(id)` bara godkänner samma format.

## Exempel
- `genereraJobbId()` två gånger ger olika strängar, båda matchar `[0-9a-f]{32}`.
- `arGiltigtJobbId("abc")` → false.
- `arGiltigtJobbId("0123456789abcdef0123456789abcdef")` → true.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbLankTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
