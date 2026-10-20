# Uppgift: Telefon och e-post i fler format

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt

## Uppgift
Utöka `maskaPersonuppgifter` så att:

Telefon maskas till `[telefon]` för bland annat:
- `070 123 45 67`
- `0701-23 45 67`
- `0046 70 123 45 67`
- befintliga format (`070-123 45 67`, `+46 70 123 45 67`)

E-post med versaler och bindestreck i domänen maskas till `[e-post]`, t.ex. `Styrelse@Min-Brf.se`.

## Exempel
- Indata: `Ring 070 123 45 67` → `Ring [telefon]`
- Indata: `Mail Styrelse@Min-Brf.se` → `Mail [e-post]`
- Indata: `769612-3456` → oförändrat

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*MaskaPersonuppgifterTest.maskaTelefonOchEpost"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
