# Uppgift: Test av VerktygAnropsGrans (Styrelserummet-server)

## Vad som saknades
- Filen `styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbLankTest.kt` står inte under "Ändra bara dessa filer" (fel sökväg eller fel fil).

uppdrag: uppdrag/claude/2026-10-27-testtackning-kvall.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/VerktygAnropsGransTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/VerktygAnropsGrans.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt (`MAX_VERKTYG_ANROP_PER_IP_TIMME = 60`)

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertTrue`, `assertFalse`) i paketet `se.tmconnect.styrelserummet.server`. Skicka alltid tiden som `nuMs` så att testet inte beror på klockan. Använd konstanten `MAX_VERKTYG_ANROP_PER_IP_TIMME`, inte talet 60.

## Exempel
- Ny gräns: `farAnropa("1.2.3.4", 0)` är `true`.
- Efter 59 anrop (`registreraAnrop("1.2.3.4", t)` för t = 0 till 58): `farAnropa("1.2.3.4", 100)` är `true`.
- Efter 60 anrop (t = 0 till 59): `farAnropa("1.2.3.4", 100)` är `false`.
- Samma 60 anrop: `farAnropa("5.6.7.8", 100)` är `true` (gränsen gäller per IP).
- Samma 60 anrop: `farAnropa("1.2.3.4", 3_600_000)` är `true` (anropet vid t = 0 är en timme gammalt och räknas inte).
- Samma 60 anrop: `farAnropa("1.2.3.4", 3_599_999)` är `false`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*VerktygAnropsGransTest"

## Gör inte
- Ändra produktionskod eller andra filer än den nya testfilen.
- Lägga till beroenden eller använda `Thread.sleep`.

utfall: godkänd 2026-10-29
