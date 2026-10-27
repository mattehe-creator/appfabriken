# Uppgift: Test av lasArsredovisning, byggnadsår och avskrivningsprincip (Styrelserummet)

uppdrag: uppdrag/claude/2026-10-27-testtackning-kvall.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ArsredovisningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Arsredovisning.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`, `assertNull`) i paketet `se.tmconnect.styrelserummet.underlag` för fälten `byggnadsar` och `avskrivningsprincip` i `lasArsredovisning(text: String): ArsredovisningUtdrag`. Testa inte `byggnadensVarde` och `ackumuleradeAvskrivningar` i den här uppgiften.

## Exempel
- `"Byggnadsår 1968"` ger `byggnadsar == 1968`.
- `"BYGGNADSÅR: 1972"` ger `1972` (stora bokstäver gör ingenting).
- `"Byggnadsår enligt taxeringen 1985"` ger `1985` (högst 20 tecken mellan ordet och året).
- `"Byggnadsår, se not 12 om fastighetens taxering och värde, 1990"` ger `byggnadsar == null` (regexen är `Byggnadsår[^\d]{0,20}(\d{4})`; "12" är bara två siffror och regexen hoppar inte över siffror).
- `"Avskrivningsprincip\nLinjär avskrivning över 100 år\nÖvrigt"` ger `avskrivningsprincip == "Linjär avskrivning över 100 år"`.
- `"Avskrivningsprincip för byggnader\n  Komponentavskrivning  \n"` ger `"Komponentavskrivning"` (trimmad).
- `"Ingenting här"` ger `byggnadsar == null` och `avskrivningsprincip == null`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ArsredovisningTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden.
