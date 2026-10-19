# Uppgift: Rensning efter 30 dagar

uppdrag: uppdrag/vantar/2026-10-19-styrelserummet-webb.md
försök: 1
beror-på: cursor:styrelserummet-webbtj-nst-k3-7da6

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/RensningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt

## Uppgift
Funktionen `skaRensas(sistaAktivitetEpochMs, nuMs)` ska returnera true när skillnaden är **mer än** 30 hela dygn, annars false.

## Exempel
- Aktivitet för 31 dygn sedan → true.
- Aktivitet för 29 dygn sedan → false.
- Samma tidsstämpel som `nuMs` → false.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*RensningTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
