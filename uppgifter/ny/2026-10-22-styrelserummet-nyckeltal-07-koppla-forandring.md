# Uppgift: Koppla in förändring mot föregående år i nyckeltal (API)

uppdrag: uppdrag/2026-10-22-styrelserummet-verktyg-indexering-forandring.md
försök: 1
beror-på: 2026-10-22-styrelserummet-nyckeltal-06-forandring-f2.md

cursor:2026-10-22-styrelserummet-verktyg-indexering-forandring

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/VerktygApi.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/VerktygApiTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalForandring.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalRader.kt

## Uppgift
Lägg till valfritt `foregaendeAr: NyckeltalForegaendeDto?` i `NyckeltalRequest` (samma fält som års-DTO utan `ar`). Lägg till valfritt `forandring: String?` i `NyckeltalRadDto`. För det nyaste räkenskapsåret: beräkna råvärden i samma ordning som `nyckeltalRader`, anropa `formateraForandring(forandringTiondelsProcent(nu, fore))` per rad. Utan `foregaendeAr` ska svaret vara oförändrat (`forandring` null).

## Exempel
- Sparande 309 vs 280 kr/kvm → "+10,4 %".
- Räntekänslighet 9,0 vs 9,5 → "−5,3 %".
- Föregående yta 0 → "–" för kvm-nyckeltal.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*VerktygApiTest.nyckeltal*"

## Gör inte
- Ändra NyckeltalForandring.kt eller nyckeltal.html/js (egen uppgift).
- Lägga till beroenden.
