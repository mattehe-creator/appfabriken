# Uppgift: Maska personuppgifter

uppdrag: uppdrag/vantar/2026-10-19-styrelserummet-underlag.md
försök: 1
beror-på: cursor:2026-10-19-styrelserummet-underlag

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Utkast.kt

## Uppgift
`fun maskaPersonuppgifter(text: String): String`

Personnummer (`ååmmdd-xxxx`, `ååååmmdd-xxxx`, med eller utan bindestreck) → `[personnummer]`.
E-post → `[e-post]`. Svenska telefonnummer (t.ex. `070-123 45 67`, `+46 70 123 45 67`, `08-123 456`) → `[telefon]`.

Organisationsnummer för bostadsrättsförening (t.ex. `769612-3456`), årtal och belopp ska **inte** maskas.

## Exempel
- `Kontakt 850709-1234 och 070-123 45 67` → personnummer och telefon maskade.
- `Org 769612-3456, år 2024, 1 500 000 kr` → oförändrat utom eventuell telefon.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*MaskaPersonuppgifterTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
